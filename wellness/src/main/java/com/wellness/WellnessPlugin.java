package com.wellness;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.time.Instant;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Wellness Notifications",
	description = "A plugin to add reminders that promote wellness and healthy gaming.",
	tags = {"wellness", "health", "eye", "reminder", "hydration"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class WellnessPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private WellnessConfig config;

	@Inject
	private Notifier notifier;

	private Instant eyeNotifyTime;
	private Instant postureNotifyTime;

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN && eyeNotifyTime == null && config.eyenotify())
		{
			eyeNotifyTime = Instant.now();
		}
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN && postureNotifyTime == null && config.posturenotify())
		{
			postureNotifyTime = Instant.now();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Player local = client.getLocalPlayer();
		final Duration eyeDuration = Duration.ofMinutes(config.eyeinterval());
		final Duration postureDuration = Duration.ofMinutes(config.postureinterval());
		
		if (config.eyenotify() && Instant.now().compareTo(eyeNotifyTime.plus(eyeDuration)) >= 0) {
		 	notifier.notify(local.getName() + " it has been " + config.eyeinterval() + " minutes. Consider taking a small 20 second break from looking at the screen.");
		 	eyeNotifyTime = Instant.now();
		}
		if (config.posturenotify() && Instant.now().compareTo(postureNotifyTime.plus(postureDuration)) >= 0) {
			notifier.notify("Posture check!!! It has been " + config.postureinterval() + " minutes");
			postureNotifyTime = Instant.now();
		}
	}

	@Provides
    WellnessConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WellnessConfig.class);
	}
}
