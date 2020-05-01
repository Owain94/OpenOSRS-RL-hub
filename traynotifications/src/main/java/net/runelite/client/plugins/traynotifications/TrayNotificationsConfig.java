package net.runelite.client.plugins.traynotifications;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("traynotifications")
public interface TrayNotificationsConfig extends Config
{
	@ConfigItem(
		keyName = "monitor",
		name = "Monitor",
		description = "Which monitor do you want to display the notification on"
	)
	default TrayNotificationsPlugin.MonitorConfig monitor()
	{
		return TrayNotificationsPlugin.MonitorConfig.CURRENT_MONITOR;
	}
}