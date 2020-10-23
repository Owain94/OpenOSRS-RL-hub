package com.highlight;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("World Highlighter")
public interface HighlightConfig extends Config
{
	@ConfigItem(
		keyName = "message",
		name = "Highlight Message",
		description = "Adds a message to tell you which world is highlighted."
	)
	default boolean message()
	{
		return true;
	}
	@ConfigItem(
			keyName = "highlightClan",
			name = "Highlight in friends chat",
			description = "Attempts to highlight the player in Friends chat list instead of the world, if it fails will default to world."
	)
	default boolean clanFirst()
	{
		return true;
	}

}
