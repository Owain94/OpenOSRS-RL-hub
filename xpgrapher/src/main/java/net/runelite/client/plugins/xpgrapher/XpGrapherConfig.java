package net.runelite.client.plugins.xpgrapher;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;

@ConfigGroup("xpgrapher")
public interface XpGrapherConfig extends Config
{

	@ConfigTitleSection(
		name = "Skill Colors",
		description = "Set a color for each skill",
		position = 99,
		keyName = "skillColorSection"
	)
	default Title skillColorSection()
	{
		return new Title();
	}

	@ConfigItem(
		position = 0,
		keyName = "graphWidth",
		name = "Graph Width",
		description = "Configures the width of the graph."
	)
	default int graphWidth()
	{
		return 200;
	}

	@ConfigItem(
		position = 1,
		keyName = "graphHeight",
		name = "Graph Height",
		description = "Configures the height of the graph."
	)
	default int graphHeight()
	{
		return 100;
	}

	@ConfigItem(
		position = 2,
		keyName = "resetGraph",
		name = "Reset Graph",
		description = "Start over with all data. You will lose all saved graphs."
	)
	default boolean resetGraph()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "displayKey",
		name = "Display Key",
		description = "Use this to turn off the color reference key."
	)
	default boolean displayKey()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "displayXpRate",
		name = "Display XP/hr",
		description = ""
	)
	default boolean displayXpRate()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "maxSkillsToGraph",
		name = "# of Skills to Graph",
		description = ""
	)
	default int maxSkillsToGraph()
	{
		return 8;
	}

	@ConfigItem(
		position = 6,
		keyName = "graphColor",
		name = "Graph Line/Text Color",
		description = "The color of the graph lines, text and border"
	)
	default Color graphColor()
	{
		return new Color(56, 36, 24);
	}

	@ConfigItem(
		position = 7,
		keyName = "graphBackgroundColor",
		name = "Background Color",
		description = "The background color of the graph."
	)
	default Color graphBackgroundColor()
	{
		return new Color(132, 109, 71, 200);
	}

	@Range(
		min = 1,
		max = 100
	)
	@ConfigItem(
		position = 8,
		keyName = "graphBackgroundTransparency",
		name = "Background Transparency",
		description = "The background transparency."
	)
	default int graphBackgroundTransparency()
	{
		return 70;
	}


	@ConfigItem(
		position = 101,
		keyName = "attackColor",
		name = "Attack Color",
		description = "Color of the Attack graph line",
		titleSection = "skillColorSection"
	)
	default Color attackColor()
	{
		return new Color(79, 143, 35);
	}

	@ConfigItem(
		position = 9,
		keyName = "defenceColor",
		name = "Defence Color",
		description = "Color of the Defence graph line",
		titleSection = "skillColorSection"
	)
	default Color defenceColor()
	{
		return new Color(115, 115, 115);
	}

	@ConfigItem(
		position = 9,
		keyName = "strengthColor",
		name = "Strength Color",
		description = "Color of the Strength graph line",
		titleSection = "skillColorSection"
	)
	default Color strengthColor()
	{
		return new Color(115, 115, 115);
	}

	@ConfigItem(
		position = 9,
		keyName = "hitpointsColor",
		name = "Hitpoints Color",
		description = "Color of the Hitpoints graph line",
		titleSection = "skillColorSection"
	)
	default Color hitpointsColor()
	{
		return new Color(143, 35, 35);
	}

	@ConfigItem(
		position = 10,
		keyName = "rangedColor",
		name = "Ranged Color",
		description = "Color of the Ranged graph line",
		titleSection = "skillColorSection"
	)
	default Color rangedColor()
	{
		return new Color(106, 255, 0);
	}

	@ConfigItem(
		position = 11,
		keyName = "prayerColor",
		name = "Prayer Color",
		description = "Color of the Prayer graph line",
		titleSection = "skillColorSection"
	)
	default Color prayerColor()
	{
		return new Color(255, 212, 0);
	}

	@ConfigItem(
		position = 12,
		keyName = "magicColor",
		name = "Magic Color",
		description = "Color of the Magic graph line",
		titleSection = "skillColorSection"
	)
	default Color magicColor()
	{
		return new Color(0, 149, 255);
	}

	@ConfigItem(
		position = 13,
		keyName = "cookingColor",
		name = "Cooking Color",
		description = "Color of the Cooking graph line",
		titleSection = "skillColorSection"
	)
	default Color cookingColor()
	{
		return new Color(107, 35, 143);
	}

	@ConfigItem(
		position = 14,
		keyName = "woodcuttingColor",
		name = "Woodcutting Color",
		description = "Color of the Woodcutting graph line",
		titleSection = "skillColorSection"
	)
	default Color woodcuttingColor()
	{
		return new Color(35, 98, 143);
	}

	@ConfigItem(
		position = 15,
		keyName = "fletchingColor",
		name = "Fletching Color",
		description = "Color of the Fletching graph line",
		titleSection = "skillColorSection"
	)
	default Color fletchingColor()
	{
		return new Color(255, 127, 0);
	}

	@ConfigItem(
		position = 16,
		keyName = "fishingColor",
		name = "Fishing Color",
		description = "Color of the Fishing graph line",
		titleSection = "skillColorSection"
	)
	default Color fishingColor()
	{
		return new Color(231, 233, 185);
	}

	@ConfigItem(
		position = 17,
		keyName = "firemakingColor",
		name = "Firemaking Color",
		description = "Color of the Firemaking graph line",
		titleSection = "skillColorSection"
	)
	default Color firemakingColor()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
		position = 18,
		keyName = "craftingColor",
		name = "Crafting Color",
		description = "Color of the Crafting graph line",
		titleSection = "skillColorSection"
	)
	default Color craftingColor()
	{
		return new Color(255, 0, 170);
	}

	@ConfigItem(
		position = 19,
		keyName = "smithingColor",
		name = "Smithing Color",
		description = "Color of the Smithing graph line",
		titleSection = "skillColorSection"
	)
	default Color smithingColor()
	{
		return new Color(185, 237, 224);
	}

	@ConfigItem(
		position = 20,
		keyName = "miningColor",
		name = "Mining Color",
		description = "Color of the Mining graph line",
		titleSection = "skillColorSection"
	)
	default Color miningColor()
	{
		return new Color(204, 204, 204);
	}

	@ConfigItem(
		position = 21,
		keyName = "herbloreColor",
		name = "Herblore Color",
		description = "Color of the Herblore graph line",
		titleSection = "skillColorSection"
	)
	default Color herbloreColor()
	{
		return new Color(191, 255, 0);
	}

	@ConfigItem(
		position = 22,
		keyName = "agilityColor",
		name = "Agility Color",
		description = "Color of the Agility graph line",
		titleSection = "skillColorSection"
	)
	default Color agilityColor()
	{
		return new Color(185, 215, 237);
	}

	@ConfigItem(
		position = 23,
		keyName = "thievingColor",
		name = "Thieving Color",
		description = "Color of the Thieving graph line",
		titleSection = "skillColorSection"
	)
	default Color thievingColor()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
		position = 24,
		keyName = "slayerColor",
		name = "Slayer Color",
		description = "Color of the Slayer graph line",
		titleSection = "skillColorSection"
	)
	default Color slayerColor()
	{
		return new Color(220, 185, 237);
	}

	@ConfigItem(
		position = 25,
		keyName = "farmingColor",
		name = "Farming Color",
		description = "Color of the Farming graph line",
		titleSection = "skillColorSection"
	)
	default Color farmingColor()
	{
		return new Color(0, 234, 255);
	}

	@ConfigItem(
		position = 26,
		keyName = "runecraftColor",
		name = "Runecraft Color",
		description = "Color of the Runecraft graph line",
		titleSection = "skillColorSection"
	)
	default Color runecraftColor()
	{
		return new Color(170, 0, 255);
	}

	@ConfigItem(
		position = 27,
		keyName = "hunterColor",
		name = "Hunter Color",
		description = "Color of the Hunter graph line",
		titleSection = "skillColorSection"
	)
	default Color hunterColor()
	{
		return new Color(237, 185, 185);
	}

	@ConfigItem(
		position = 28,
		keyName = "constructionColor",
		name = "Construction Color",
		description = "Color of the Construction graph line",
		titleSection = "skillColorSection"
	)
	default Color constructionColor()
	{
		return new Color(220, 190, 255);
	}

}
