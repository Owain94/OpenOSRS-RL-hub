package com.ItemRarity;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("itemRarity")
public interface ItemRarityConfig extends Config
{
	@ConfigItem(keyName = "a-commonItemValue", name = "Common GE Value", description = "Max Value of a Common Item")
	default int getCommonItemValue()
	{
		return 1000;
	}

	@Alpha
	@ConfigItem(keyName = "a-commonItemColor", name = "Common Item Color", description = "Color of a Common Item")
	default Color getCommonItemColor()
	{
		return new Color(0,0,0,0.0f);
	}

	@ConfigItem(keyName = "b-uncommonItemValue", name = "Uncommon GE Value", description = "Max Value of a Uncommon Item")
	default int getUncommonItemValue()
	{
		return 12500;
	}

	@Alpha
	@ConfigItem(keyName = "b-uncommonItemColor", name = "Uncommon Item Color", description = "Color of a Uncommon Item")
	default Color getUncommonItemColor()
	{
		return new Color(0, 240 / 255f, 0, 110 / 255f);
	}

	@ConfigItem(keyName = "c-rareItemValue", name = "Rare GE Value", description = "Max Value of a Rare Item")
	default int getRareItemValue()
	{
		return 125000;
	}

	@Alpha
	@ConfigItem(keyName = "c-rareItemColor", name = "Rare Item Color", description = "Color of a Rare Item")
	default Color getRareItemColor()
	{
		return new Color(0, 100 / 255f, 240 / 255f, 195 / 255f);
	}

	@ConfigItem(keyName = "d-epicItemValue", name = "Epic GE Value", description = "Max Value of a Epic Item")
	default int getEpicItemValue()
	{
		return 1800000;
	}

	@Alpha
	@ConfigItem(keyName = "d-epicItemColor", name = "Epic Item Color", description = "Color of a Epic Item")
	default Color getEpicItemColor()
	{
		return new Color(160 / 255f, 50 / 255f, 240 / 255f, 195 / 255f);
	}

	@ConfigItem(keyName = "e-legendaryItemValue", name = "Legendary GE Value", description = "Max Value of a Legendary Item")
	default int getLegendaryItemValue()
	{
		return Integer.MAX_VALUE;
	}

	@Alpha
	@ConfigItem(keyName = "e-legendaryItemColor", name = "Legendary Item Color", description = "Color of a Legendary Item")
	default Color getLegendaryItemColor()
	{
		return new Color(255 / 255f, 120 / 255f, 0 / 255f, 195 / 255f);
	}
}
