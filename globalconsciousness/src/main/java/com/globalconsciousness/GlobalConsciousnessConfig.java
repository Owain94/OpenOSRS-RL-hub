package com.globalconsciousness;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(GlobalConsciousnessPlugin.CONFIG_GROUP_KEY)
public interface GlobalConsciousnessConfig extends Config {

	@ConfigItem(
			keyName = "itemName",
			name = "Item Name",
			description = "Name of item to display.",
			position = 1
	)
	default String itemName()
	{
		return "";
	}

	@ConfigItem(
			keyName = "iconSpeed",
			name = "Speed",
			description = "Speed to travel across the screen.",
			position = 2
	)
	@Range(
			min = 1,
			max = 20
	)
	default int iconSpeed() {
		return 1;
	}

	@ConfigItem(
			keyName = "iconScale",
			name = "Scale",
			description = "Size of floating icon.",
			position = 3
	)
	@Range(
			min = 1,
			max = 5
	)
	default int iconScale() {
		return 1;
	}

	@ConfigItem(
			keyName = "iconOpacity",
			name = "Opacity",
			description = "Opacity of floating icon.",
			position = 4
	)
	@Range(
			min = 0,
			max = 100
	)
	default int iconOpacity() {
		return 100;
	}

}
