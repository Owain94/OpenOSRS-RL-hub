package com.osrsstreamers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("osrsstreamers")
public interface OsrsStreamersConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "userAccessToken",
			name = "Token",
			description = "Twitch access token generated from https://rhoiyds.github.io/osrs-streamers/",
			secret = true
	)
	default String userAccessToken()
	{
		return "";
	}

	@ConfigItem(
			position = 2,
			keyName = "onlyShowStreamersWhoAreLive",
			name = "Only live streams",
			description = "Only highlight characters that are currently streaming",
			warning = "Have you correctly added a user access token above?"
	)
	default boolean onlyShowStreamersWhoAreLive()
	{
		return false;
	}

	@ConfigItem(
			position = 3,
			keyName = "showOnMinimap",
			name = "Minimap",
			description = "Show characters Twitch name on the minimap and recolor player's dot"
	)
	default boolean showOnMinimap()
	{
		return false;
	}

}
