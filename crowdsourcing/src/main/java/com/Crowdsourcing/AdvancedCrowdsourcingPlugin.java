package com.Crowdsourcing;

import com.Crowdsourcing.varbits.CrowdsourcingVarbits;
import javax.inject.Inject;
import java.time.temporal.ChronoUnit;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.task.Schedule;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "OSRS Wiki Crowdsourcing (advanced)",
	description = "Help figure out varbits, quest states, and more. See osrs.wiki/RS:CROWD",
	type = PluginType.UTILITY,
	enabledByDefault = false
)
public class AdvancedCrowdsourcingPlugin extends Plugin
{
	// Number of seconds to wait between trying to send data to the wiki.
	// NOTE: I wanted to make this a config entry but annotation parameters
	// need to be compile time constants.
	private static final int SECONDS_BETWEEN_UPLOADS = 300;

	@Inject
	private EventBus eventBus;

	@Inject
	CrowdsourcingManager manager;

	@Inject
	private CrowdsourcingVarbits varbits;

	@Override
	protected void startUp()
	{
		eventBus.subscribe(GameStateChanged.class, varbits.getClass(), varbits::onGameStateChanged);
		eventBus.subscribe(VarbitChanged.class, varbits.getClass(), varbits::onVarbitChanged);

		varbits.startUp();
	}

	@Override
	protected void shutDown()
	{
		eventBus.unregister(varbits.getClass());

		varbits.shutDown();
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
