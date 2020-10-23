package com.playtime;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("play-time")
public interface PlayTimeConfig extends Config
{
	@ConfigItem(
			keyName = "showAverages",
			name = "Show Averages",
			description = "Show average values for week and month."
	)
	default boolean showAverages()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showSeconds",
			name = "Show Seconds",
			description = "Show seconds on times"
	)
	default boolean showSeconds()
	{
		return false;
	}
}
