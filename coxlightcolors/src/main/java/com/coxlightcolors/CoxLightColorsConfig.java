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
package com.coxlightcolors;

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
		keyName = "lightColorsTitle",
		name = "Light colors",
		description = "Colors of the lights above the loot chest for different scenarios",
		position = 0
	)
	default Title lightColorsTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "uniquesTitle",
		name = "Item groups",
		description = "Uniques that, when obtained, will use the 'Specific Unique' color for the light",
		position = 1
	)
	default Title uniquesTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "togglesTitle",
		name = "Toggles",
		description = "Toggle different recolors on or off",
		position = 2
	)
	default Title togglesTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "standardLoot",
		name = "Standard loot",
		description = "Color of light when no unique item is obtained",
		position = 0,
		titleSection = "lightColorsTitle"
	)
	default Color standardLoot()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = "enableStandardLoot",
		name = "Recolor standard loot",
		description = "Enable recoloring the light of the chest when no unique is obtained",
		position = 1,
		titleSection = "togglesTitle"
	)
	default boolean enableStandardLoot()
	{
		return true;
	}

	@ConfigItem(
		keyName = "unique",
		name = "Unique",
		description = "Color of light when a unique item is obtained (besides twisted kit or dust)",
		position = 2,
		titleSection = "lightColorsTitle"
	)
	default Color unique()
	{
		return Color.decode("#F155F5");
	}

	@ConfigItem(
		keyName = "enableUnique",
		name = "Recolor uniques",
		description = "Enable recoloring the light of the chest when a unique is obtained",
		position = 3,
		titleSection = "togglesTitle"
	)
	default boolean enableUnique()
	{
		return true;
	}

	@ConfigItem(
		keyName = "dust",
		name = "Metamorphic Dust",
		description = "Color of light when metamorphic dust is obtained",
		position = 4,
		titleSection = "lightColorsTitle"
	)
	default Color dust()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "enableDust",
		name = "Recolor dust",
		description = "Enable recoloring the light of the chest when metamorphic dust is obtained",
		position = 5,
		titleSection = "togglesTitle"
	)
	default boolean enableDust()
	{
		return true;
	}

	@ConfigItem(
		keyName = "twistedKit",
		name = "Twisted Kit",
		description = "Color of light when a twisted kit is obtained",
		position = 6,
		titleSection = "lightColorsTitle"
	)
	default Color twistedKit()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "enableKit",
		name = "Recolor Twisted kit",
		description = "Enable recoloring the light of the chest when a twisted kit is obtained",
		position = 7,
		titleSection = "togglesTitle"
	)
	default boolean enableKit()
	{
		return true;
	}

	@ConfigItem(
		keyName = "olmEntrance",
		name = "Olm Entrance",
		description = "Color of the barrier used to enter the Olm room",
		position = 8,
		titleSection = "lightColorsTitle"
	)
	default Color olmEntrance()
	{
		return Color.decode("#8CFF0B");
	}

	@ConfigItem(
		keyName = "enableEntrance",
		name = "Recolor entance",
		description = "Enable recoloring the entrance barrier to Olm",
		position = 9,
		titleSection = "togglesTitle"
	)
	default boolean enableEntrance()
	{
		return true;
	}

	@ConfigItem(
		keyName = "groupOneColor",
		name = "Group 1",
		description = "Color of the light when an item from group 1 is obtained",
		position = 10,
		titleSection = "lightColorsTitle"
	)
	default Color groupOneColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "enableGroupOne",
		name = "Recolor group 1",
		description = "Enable recoloring the light of the chest when a unique from group 1 is obtained",
		position = 11,
		titleSection = "togglesTitle"
	)
	default boolean enableGroupOne()
	{
		return true;
	}

	@ConfigItem(
		keyName = "groupTwoColor",
		name = "Group 2",
		description = "Color of the light when an item from group 2 is obtained",
		position = 12,
		titleSection = "lightColorsTitle"
	)
	default Color groupTwoColor()
	{
		return Color.BLUE;
	}

	@ConfigItem(
		keyName = "enableGroupTwo",
		name = "Recolor group 2",
		description = "Enable recoloring the light of the chest when a unique from group 2 is obtained",
		position = 13,
		titleSection = "togglesTitle"
	)
	default boolean enableGroupTwo()
	{
		return true;
	}

	@ConfigItem(
		keyName = "groupThreeColor",
		name = "Group 3",
		description = "Color of the light when an item from group 3 is obtained",
		position = 14,
		titleSection = "lightColorsTitle"
	)
	default Color groupThreeColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "enableGroupThree",
		name = "Recolor group 3",
		description = "Enable recoloring the light of the chest when a unique from group 3 is obtained",
		position = 15,
		titleSection = "togglesTitle"
	)
	default boolean enableGroupThree()
	{
		return true;
	}

	@ConfigItem(
		keyName = "groupTwistedBow",
		name = "Twisted bow",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		position = 16,
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupTwistedBow()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupKodai",
		name = "Kodai insignia",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupKodai()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupElderMaul",
		name = "Elder maul",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupElderMaul()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupClaws",
		name = "Dragon claws",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupClaws()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupAncestralHat",
		name = "Ancestral hat",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupAncestralHat()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupAncestralTop",
		name = "Ancestral robe top",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupAncestralTop()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupAncestralBottom",
		name = "Ancestral robe bottom",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupAncestralBottom()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupDinhs",
		name = "Dinh's bulwark",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupDinhs()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupDHCB",
		name = "Dragon hunter crossbow",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupDHCB()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupBuckler",
		name = "Twisted buckler",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupBuckler()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupArcane",
		name = "Arcane prayer scroll",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupArcane()
	{
		return ItemGroup.NONE;
	}

	@ConfigItem(
		keyName = "groupDex",
		name = "Dexterous prayer scroll",
		description = "Group color to use when this item is obtained. If no group is specified, the 'unique' color will be used",
		titleSection = "uniquesTitle"
	)
	default ItemGroup groupDex()
	{
		return ItemGroup.NONE;
	}

}
