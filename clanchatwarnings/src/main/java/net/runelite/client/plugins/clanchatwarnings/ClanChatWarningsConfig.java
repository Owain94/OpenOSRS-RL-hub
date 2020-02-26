package net.runelite.client.plugins.clanchatwarnings;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ClanChatPlus")
public interface ClanChatWarningsConfig extends Config
{
	@ConfigItem(
		keyName = "notifiedPlayer",
		name = "Players Warnings",
		description = "List of players you want to be warned of on joining. Supports notes by adding '~' after name. Separate with commas.",
		position = 0
	)
	default String warnPlayers()
	{
		return "";
	}

	@ConfigItem(
		keyName = "notifiedMass",
		name = "Regex Warnings",
		description = "Regex warnings for players joining Clan Chat. Supports notes by adding '~' after name. Separate with new lines.",
		position = 1
	)
	default String warnings()
	{
		return "";
	}

	@ConfigItem(
		keyName = "exemptPlayers",
		name = "Exempt Players",
		description = "Players to be ignored when joining Clan Chat.",
		position = 2
	)
	default String exemptPlayers()
	{
		return "";
	}

	@ConfigItem(
		keyName = "cooldown",
		name = "Cooldown",
		description = "Cooldown, in seconds, before you will be notified of a player joining again.",
		position = 3
	)
	default int cooldown()
	{
		return 30;
	}

	@ConfigItem(
		keyName = "warnedAndAlerted",
		name = "Alert On Warning",
		description = "Ping if player procs a warning.",
		position = 4
	)
	default boolean warnedAttention()
	{
		return true;
	}

	@ConfigItem(
		keyName = "Check on self join",
		name = "Check on Joining",
		description = "Runs the check when you join Clan Chat.",
		position = 5
	)
	default boolean selfCheck()
	{
		return false;
	}

	@ConfigItem(
		keyName = "Ping on self join",
		name = "Ping on Joining",
		description = "If \"Check on Joining\" is enabled, will ping if players on the list are in cc when you join Clan Chat.",
		position = 6
	)
	default boolean selfPing()
	{
		return false;
	}

	@ConfigItem(
		keyName = "Kicks",
		name = "Kick from Warning",
		description = "Changes message to support kicking players from warning.",
		position = 7
	)
	default boolean kickable()
	{
		return false;
	}

	@ConfigItem(
		keyName = "Tracker",
		name = "Enable Tracker",
		description = "Adds a right click option that will track, and notify via chat message, when a player leaves Clan Chat after saying \"Tracker Keyword\".",
		position = 8
	)
	default boolean track()
	{
		return false;
	}

	@ConfigItem(
		keyName = "Tracker Length",
		name = "Tracker Length",
		description = "How long to track a Player, in seconds.",
		position = 9
	)
	default int trackerLength()
	{
		return 30;
	}

	@ConfigItem(
		keyName = "Tracker Ping",
		name = "Ping on Tracked Leave",
		description = "Sends a ping if a tracked player leaves.",
		position = 10
	)
	default boolean trackerPing()
	{
		return false;
	}
}