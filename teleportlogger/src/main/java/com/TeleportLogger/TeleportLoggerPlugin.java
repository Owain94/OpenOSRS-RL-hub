package com.TeleportLogger;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;


import java.awt.image.BufferedImage;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Teleport Logger",
	description = "Utility for tracking a list of all ingame teleportation methods.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)

public class TeleportLoggerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	private TeleportLoggerPanel panel;
	private NavigationButton navButton;
	private WorldPoint lastWorldPoint = null;
	private int ticksStill = 1;

	@Override
	public void startUp()
	{
		// Shamelessly copied from NotesPlugin
		panel = injector.getInstance(TeleportLoggerPanel.class);
		panel.init();

		// Hack to get around not having resources.
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "Transportation_icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Teleport Logger")
				.icon(icon)
				.priority(100)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		Player player = client.getLocalPlayer();
		if (player == null) {
			return;
		}
		WorldPoint newWorldPoint = player.getWorldLocation();
		if (lastWorldPoint == null) {
			lastWorldPoint = newWorldPoint;
			return;
		}
		int distance = newWorldPoint.distanceTo(lastWorldPoint);
		if (distance == 0) {
			ticksStill++;
			return;
		}
		if (distance > 2) {
			panel.appendText(lastWorldPoint + "\t" + newWorldPoint + "\t" + ticksStill);
		}
		ticksStill = 1;
		lastWorldPoint = newWorldPoint;
	}
}
