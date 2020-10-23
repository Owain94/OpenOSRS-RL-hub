package com.TrayIndicators;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("Tray Indicators")
public interface TrayIndicatorsConfig extends Config {
	@ConfigItem(
			keyName = "health",
			name = "Enable Health",
			description = "",
			position = 1
	)
	default boolean health()
	{
		return true;
	}

	@ConfigItem(
			keyName = "healthColor",
			name = "",
			description = "",
			position = 2
	)
	default Color healthColor()
	{
		return Color.decode("#ff0000");
	}

	@ConfigItem(
			keyName = "prayer",
			name = "Enable Prayer",
			description = "",
			position = 3
	)
	default boolean prayer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "prayerColor",
			name = "",
			description = "",
			position = 4
	)
	default Color prayerColor()
	{
		return Color.decode("#00f3ff");
	}

	@ConfigItem(
			keyName = "absorption",
			name = "Enable Absorption",
			description = "",
			position = 5
	)
	default boolean absorption()
	{
		return true;
	}

	@ConfigItem(
			keyName = "absorptionColor",
			name = "",
			description = "",
			position = 6
	)

	default Color absorptionColor()
	{
		return Color.decode("#ffffff");
	}

}