/*
 * Copyright (c) 2020, ConorLeckey <https://github.com/ConorLeckey>
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
package com.cannonhighlight;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("cannonhighlight")
public interface CannonHighlighterConfig extends Config
{
	@ConfigItem(
		keyName = "drawNames",
		name = "Draw Status Names",
		description = "Draw text containing the hit status above NPCs"
	)
	default boolean drawNames()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightSouthWestTile",
			name = "Highlight South West Tile",
			description = "Show NPCs south western tiles"
	)
	default boolean highlightSouthWestTile()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightHull",
			name = "Highlight Hull",
			description = "Show NPC hulls"
	)
	default boolean highlightHull()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showHitZones",
			name = "Show Hit Zones",
			description = "Show the cannon's double hit and no hit zones"
	)
	default boolean showHitZones()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "unhittableColor",
			name = "Unhittable Color",
			position = 1,
			description = "Configures the color used for unhittable npcs and tiles"
	)
	default Color unhittableColor() {
		return Color.red;
	}

	@Alpha
	@ConfigItem(
			keyName = "singleColor",
			name = "Single Hit Color",
			position = 2,
			description = "Configures the color used for single hittable npcs and tiles"
	)
	default Color singleColor() {
		return Color.yellow;
	}

	@Alpha
	@ConfigItem(
			keyName = "doubleColor",
			name = "Double Hit Color",
			position = 3,
			description = "Configures the color used for double hittable npcs and tiles"
	)
	default Color doubleColor() {
		return Color.green;
	}
}
