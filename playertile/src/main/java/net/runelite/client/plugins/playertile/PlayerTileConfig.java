package net.runelite.client.plugins.playertile;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("player")
public interface PlayerTileConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "tileColor",
		name = "Tile Color",
		description = "Color of the tile"
	)
	default Color getTileColor()
	{
		return Color.WHITE;
	}

	@Range(
		max = 255
	)
	@ConfigItem(
		position = 1,
		keyName = "outlineOpacity",
		name = "Outline Opacity",
		description = "Opacity level of the tiles outline, 0-255"
	)
	default int getOutlineOpacity()
	{
		return 0;
	}

	@Range(
		max = 255
	)
	@ConfigItem(
		position = 2,
		keyName = "fillOpacity",
		name = "Fill Opacity",
		description = "Opacity level of the tiles filled area, 0-255"
	)
	default int getFillOpacity()
	{
		return 100;
	}


}