package net.runelite.client.plugins.crowdsourcing;

import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.crowdsourcing.cooking.CrowdsourcingCooking;
import net.runelite.client.plugins.crowdsourcing.dialogue.CrowdsourcingDialogue;
import net.runelite.client.plugins.crowdsourcing.movement.CrowdsourcingMovement;
import net.runelite.client.plugins.crowdsourcing.music.CrowdsourcingMusic;
import net.runelite.client.plugins.crowdsourcing.thieving.CrowdsourcingThieving;
import net.runelite.client.plugins.crowdsourcing.woodcutting.CrowdsourcingWoodcutting;
import net.runelite.client.plugins.crowdsourcing.zmi.CrowdsourcingZMI;
import net.runelite.client.task.Schedule;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "OSRS Wiki Crowdsourcing",
	description = "Help figure out skilling success rates, burn rates, more. See osrs.wiki/RS:CROWD",
	type = PluginType.UTILITY,
	enabledByDefault = false
)
@Slf4j
public class CrowdsourcingPlugin extends Plugin
{
	// Number of seconds to wait between trying to send data to the wiki.
	// NOTE: I wanted to make this a config entry but annotation parameters
	// need to be compile time constants.
	// I will either completely change the approach (stop using @Schedule) or
	// massively raise the time (~300 or 600 seconds) before making it widely
	// available. The current low value is for further testing from wiki editors.
	private static final int SECONDS_BETWEEN_UPLOADS = 300;

	@Inject
	private EventBus eventBus;

	@Inject
	CrowdsourcingManager manager;

	@Inject
	private CrowdsourcingCooking cooking;

	@Inject
	private CrowdsourcingDialogue dialogue;

	@Inject
	private CrowdsourcingMovement movement;

	@Inject
	private CrowdsourcingMusic music;

	@Inject
	private CrowdsourcingThieving thieving;

	@Inject
	private CrowdsourcingWoodcutting woodcutting;

	@Inject
	private CrowdsourcingZMI zmi;

	@Override
	protected void startUp()
	{
		eventBus.subscribe(ChatMessage.class, this, cooking::onChatMessage);
		eventBus.subscribe(MenuOptionClicked.class, this, cooking::onMenuOptionClicked);

		eventBus.subscribe(GameTick.class, this, dialogue::onGameTick);

		eventBus.subscribe(GameTick.class, this, movement::onGameTick);
		eventBus.subscribe(MenuOptionClicked.class, this, movement::onMenuOptionClicked);

		eventBus.subscribe(ChatMessage.class, this, music::onChatMessage);

		eventBus.subscribe(ChatMessage.class, this, woodcutting::onChatMessage);
		eventBus.subscribe(GameTick.class, this, woodcutting::onGameTick);
		eventBus.subscribe(MenuOptionClicked.class, this, woodcutting::onMenuOptionClicked);
		eventBus.subscribe(GameObjectDespawned.class, this, woodcutting::onGameObjectDespawned);

		eventBus.subscribe(MenuOptionClicked.class, this, zmi::onMenuOptionClicked);
		eventBus.subscribe(ChatMessage.class, this, zmi::onChatMessage);
		eventBus.subscribe(StatChanged.class, this, zmi::onStatChanged);
		eventBus.subscribe(ItemContainerChanged.class, this, zmi::onItemContainerChanged);

		eventBus.subscribe(ChatMessage.class, this, thieving::onChatMessage);
		eventBus.subscribe(MenuOptionClicked.class, this, thieving::onMenuOptionClicked);
	}

	@Override
	protected void shutDown()
	{
		eventBus.unregister(this);
	}

	@Schedule(
		period = SECONDS_BETWEEN_UPLOADS,
		unit = ChronoUnit.SECONDS,
		asynchronous = true
	)
	public void submitToAPI()
	{
		manager.submitToAPI();
	}
}