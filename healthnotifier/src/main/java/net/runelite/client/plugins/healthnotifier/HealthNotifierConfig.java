package net.runelite.client.plugins.healthnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("healthnotifier")
public interface HealthNotifierConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "NPCName",
		name = "NPC Name",
		description = "The name of the NPC, you want to be notified when it is below the specified health. " +
			"Leave blank for it to fire on any NPC."
	)
	default String NPCName()
	{
		return "";
	}

	@ConfigItem(
		position = 2,
		keyName = "specifiedHealth",
		name = "Specified Health",
		description = "If the NPC's health is below or equal to this value it will send a notification"
	)
	default int specifiedHealth()
	{
		return 10;
	}
}