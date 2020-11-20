package com.trevor.greenscreen;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Green Screen",
	description = "Green screen around the local player, useful for making videos",
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
public class GreenScreenPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private GreenScreenConfig config;

	@Inject
	private GreenScreenOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Provides
	GreenScreenConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GreenScreenConfig.class);
	}
}
