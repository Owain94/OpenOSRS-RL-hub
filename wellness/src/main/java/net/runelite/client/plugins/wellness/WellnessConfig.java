package net.runelite.client.plugins.wellness;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("wellness")
public interface WellnessConfig extends Config
{
	@ConfigItem(
		keyName = "eyenotify",
		name = "Eye Strain Notifications",
		description = "Configures if eye strain notifications are enabled",
		position = 1
	)
	default boolean eyenotify()
	{
		return true;
	}

	@ConfigItem(
		keyName = "eyeinterval",
		name = "Eye Strain Notify Interval",
		description = "The time between each notification",
		position = 2
	)
	@Units(Units.MINUTES)
	default int eyeinterval()
	{
		return 20;
	}

	@ConfigItem(
		keyName = "posturenotify",
		name = "Posture Check Notifications",
		description = "Configures if posture check notifications are enabled",
		position = 3
	)
	default boolean posturenotify()
	{
		return true;
	}

	@ConfigItem(
		keyName = "postureinterval",
		name = "Posture Check Notify Interval",
		description = "The time between each notification",
		position = 4
	)
	@Units(Units.MINUTES)
	default int postureinterval()
	{
		return 20;
	}
}