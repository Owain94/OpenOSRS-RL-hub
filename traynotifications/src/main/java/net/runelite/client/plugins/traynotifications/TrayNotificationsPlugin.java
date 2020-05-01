package net.runelite.client.plugins.traynotifications;

import com.google.inject.Provides;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.TrayIcon;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientUI;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Tray Notifications",
	description = "Allows for custom tray notifications. No longer do you have to deal with terrible windows tray notifications. Original author: abex",
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class TrayNotificationsPlugin extends Plugin
{
	@Inject
	private TrayNotificationsConfig config;

	@Inject
	private ClientUI clientUI;

	@Inject
	private RuneLiteConfig runeLiteConfig;

	public enum MonitorConfig
	{
		CURRENT_MONITOR,
		MAIN_MONITOR
	}

	@Subscribe
	public void onNotificationFired(NotificationFired event)
	{
		if (!runeLiteConfig.sendNotificationsWhenFocused() && clientUI.isFocused())
		{
			return;
		}

		SwingUtilities.invokeLater(() -> sendCustomNotification(RuneLiteProperties.getTitle(), event.getMessage(), event.getType()));
	}

	private void sendCustomNotification(
		final String title,
		final String message,
		final TrayIcon.MessageType type
	)
	{
		GraphicsConfiguration graphicsConfiguration;

		if (config.monitor() == MonitorConfig.CURRENT_MONITOR)
		{
			graphicsConfiguration = clientUI.getGraphicsConfiguration();
		}
		else
		{
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice device = environment.getDefaultScreenDevice();
			graphicsConfiguration = device.getDefaultConfiguration();
		}

		CustomNotification.sendCustomNotification(title, message, type, graphicsConfiguration.getBounds());
	}

	@Provides
	TrayNotificationsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TrayNotificationsConfig.class);
	}
}