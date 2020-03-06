package net.runelite.client.plugins.worldhighlighter;

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
		name = "Highlight clan member",
		description = "Attempts to highlight the clan member instead of the world, if it fails will default to world."
	)
	default boolean clanFirst()
	{
		return true;
	}
}