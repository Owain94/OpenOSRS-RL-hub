/*
 * Copyright (c) 2020, Brooklyn <https://github.com/Broooklyn>
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
package net.runelite.client.plugins.annoyancemute;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("annoyancemute")
public interface AnnoyanceMuteConfig extends Config
{
	@ConfigTitleSection(
		keyName = "combatTitle",
		name = "Combat",
		description = "Combat sounds to mute",
		position = 0
	)
	default Title combatTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "npcsTitle",
		name = "NPCs",
		description = "NPC sounds to mute",
		position = 1
	)
	default Title npcsTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "skillingTitle",
		name = "Skilling",
		description = "Skilling sounds to mute",
		position = 2
	)
	default Title skillingTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "miscellaneousTitle",
		name = "Miscellaneous",
		description = "Miscellaneous sounds to mute",
		position = 3
	)
	default Title miscellaneousTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "mutePetSounds",
		name = "Pets",
		description = "Mutes the sounds of noise-making pets",
		titleSection = "npcsTitle"
	)
	default boolean mutePetSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteAreaOfEffectSpells",
		name = "Humidify",
		description = "Mutes humidify spell sound",
		titleSection = "skillingTitle"
	)
	default boolean muteAOESounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteSnowballs",
		name = "Snowballs",
		description = "Mutes the sounds of snowballs being thrown",
		titleSection = "miscellaneousTitle"
	)
	default boolean muteSnowballSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteWhack",
		name = "Whack",
		description = "Mutes the Rubber chicken and Stale baguette whack sound",
		titleSection = "miscellaneousTitle"
	)
	default boolean muteRubberChickenSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteCrier",
		name = "Town Crier",
		description = "Mutes the sounds of the Town Crier",
		titleSection = "npcsTitle"
	)
	default boolean muteTownCrierSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteCannon",
		name = "Cannon spin",
		description = "Mutes the sounds of a cannon spinning",
		titleSection = "combatTitle"
	)
	default boolean muteCannon()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteREEEE",
		name = "Armadyl Crossbow",
		description = "Mutes the REEEEE of the ACB spec",
		titleSection = "combatTitle"
	)
	default boolean muteREEEE()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteSire",
		name = "Sire Spawns",
		description = "Mutes the sounds of the Abyssal Sire's spawns",
		titleSection = "npcsTitle"
	)
	default boolean muteSire()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteObelisk",
		name = "Wilderness Obelisk",
		description = "Mutes the sounds of the Wilderness Obelisk",
		titleSection = "miscellaneousTitle"
	)
	default boolean muteObelisk()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteRandoms",
		name = "Random Events",
		description = "Mutes the sounds produced by random events",
		titleSection = "npcsTitle"
	)
	default boolean muteRandoms()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteTekton",
		name = "Tekton meteors",
		description = "Mutes the sound of Tekton's meteor attack",
		titleSection = "npcsTitle"
	)
	default boolean muteTekton()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteDenseEssence",
		name = "Dense Essence",
		description = "Mutes the sound of chiseling Dense Essence",
		titleSection = "skillingTitle"
	)
	default boolean muteDenseEssence()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteChopChop",
		name = "Chop Chop!",
		description = "Mutes the sound of the Dragon axe special",
		titleSection = "skillingTitle"
	)
	default boolean muteChopChop()
	{
		return true;
	}

	@ConfigItem(
		keyName = "plankMake",
		name = "Plank Make",
		description = "Mutes the sound of Plank Make",
		titleSection = "skillingTitle"
	)
	default boolean mutePlankMake()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteNightmare",
		name = "Nightmare",
		description = "Mutes the sound of the Nightmare's parry",
		titleSection = "npcsTitle"
	)
	default boolean muteNightmare()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteScarabs",
		name = "Scarab Swarm",
		description = "Mutes the sound of the Scarab swarm in Pyramid Plunder",
		titleSection = "npcsTitle"
	)
	default boolean muteScarabs()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteFishing",
		name = "Fishing",
		description = "Mutes the sound of Fishing",
		titleSection = "skillingTitle"
	)
	default boolean muteFishing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteAlchemy",
		name = "Alchemy",
		description = "Mutes the sounds of Low and High Alchemy",
		titleSection = "skillingTitle"
	)
	default boolean muteAlchemy()
	{
		return true;
	}
}
