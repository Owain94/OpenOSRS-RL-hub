/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.coxlightcolors;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("coxlightcolors")
public interface CoxLightColorsConfig extends Config
{
	@ConfigTitleSection(
		name = "Light colors",
		description = "Colors of the lights above the loot chest for different scenarios",
		position = 0,
		keyName = "colorsTitle"
	)
	default Title colorsTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		name = "Specific Uniques",
		description = "Uniques that, when obtained, will use the 'Specific Unique' color for the light",
		position = 1,
		keyName = "uniquesTitle"
	)
	default Title uniquesTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "noUnique",
		name = "No Unique",
		description = "Color of light when no unique item is obtained",
		position = 0,
		titleSection = "colorsTitle"
	)
	default Color noUnique()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = "unique",
		name = "Unique",
		description = "Color of light when a unique item is obtained (besides twisted kit or dust)",
		position = 1,
		titleSection = "colorsTitle"
	)
	default Color unique()
	{
		return Color.decode("#F155F5");
	}

	@ConfigItem(
		keyName = "dust",
		name = "Metamorphic Dust",
		description = "Color of light when metamorphic dust is obtained",
		position = 2,
		titleSection = "colorsTitle"
	)
	default Color dust()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "twistedKit",
		name = "Twisted Kit",
		description = "Color of light when a twisted kit is obtained",
		position = 3,
		titleSection = "colorsTitle"
	)
	default Color twistedKit()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "olmEntrance",
		name = "Olm Entrance",
		description = "Color of the barrier used to enter the Olm room",
		position = 4,
		titleSection = "colorsTitle"
	)
	default Color olmEntrance()
	{
		return Color.decode("#8CFF0B");
	}

	@ConfigItem(
		keyName = "specificUniqueColor",
		name = "Specific Unique",
		description = "Color of the light when an item specified below is obtained",
		position = 5,
		titleSection = "colorsTitle"
	)
	default Color specificUniqueColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "specifyTwistedBow",
		name = "Twisted bow",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyTwistedBow()
	{
		return true;
	}

	@ConfigItem(
		keyName = "specifyKodaiInsignia",
		name = "Kodai insignia",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyKodaiInsignia()
	{
		return true;
	}

	@ConfigItem(
		keyName = "specifyElderMaul",
		name = "Elder maul",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyElderMaul()
	{
		return true;
	}

	@ConfigItem(
		keyName = "specifyDragonClaws",
		name = "Dragon Claws",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyDragonClaws()
	{
		return false;
	}

	@ConfigItem(
		keyName = "specifyAncestralHat",
		name = "Ancestral hat",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyAncestralHat()
	{
		return false;
	}


	@ConfigItem(
		keyName = "specifyAncestralRobeTop",
		name = "Ancestral robe top",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyAncestralRobeTop()
	{
		return false;
	}

	@ConfigItem(
		keyName = "specifyAncestralRobeBottom",
		name = "Ancestral robe bottom",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyAncestralRobeBottom()
	{
		return false;
	}

	@ConfigItem(
		keyName = "specifyDinhsBulwark",
		name = "Dinh's bulwark",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyDinhsBulwark()
	{
		return false;
	}

	@ConfigItem(
		keyName = "specifyDragonHunterCrossbow",
		name = "Dragon hunter crossbow",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyDragonHunterCrossbow()
	{
		return false;
	}

	@ConfigItem(
		keyName = "specifyTwistedBuckler",
		name = "Twisted buckler",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyTwistedBuckler()
	{
		return false;
	}

	@ConfigItem(
		keyName = "specifyArcanePrayerScroll",
		name = "Arcane prayer scroll",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyArcanePrayerScroll()
	{
		return false;
	}

	@ConfigItem(
		keyName = "specifyDexPrayerScroll",
		name = "Dexterous Prayer Scroll",
		description = "Color the light according to the 'Specific Unique' color when this item is obtained",
		titleSection = "uniquesTitle"
	)
	default boolean specifyDexPrayerScroll()
	{
		return false;
	}
}
