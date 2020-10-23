package com.thenorsepantheon.groupironman;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("groupironman")
public interface GroupIronManConfig extends Config
{
	@ConfigItem(
		keyName = "ultimate",
		name = "Ultimate?",
		description = "You think you can face the challenges of having no bank?"
	)
	default boolean ultimate()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "groupMembers",
		name = "Group Members",
		description = "Names of other members of the group, one per line"
	)
	default String groupMembers()
	{
		return "";
	}

	@ConfigItem(
		position = 3,
		keyName = "iconHueOffset",
		name = "Icon Hue Offset",
		description = "Set the hue offset for iron man icons"
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int iconHueOffset()
	{
		return 50;
	}
}
