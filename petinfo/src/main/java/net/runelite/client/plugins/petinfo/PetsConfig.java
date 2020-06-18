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

package net.runelite.client.plugins.petinfo;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("pets")
public interface PetsConfig extends Config
{
	enum PetMode
	{
		OFF,
		HIGHLIGHT,
		NAME_ONLY
	}

	@ConfigItem(
		position = 1,
		keyName = "showBoss",
		name = "Highlight Bossing Pets",
		description = "Toggles highlighting for bossing pets"
	)
	default PetMode showBoss()
	{
		return PetMode.HIGHLIGHT;
	}

	@ConfigItem(
		position = 2,
		keyName = "bossColor",
		name = "Boss Pet color",
		description = "Highlight color for boss pets"
	)
	default Color getBossColor()
	{
		return new Color(193, 18, 18);
	}

	@ConfigItem(
		position = 3,
		keyName = "showSkilling",
		name = "Highlight Skilling Pets",
		description = "Toggles highlighting for skilling pets"
	)
	default PetMode showSkilling()
	{
		return PetMode.HIGHLIGHT;
	}

	@ConfigItem(
		position = 4,
		keyName = "skillingColor",
		name = "Skilling Pet color",
		description = "Highlight color for skilling pets"
	)
	default Color getSkillingColor()
	{
		return new Color(106, 232, 38);
	}

	@ConfigItem(
		position = 5,
		keyName = "showToy",
		name = "Highlight Toys",
		description = "Toggles highlighting for clockwork toys"
	)
	default PetMode showToy()
	{
		return PetMode.OFF;
	}

	@ConfigItem(
		position = 6,
		keyName = "toyColor",
		name = "Toy color",
		description = "Highlight color for clockwork toys"
	)
	default Color getToyColor()
	{
		return new Color(139, 120, 69);
	}

	@ConfigItem(
		position = 7,
		keyName = "showOther",
		name = "Show Other Pets",
		description = "Toggles highlighting for other pets (like cats)"
	)
	default PetMode showOther()
	{
		return PetMode.NAME_ONLY;
	}

	@ConfigItem(
		position = 8,
		keyName = "otherColor",
		name = "Other Pet color",
		description = "Highlight color for other pets"
	)
	default Color getOtherColor()
	{
		return new Color(18, 47, 193);
	}

	@ConfigItem(
		position = 9,
		keyName = "petInfo",
		name = "Right click menu",
		description = "Show pet Info and Owner option on right click"
	)
	default boolean showMenu()
	{
		return true;
	}

	@ConfigItem(
		position = 10,
		keyName = "showNpcId",
		name = "Show NPC ID",
		description = "Show the pets NPC id next to its overhead name"
	)
	default boolean getShowNpcId()
	{
		return false;
	}
}