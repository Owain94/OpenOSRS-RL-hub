/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, Bram91 <https://github.com/Bram91>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bram91.brushmarkers;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;

@ConfigGroup("brushMarkers")
public interface BrushMarkerConfig extends Config
{
	@ConfigSection(
		keyName = "colorSection",
		name = "Color Palette",
		description = "Contains all the colors",
		position = 0
	)
	default Title colorSection()
	{
		return new Title();
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor1",
		name = "Color1",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 0
	)
	default Color markerColor1()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor2",
		name = "Color 2",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 1
	)
	default Color markerColor2()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor3",
		name = "Color 3",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 2
	)
	default Color markerColor3()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor4",
		name = "Color 4",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 3
	)
	default Color markerColor4()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor5",
		name = "Color 5",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 4
	)
	default Color markerColor5()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor6",
		name = "Color 6",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 5
	)
	default Color markerColor6()
	{
		return Color.MAGENTA;
	}

	@ConfigItem(
		keyName = "brushdDoubleColors",
		name = "Enable color 7-12",
		description = "Enables color 7-12 for double the fun!",
		titleSection = "colorSection",
		position = 6
	)
	default boolean doubleColors()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor7",
		name = "Color 7",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 7
	)
	default Color markerColor7()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor8",
		name = "Color 8",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 8
	)
	default Color markerColor8()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor9",
		name = "Color 9",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 9
	)
	default Color markerColor9()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor10",
		name = "Color 10",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 10
	)
	default Color markerColor10()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor11",
		name = "Color 11",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 11
	)
	default Color markerColor11()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		keyName = "brushColor12",
		name = "Color 12",
		description = "Configures the color of marked tile",
		titleSection = "colorSection",
		position = 12
	)
	default Color markerColor12()
	{
		return Color.MAGENTA;
	}

	@ConfigItem(
		keyName = "brushdrawOnMinimap",
		name = "Draw tiles on minimap",
		description = "Configures whether marked tiles should be drawn on minimap",
		position = 2
	)
	default boolean drawTileOnMinimmap()
	{
		return false;
	}

	@ConfigItem(
		keyName = "brushdrawOnworldmap",
		name = "Draw tiles on worldmap",
		description = "Configures whether marked tiles should be drawn on the worldmap",
		position = 3
	)
	default boolean drawTilesOnWorldMap()
	{
		return false;
	}

	@ConfigItem(
		keyName = "brushreplaceMode",
		name = "Replace mode",
		description = "Allows you to draw over existing tiles",
		position = 4
	)
	default boolean replaceMode()
	{
		return false;
	}

	@ConfigItem(
		keyName = "brushFillPoly",
		name = "Fill tiles",
		description = "Fills the tiles you have drawn.",
		position = 5
	)
	default boolean fillPoly()
	{
		return false;
	}

	@Range(
		max = 128
	)
	@ConfigItem(
		keyName = "brushPolyAlpha",
		name = "Set fill transparency",
		description = "Allows you to change the transparency of the tile fill.",
		position = 6
	)
	default int polyAlpha()
	{
		return 64;
	}

	@ConfigItem(
		keyName = "brushSize",
		name = "Brush Size",
		description = "Changes the brush size",
		position = 7
	)
	default BrushSize brushSize()
	{
		return BrushSize.ONE;
	}

	@ConfigItem(
		keyName = "brushpaintMode",
		name = "Paint Mode",
		description = "Enables paint mode",
		position = 8
	)
	default boolean paintMode()
	{
		return false;
	}
}
