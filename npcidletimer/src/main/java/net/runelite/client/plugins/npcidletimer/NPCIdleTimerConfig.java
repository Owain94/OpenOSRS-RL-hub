package net.runelite.client.plugins.npcidletimer;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("npcidletimerplugin")
public interface NPCIdleTimerConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "showOverlay",
		name = "Show timer over chosen NPCs",
		description = "Configures whether or not to have a timer over the chosen NPCs"
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "npcToShowTimer",
		name = "NPC Names",
		description = "Enter names of NPCs where you wish to use this plugin"
	)
	default String npcToShowTimer()
	{
		return "";
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "timerColor",
		name = "Color of timer",
		description = "Configures the color of the timer"
	)
	default Color timerColor()
	{
		return Color.WHITE;
	}

	@Range(
		max = 300
	)
	@ConfigItem(
		position = 3,
		keyName = "maxDisplay",
		name = "Time at which timer appears",
		description = "The maximum time at which the timer is displayed"
	)
	default int maxDisplay()
	{
		return 300;
	}

	@Range(
		max = 300
	)
	@ConfigItem(
		position = 4,
		keyName = "timerHeight",
		name = "Height of timer",
		description = "Change the vertical offset of the timer above the npc"
	)
	default int timerHeight()
	{
		return 25;
	}
}