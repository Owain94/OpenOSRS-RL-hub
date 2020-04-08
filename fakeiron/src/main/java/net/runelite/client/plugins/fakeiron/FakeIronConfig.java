package net.runelite.client.plugins.fakeiron;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("fakeiron")
public interface FakeIronConfig extends Config
{

	@ConfigItem(
		keyName = "icon",
		name = "Icon Color",
		description = ""
	)
	default FakeIronIcons icon()
	{
		return FakeIronIcons.GREEN;
	}

	@ConfigItem(
		keyName = "icon",
		name = "Icon Color",
		description = ""
	)
	void icon(FakeIronIcons icon);

	@ConfigItem(
		keyName = "players",
		name = "Other Players",
		description = "List of other players to show icon for, newline seperated"
	)
	default String otherPlayers()
	{
		return "";
	}

}