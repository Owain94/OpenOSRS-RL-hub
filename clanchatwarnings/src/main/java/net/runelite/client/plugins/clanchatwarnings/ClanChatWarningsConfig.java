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
		keyName = "warnedAndAlerted",
		name = "Alert On Warning",
		description = "Ping if player procs a warning.",
		position = 3
	)
	default boolean warnedAttention()
	{
		return true;
	}

	@ConfigItem(
		keyName = "Check on self join",
		name = "Check on Joining",
		description = "Runs the check when you join Clan Chat.",
		position = 4
	)
	default boolean selfCheck()
	{
		return false;
	}

	@ConfigItem(
		keyName = "Ping on self join",
		name = "Ping on Joining",
		description = "If \"Check on Joining\" is enabled, will ping if players on the list are in cc when you join Clan Chat.",
		position = 5
	)
	default boolean selfPing()
	{
		return false;
	}

	@ConfigItem(
		keyName = "Kicks",
		name = "Kick from Warning",
		description = "Changes message to support kicking players from warning.",
		position = 6
	)
	default boolean kickable()
	{
		return false;
	}
}