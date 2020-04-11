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
		keyName = "drawTile",
		name = "Draw waypoint ground tile",
		description = "Render a ground tile in the world of your waypoint"
	)
	default boolean drawTile()
	{
		return true;
	}
}