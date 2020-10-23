package com.tobinfoboxes;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("tobinfoboxes")
public interface TobInfoboxesConfig extends Config
{
	@ConfigItem(
		keyName = "showMaiden",
		name = "Show Maiden",
		description = "Show Maiden completion time",
		position = 1
	)
	default boolean showMaiden()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showBloat",
		name = "Show Bloat",
		description = "Show Bloat completion time",
		position = 2
	)
	default boolean showBloat()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showNylo",
		name = "Show Nylocas",
		description = "Show Nylocas completion time",
		position = 3
	)
	default boolean showNylo()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSote",
		name = "Show Sotetseg",
		description = "Show Sotetseg completion time",
		position = 4
	)
	default boolean showSotetseg()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showXarpus",
		name = "Show Xarpus",
		description = "Show Xarpus completion time",
		position = 5
	)
	default boolean showXarpus()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showVerzik",
		name = "Show Verzik",
		description = "Show Verzik completion time",
		position = 6
	)
	default boolean showVerzik()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showTotal",
		name = "Show Total",
		description = "Show total room completion time",
		position = 7
	)
	default boolean showTotal()
	{
		return true;
	}
}
