package net.runelite.client.plugins.masterfarmer;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Master Farmer",
	description = "Utilities that help when thieving master farmers",
	type = PluginType.SKILLING,
	enabledByDefault = false
)
@Slf4j
public class MasterFarmerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MasterFarmerOverlay masterfarmeroverlay;

	@Getter(AccessLevel.PACKAGE)
	private Instant lastTickUpdate;

	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, MasterFarmerNPC> masterFarmers = new HashMap<>();

	@Provides
	MasterFarmerConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MasterFarmerConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(masterfarmeroverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(masterfarmeroverlay);
		masterFarmers.clear();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null)
		{
			return;
		}

		if (npc.getName().equals("Master Farmer"))
		{
			masterFarmers.putIfAbsent(npc.getIndex(), new MasterFarmerNPC(npc));
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null)
		{
			return;
		}

		if (npc.getName().equals("Master Farmer"))
		{
			masterFarmers.remove(npc.getIndex());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN ||
			event.getGameState() == GameState.HOPPING)
		{
			masterFarmers.clear();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		lastTickUpdate = Instant.now();

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null)
			{
				continue;
			}

			if (npcName.equals("Master Farmer"))
			{
				final MasterFarmerNPC mf = masterFarmers.get(npc.getIndex());

				if (mf == null)
				{
					continue;
				}

				if (mf.getCurrentLocation().getX() != npc.getWorldLocation().getX() || mf.getCurrentLocation().getY() != npc.getWorldLocation().getY())
				{
					mf.setCurrentLocation(npc.getWorldLocation());
					mf.setTimeWithoutMoving(0);
					mf.setStoppedMovingTick(Instant.now());
					mf.setNpc(npc);
				}
				else
				{
					mf.setTimeWithoutMoving(lastTickUpdate.getEpochSecond() - mf.getStoppedMovingTick().getEpochSecond());
				}
			}
		}
	}
}