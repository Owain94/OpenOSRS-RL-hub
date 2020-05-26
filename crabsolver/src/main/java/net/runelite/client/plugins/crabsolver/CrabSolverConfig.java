package net.runelite.client.plugins.crabsolver;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("crabsolver")
public interface CrabSolverConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "displayColor",
		name = "Display Orb Color",
		description = "Configures whether to display the orb color needed to solve the crystal"
	)
	default boolean displayColor()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "displayStyle",
		name = "Display Combat Style",
		description = "Configures whether to display the combat style needed to solve the crystal"
	)
	default boolean displayStyle()
	{
		return true;
	}
}