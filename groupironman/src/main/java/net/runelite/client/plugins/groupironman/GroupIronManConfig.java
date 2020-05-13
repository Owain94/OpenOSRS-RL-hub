package net.runelite.client.plugins.groupironman;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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
}