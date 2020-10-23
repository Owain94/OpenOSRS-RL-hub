package com.snip;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.FriendChatManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Chat Transcripts",
	description = "Creates transcripts and exports an image of chat to Screenshots folder.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class SnipPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private SnipConfig config;
	@Inject
	private FriendChatManager friendChatManager;
	private SnipPanel panel;
	private NavigationButton button;
	final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/227-0.png");

	@Override
	protected void startUp() throws Exception
	{
		panel = new SnipPanel(config,client,friendChatManager);
		button = NavigationButton.builder()
				.tooltip("Chat Transcripts")
				.icon(icon)
				.priority(config.location())
				.panel(panel)
				.build();
		clientToolbar.addNavigation(button);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(button);
	}
	@Provides
	SnipConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SnipConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("Chat Transcripts")) {
			clientToolbar.removeNavigation(button);
			button = NavigationButton.builder()
					.tooltip("Chat Transcripts")
					.icon(icon)
					.priority(config.location())
					.panel(panel)
					.build();
			clientToolbar.addNavigation(button);
		}
	}
}
