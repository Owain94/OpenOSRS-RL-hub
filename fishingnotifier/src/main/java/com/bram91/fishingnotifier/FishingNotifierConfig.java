package com.bram91.fishingnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("fishingnotifier")
public interface FishingNotifierConfig extends Config
{
	@ConfigItem(
		keyName = "barbfishing",
		name = "Barbarian Fishing",
		description = "Notifies when idling while barbarian fishing.",
		position = 0
	)
	default boolean barbFishing()
	{
		return false;
	}

	@ConfigItem(
		keyName = "anglerFishing",
		name = "Angler Fishing",
		description = "Notifies when idling while angler fishing.",
		position = 1
	)
	default boolean anglerFishing()
	{
		return false;
	}

	@ConfigItem(
		keyName = "karambwanFishing",
		name = "Karambwan Fishing",
		description = "Notifies when idling while Karambwan fishing.",
		position = 2
	)
	default boolean karambwanFishing()
	{
		return false;
	}

	@ConfigItem(
		keyName = "monkFishing",
		name = "Monkfish Fishing",
		description = "Notifies when idling while Monkfish fishing.",
		position = 3
	)
	default boolean monkFishing()
	{
		return false;
	}

	@ConfigItem(
		keyName = "eelFishing",
		name = "Sacred/Infernal eels",
		description = "Notifies when idling while eel fishing.",
		position = 4
	)
	default boolean eelFishing()
	{
		return false;
	}

	@ConfigItem(
		keyName = "otherFishing",
		name = "All other Fish",
		description = "Notifies when idling while fishing for other fish.",
		position = 5
	)
	default boolean otherFishing()
	{
		return false;
	}
}