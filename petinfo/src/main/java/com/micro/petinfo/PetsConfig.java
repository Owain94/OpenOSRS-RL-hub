/* Copyright (c) 2020 by micro
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
 *
 * Portions of the code are based off of the "Implings" RuneLite plugin.
 * The "Implings" is:
 * Copyright (c) 2017, Robin <robin.weymans@gmail.com>
 * All rights reserved.
 */

package com.micro.petinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("pets")
public interface PetsConfig extends Config
{
	@ConfigTitleSection(
		keyName = "highlightSection",
			name = "Pet group highlight settings",
			description = "Choose how and what color pet groups (i.e. bossing, skilling) should be highlighted.",
			position = 99
	)
	default Title highlightSection()
	{
		return new Title();
	}

	enum PetMode
	{
		OFF,
		HIGHLIGHT,
		NAME_ONLY
	}

	enum HighlightMode
	{
		OFF,
		ALL,
		OWN
	}

	@ConfigItem(
			position = 1,
			keyName = "petInfo",
			name = "Right click menu",
			description = "Show pet Info and Owner option on right click"
	)
	default boolean showMenu()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "toggleHighlight",
			name = "Highlight toggle",
			description = "Select if no, all, or only your own pets are highlighted"
	)
	default HighlightMode highlight() { return HighlightMode.OFF; }

	@ConfigItem(
			position = 3,
			keyName = "showBoss",
			name = "Highlight Bossing Pets",
			description = "Toggles highlighting for bossing pets",
			titleSection = "highlightSection"
	)
	default PetMode showBoss()
	{
		return PetMode.HIGHLIGHT;
	}

	@ConfigItem(
			position = 4,
			keyName = "bossColor",
			name = "Boss Pet color",
			description = "Highlight color for boss pets",
			titleSection = "highlightSection"
	)
	default Color getBossColor()
	{
		return new Color(193, 18, 18);
	}

	@ConfigItem(
			position = 5,
			keyName = "showSkilling",
			name = "Highlight Skilling Pets",
			description = "Toggles highlighting for skilling pets",
			titleSection = "highlightSection"
	)
	default PetMode showSkilling()
	{
		return PetMode.HIGHLIGHT;
	}

	@ConfigItem(
			position = 6,
			keyName = "skillingColor",
			name = "Skilling Pet color",
			description = "Highlight color for skilling pets",
			titleSection = "highlightSection"
	)
	default Color getSkillingColor()
	{
		return new Color(106, 232, 38);
	}

	@ConfigItem(
			position = 7,
			keyName = "showToy",
			name = "Highlight Toys",
			description = "Toggles highlighting for clockwork toys",
			titleSection = "highlightSection"
	)
	default PetMode showToy()
	{
		return PetMode.OFF;
	}

	@ConfigItem(
			position = 8,
			keyName = "toyColor",
			name = "Toy color",
			description = "Highlight color for clockwork toys",
			titleSection = "highlightSection"
	)
	default Color getToyColor()
	{
		return new Color(139, 120, 69);
	}

	@ConfigItem(
			position = 9,
			keyName = "showOther",
			name = "Show Other Pets",
			description = "Toggles highlighting for other pets (like cats)",
			titleSection = "highlightSection"
	)
	default PetMode showOther()
	{
		return PetMode.NAME_ONLY;
	}

	@ConfigItem(
			position = 10,
			keyName = "otherColor",
			name = "Other Pet color",
			description = "Highlight color for other pets",
			titleSection = "highlightSection"
	)
	default Color getOtherColor()
	{
		return new Color(18, 47, 193);
	}

	@ConfigItem(
			position = 11,
			keyName = "showNpcId",
			name = "Show NPC ID",
			description = "Show the pets NPC id next to its overhead name",
			titleSection = "highlightSection"
	)
	default boolean getShowNpcId()
	{
		return false;
	}
}
