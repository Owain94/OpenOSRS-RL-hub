package com.osrsstreamers;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.inject.Named;

import com.osrsstreamers.handler.StreamerHandler;
import com.osrsstreamers.handler.StreamingPlayerMinimapOverlay;
import com.osrsstreamers.handler.StreamingPlayerOverlay;
import com.osrsstreamers.ui.OsrsStreamersPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.CommandExecuted;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "OSRS Streamers",
	description = "See which players in-game are usually/currently streamed on Twitch from a list of verified streamers.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class OsrsStreamersPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OsrsStreamersConfig config;

	@Inject
	private EventBus eventBus;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StreamingPlayerOverlay streamingPlayerOverlay;

	@Inject
	private StreamingPlayerMinimapOverlay streamingPlayerMinimapOverlay;

	@Inject
	private ClientToolbar clientToolbar;

	public StreamerHandler streamerHandler;

	@Override
	protected void startUp()
	{
		startHandlingTwitchStreams();
			OsrsStreamersPluginPanel panel = new OsrsStreamersPluginPanel(this);
			final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/icon.png");

			NavigationButton navButton = NavigationButton.builder()
					.tooltip("OSRS Streamers")
					.icon(icon)
					.priority(7)
					.panel(panel)
					.build();

			clientToolbar.addNavigation(navButton);

	}

	@Override
	protected void shutDown()
	{
		stopHandlingTwitchStreams();
	}

	private void stopHandlingTwitchStreams() {
		if (Objects.nonNull(streamerHandler)) {
			eventBus.unregister(streamerHandler);
			streamerHandler = null;
			this.streamingPlayerOverlay.streamerHandler = null;
			this.streamingPlayerMinimapOverlay.streamerHandler = null;
			overlayManager.remove(streamingPlayerOverlay);
			overlayManager.remove(streamingPlayerMinimapOverlay);
		}
	}

	private void startHandlingTwitchStreams() {
		if (Objects.isNull(streamerHandler)) {
			streamerHandler = new StreamerHandler(client, config, eventBus);
			this.streamingPlayerOverlay.streamerHandler = streamerHandler;
			this.streamingPlayerMinimapOverlay.streamerHandler = streamerHandler;
			overlayManager.add(streamingPlayerOverlay);
			overlayManager.add(streamingPlayerMinimapOverlay);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("osrsstreamers"))
		{
			return;
		}

		stopHandlingTwitchStreams();
		startHandlingTwitchStreams();
		
	}

	@Provides
	OsrsStreamersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OsrsStreamersConfig.class);
	}

	@Schedule(period = 2, unit = ChronoUnit.SECONDS, asynchronous = true)
	public void checkNearbyPlayers() {
		if (Objects.nonNull(this.streamerHandler)) {
			this.streamerHandler.removeOldNearbyPlayers();
			if (Objects.nonNull(config.userAccessToken())) {
				this.streamerHandler.fetchStreamStatusOfUndeterminedStreamers();
			}
		}
	}
}
