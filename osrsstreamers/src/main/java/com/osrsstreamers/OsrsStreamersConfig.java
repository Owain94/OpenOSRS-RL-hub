package com.osrsstreamers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("osrsstreamers")
public interface OsrsStreamersConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "checkIfLive",
			name = "Display if live",
			description = "Display whether any detected streamers are currently live"
	)
	default boolean checkIfLive()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "userAccessToken",
		name = "Token",
		description = "Twitch access token generated from https://rhoiyds.github.io/osrs-streamers/"
	)
	default String userAccessToken()
	{
		return "";
	}
}
