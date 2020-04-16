package net.runelite.client.plugins.mapwaypoint;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mapwaypoint")
public interface MapWaypointConfig extends Config
{
	@ConfigItem(
		keyName = "shiftClick",
		name = "Shift-click waypoints",
		description = "Set and remove waypoints with shift-click"
	)
	default boolean shiftClick()
	{
		return true;
	}

	@ConfigItem(
		keyName = "playSoundEffect",
		name = "Play sound effect",
		description = "Play sound effect when setting, removing, and focusing a waypoint"
	)
	default boolean playSoundEffect()
	{
		return true;
	}

	@ConfigItem(
		keyName = "drawTile",
		name = "Draw waypoint on ground",
		description = "Draw an indicator of your waypoint on the ground"
	)
	default boolean drawTile()
	{
		return true;
	}

	@ConfigItem(
		keyName = "drawMinimap",
		name = "Draw waypoint on minimap",
		description = "Draw an indicator of your waypoint on the minimap"
	)
	default boolean drawMinimap()
	{
		return true;
	}
}