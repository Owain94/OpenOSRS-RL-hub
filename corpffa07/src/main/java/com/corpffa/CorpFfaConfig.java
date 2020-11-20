package com.corpffa;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;

@ConfigGroup("corpFfa")
public interface CorpFfaConfig extends Config
{
	@ConfigTitleSection(
		keyName = "generalTitle",
		name = "General",
		position = 0,
		description = "General"
	)
	default Title generalTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "bannedGearTitle",
		name = "Banned Gear",
		position = 1,
		description = "Banned Gear"
	)
	default Title bannedGearTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "hidingTitle",
		name = "Hiding",
		position = 2,
		description = "Hiding"
	)
	default Title hidingTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "colorsTitle",
		name = "Colors",
		position = 3,
		description = "Colors"
	)
	default Title colorsTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "screenshotTitle",
		name = "Screenshots",
		position = 4,
		description = "Screenshots"
	)
	default Title screenshotTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "rangerColor",
		name = "Ranger Color",
		description = "The color to show rangers in",
		titleSection = "colorsTitle"
	)
	default Color rangerColor()
	{
		return Color.PINK;
	}

	@ConfigItem(
		keyName = "cheaterColor",
		name = "Cheater Color",
		description = "The color to show cheaters in",
		titleSection = "colorsTitle"
	)
	default Color cheaterColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "goodColor",
		name = "Good Player Color",
		description = "The color to show good players in",
		titleSection = "colorsTitle"
	)
	default Color goodColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "defaultColor",
		name = "Default Color",
		description = "The default color to use",
		titleSection = "colorsTitle"
	)
	default Color defaultColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = "gonePlayerColor",
		name = "Teled Player Color",
		description = "The color to use for players that have teleported/died/despawned",
		titleSection = "colorsTitle"
	)
	default Color gonePlayerColor()
	{
		return Color.BLACK;
	}

	@ConfigItem(
		keyName = "playerCountColor",
		name = "Player Count Color",
		description = "The color to show the player count in",
		titleSection = "colorsTitle"
	)
	default Color playerCountColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "taggedPlayerColor",
		name = "Tagged Player Color",
		description = "The color to show tagged players in",
		titleSection = "colorsTitle"
	)
	default Color taggedPlayerColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "hideGoodPlayers",
		name = "Hide Good Players",
		description = "Should the plugin hide players that have 2 specced and have allowed gear?",
		titleSection = "hidingTitle"
	)
	default boolean hideGoodPlayers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideRangers",
		name = "Hide Rangers",
		description = "Should rangers be shown in the player list?",
		titleSection = "hidingTitle"
	)
	default boolean hideRangers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hidePlayerCount",
		name = "Hide Player Count",
		description = "Should the player count be hidden?",
		titleSection = "hidingTitle"
	)
	default boolean hidePlayerCount()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideTeledPlayers",
		name = "Hide Teled Players",
		description = "Should teled/dead players be hidden in the player list?",
		titleSection = "hidingTitle"
	)
	default boolean hideTeledPlayers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "groupRangers",
		name = "Group Rangers",
		description = "Should the rangers be shown together in the player list?",
		titleSection = "generalTitle"
	)
	default boolean groupRangers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "splitRangersInPlayerCount",
		name = "Split Rangers In Player Count",
		description = "Should the rangers count be shown separately in the player count e.g 20 (+2)?",
		titleSection = "generalTitle"
	)
	default boolean splitRangersInPlayerCount()
	{
		return false;
	}

	@Range(
		min = 0,
		max = 9
	)
	@ConfigItem(
		keyName = "bannedItemCountToShow",
		name = "Max Shown Items",
		description = "How many banned items should be shown on a player?",
		titleSection = "bannedGearTitle"
	)
	default int bannedItemCountToShow()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "taggedPlayers",
		name = "Tagged Players",
		description = "A list of player names that should be tagged. Separate names with commas (,)",
		titleSection = "generalTitle"
	)
	default String taggedPlayers()
	{
		return "";
	}

	@ConfigItem(
		keyName = "saveToClipboard",
		name = "Copy To Clipboard",
		description = "Should screenshots also be saved to the clipboard?",
		titleSection = "screenshotTitle"
	)
	default boolean saveToClipboard()
	{
		return false;
	}

	@ConfigItem(
		keyName = "captureOnCrash",
		name = "Screenshot Crashers",
		description = "Should screenshots be taken of crashers?",
		titleSection = "screenshotTitle"
	)
	default boolean captureOnCrash()
	{
		return false;
	}

	@ConfigItem(
		keyName = "nofifyOnCapture",
		name = "Notify On Screenshot",
		description = "Should a notification be given when a screenshot is taken?",
		titleSection = "screenshotTitle"
	)
	default boolean nofifyOnCapture()
	{
		return false;
	}

}
