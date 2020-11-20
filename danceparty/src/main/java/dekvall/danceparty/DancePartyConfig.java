package dekvall.danceparty;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("danceparty")
public interface DancePartyConfig extends Config
{
	@ConfigItem(
			keyName = "workoutMode",
			name = "Workout Mode",
			description = "#1 OSRS fitness inspiration",
			position = 0
	)
	default boolean workoutMode()
	{
		return false;
	}

	@ConfigItem(
			keyName = "disableInPvp",
			name = "Disable in PvP",
			description = "Disable dance moves when entering dangerous situations :(",
			position = 1
	)
	default boolean disableInPvp()
	{
		return false;
	}

	@ConfigTitleSection(
		keyName = "conditionalTitle",
			name = "Only Dance When...",
			description = "If any of these are enabled, people will only dance if you've accomplished that thing!",
			position = 2
	)
	default Title conditionalTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "partyOnLevelup",
			name = "...You Level Up",
			description = "Players will dance when you level up",
			titleSection = "conditionalTitle",
			position = 0
	)
	default boolean partyOnLevelup()
	{
		return false;
	}

	@ConfigItem(
			keyName = "partyOnBossKill",
			name = "...You Get A Boss Kill",
			description = "Players will dance when you get a boss kill.",
			titleSection = "conditionalTitle",
			position = 1
	)
	default boolean partyOnBossKill()
	{
		return false;
	}

	@ConfigItem(
			keyName = "partyOnRaidDone",
			name = "...You Finish A Raid",
			description = "Players will dance when you get a finish a raid or complete barrows.",
			titleSection = "conditionalTitle",
			position = 2
	)
	default boolean partyOnRaidDone()
	{
		return false;
	}

	@ConfigItem(
			keyName = "partyOnPetDrop",
			name = "...You Get A Pet Drop",
			description = "Players will dance when you get a pet drop.",
			titleSection = "conditionalTitle",
			position = 3
	)
	default boolean partyOnPetDrop()
	{
		return false;
	}

}
