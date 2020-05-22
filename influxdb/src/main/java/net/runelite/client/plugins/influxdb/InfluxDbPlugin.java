package net.runelite.client.plugins.influxdb;

import com.google.inject.Provides;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.influxdb.write.InfluxWriter;
import net.runelite.client.task.Schedule;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "InfluxDB",
	description = "Saves statistics to InfluxDB",
	tags = {"experience", "levels", "stats"},
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class InfluxDbPlugin extends Plugin
{
	private int flushTaskInterval;
	private ScheduledFuture<?> flushTask;

	@Provides
	InfluxDbConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InfluxDbConfig.class);
	}

	@Inject
	private InfluxWriter writer;

	@Inject
	private InfluxDbConfig config;

	@Inject
	private Client client;

	@Inject
	private MeasurementCreator measurer;

	@Inject
	private ScheduledExecutorService executor;

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		if (!config.writeXp())
		{
			return;
		}

		writer.submit(measurer.createXpMeasurement(statChanged.getSkill()));
		if (statChanged.getSkill() != Skill.OVERALL)
		{
			writer.submit(measurer.createXpMeasurement(Skill.OVERALL));
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN && config.writeXp())
		{
			for (Skill s : Skill.values())
			{
				writer.submit(measurer.createXpMeasurement(s));
			}
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (!config.writeBankValue())
		{
			return;
		}
		ItemContainer container = event.getItemContainer();
		if (container == null)
		{
			return;
		}
		Item[] items = container.getItems();

		InventoryID id = null;
		for (InventoryID val : InventoryID.values())
		{
			if (val.getId() == event.getContainerId())
			{
				id = val;
				break;
			}
		}
		if (id != InventoryID.BANK && id != InventoryID.SEED_VAULT)
		{
			return;
		}
		if (writer.isBlocked(measurer.createItemSeries(id, MeasurementCreator.InvValueType.HA)))
		{
			return;
		}
		measurer.createItemMeasurements(id, items).forEach(writer::submit);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (config.writeSelfLoc())
		{
			writer.submit(measurer.createSelfLocMeasurement());
		}
		if (config.writeSelfMeta())
		{
			writer.submit(measurer.createSelfMeasurement());
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged changed)
	{
		if (!config.writeKillCount())
		{
			return;
		}
		// Piggyback on the chat commands plugin to record kill count to avoid
		// duplicating the complex logic to keep up to date on kill counts
		if (!changed.getGroup().startsWith("killcount.") || changed.getNewValue() == null)
		{
			return;
		}
		if (!changed.getGroup().equals("killcount." + client.getUsername().toLowerCase()))
		{
			return;
		}
		try
		{
			String boss = changed.getKey();
			int kc = Integer.parseInt(changed.getNewValue());
			writer.submit(measurer.createKillCountMeasurement(boss, kc));
		}
		catch (NumberFormatException ex)
		{
			log.debug("Failed to parse KC for boss {} value {}",
				changed.getKey(),
				changed.getNewValue(),
				ex);
		}
	}

	@Schedule(period = 15, unit = ChronoUnit.SECONDS, asynchronous = true)
	public void flush()
	{
		writer.flush();
		int currentInterval = config.writeIntervalSeconds();
		if (currentInterval != flushTaskInterval)
		{
			unscheduleFlush();
			scheduleFlush();
		}
	}

	private synchronized void scheduleFlush()
	{
		flushTaskInterval = config.writeIntervalSeconds();
		this.flushTask = executor.scheduleWithFixedDelay(this::flush, flushTaskInterval, flushTaskInterval, TimeUnit.SECONDS);
	}

	private synchronized void unscheduleFlush()
	{
		if (flushTask != null)
		{
			flushTask.cancel(false);
			flushTask = null;
		}
	}

	@Override
	protected void startUp()
	{
		scheduleFlush();
	}

	@Override
	protected void shutDown()
	{
		unscheduleFlush();
	}
}