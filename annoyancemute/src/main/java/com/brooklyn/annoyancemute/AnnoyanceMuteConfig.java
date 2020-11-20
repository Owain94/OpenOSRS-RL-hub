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
package com.brooklyn.annoyancemute;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
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

	@ConfigSection(
		keyName = "prayersTitle",
		name = "Prayers",
		description = "Pray activation/deactivation sounds to mute",
		position = 3
	)
	default Title prayersTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "miscellaneousTitle",
		name = "Miscellaneous",
		description = "Miscellaneous sounds to mute",
		position = 4
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
		keyName = "muteOthersAreaSounds",
		name = "Others' Area Sounds",
		description = "Mutes other players' area sounds",
		titleSection = "miscellaneousTitle"
	)
	default boolean muteOthersAreaSounds()
	{
		return false;
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
		keyName = "muteNPCContact",
		name = "NPC Contact",
		description = "Mutes the sound of NPC Contact",
		titleSection = "miscellaneousTitle"
	)
	default boolean muteNPCContact()
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
		keyName = "muteWoodcutting",
		name = "Woodcutting",
		description = "Mutes the sound of Woodcutting",
		titleSection = "skillingTitle"
	)
	default boolean muteWoodcutting()
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
		keyName = "mutePickpocket",
		name = "Pickpocket",
		description = "Mutes the sound of the pickpocket plop",
		titleSection = "skillingTitle"
	)
	default boolean mutePickpocket()
	{
		return false;
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

	@ConfigItem(
		keyName = "muteThickSkin",
		name = "Thick Skin",
		description = "Mutes the activation sound of Thick Skin",
		titleSection = "prayersTitle",
		position = 1
	)
	default boolean muteThickSkin()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteBurstOfStrength",
		name = "Burst of Strength",
		description = "Mutes the activation sound of Burst of Strength",
		titleSection = "prayersTitle",
		position = 2
	)
	default boolean muteBurstofStrength()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteClarityOfThought",
		name = "Clarity of Thought",
		description = "Mutes the activation sound of Clarity of Thought",
		titleSection = "prayersTitle",
		position = 3
	)
	default boolean muteClarityOfThought()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteRockSkin",
		name = "Rock Skin",
		description = "Mutes the activation sound of Rock Skin",
		titleSection = "prayersTitle",
		position = 4
	)
	default boolean muteRockSkin()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteSuperhumanStrength",
		name = "Superhuman Strength",
		description = "Mutes the activation sound of Superhuman Strength",
		titleSection = "prayersTitle",
		position = 5
	)
	default boolean muteSuperhumanStrength()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteImprovedReflexes",
		name = "Improved Reflexes",
		description = "Mutes the activation sound of Improved Reflexes",
		titleSection = "prayersTitle",
		position = 6
	)
	default boolean muteImprovedReflexes()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteRapidHeal",
		name = "Rapid Heal",
		description = "Mutes the activation sound of Rapid Heal",
		titleSection = "prayersTitle",
		position = 7
	)
	default boolean muteRapidHeal()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteProtectItem",
		name = "Protect Item",
		description = "Mutes the activation sound of Protect Item",
		titleSection = "prayersTitle",
		position = 8
	)
	default boolean muteProtectItem()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteHawkEye",
		name = "Hawk Eye",
		description = "Mutes the activation sound of Hawk Eye",
		titleSection = "prayersTitle",
		position = 9
	)
	default boolean muteHawkEye()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteMysticLore",
		name = "Mystic Lore",
		description = "Mutes the activation sound of Mystic Lore",
		titleSection = "prayersTitle",
		position = 10
	)
	default boolean muteMysticLore()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteSteelSkin",
		name = "Steel Skin",
		description = "Mutes the activation sound of Steel Skin",
		titleSection = "prayersTitle",
		position = 11
	)
	default boolean muteSteelSkin()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteUltimateStrength",
		name = "Ultimate Strength",
		description = "Mutes the activation sound of Ultimate Strength",
		titleSection = "prayersTitle",
		position = 12
	)
	default boolean muteUltimateStrength()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteIncredibleReflexes",
		name = "Incredible Reflexes",
		description = "Mutes the activation sound of Incredible Reflexes",
		titleSection = "prayersTitle",
		position = 13
	)
	default boolean muteIncredibleReflexes()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteProtectFromMagic",
		name = "Protect from Magic",
		description = "Mutes the activation sound of Protect from Magic",
		titleSection = "prayersTitle",
		position = 14
	)
	default boolean muteProtectFromMagic()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteProtectFromRange",
		name = "Protect from Range",
		description = "Mutes the activation sound of Protect from Range",
		titleSection = "prayersTitle",
		position = 15
	)
	default boolean muteProtectFromRange()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteProtectFromMelee",
		name = "Protect from Melee",
		description = "Mutes the activation sound of Protect from Melee",
		titleSection = "prayersTitle",
		position = 16
	)
	default boolean muteProtectFromMelee()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteEagleEye",
		name = "Eagle Eye",
		description = "Mutes the activation sound of Eagle Eye",
		titleSection = "prayersTitle",
		position = 17
	)
	default boolean muteEagleEye()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteMysticMight",
		name = "Mystic Might",
		description = "Mutes the activation sound of Mystic Might",
		titleSection = "prayersTitle",
		position = 18
	)
	default boolean muteMysticMight()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteRetribution",
		name = "Retribution",
		description = "Mutes the activation sound of Retribution",
		titleSection = "prayersTitle",
		position = 19
	)
	default boolean muteRetribution()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteRedemption",
		name = "Redemption",
		description = "Mutes the activation sound of Redemption",
		titleSection = "prayersTitle",
		position = 20
	)
	default boolean muteRedemption()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteSmite",
		name = "Smite",
		description = "Mutes the activation sound of Smite",
		titleSection = "prayersTitle",
		position = 21
	)
	default boolean muteSmite()
	{
		return false;
	}

	@ConfigItem(
		keyName = "mutePreserve",
		name = "Preserve",
		description = "Mutes the activation sound of Preserve and Rapid Restore",
		titleSection = "prayersTitle",
		position = 22
	)
	default boolean mutePreserve()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteChivalry",
		name = "Chivalry",
		description = "Mutes the activation sound of Chivalry",
		titleSection = "prayersTitle",
		position = 23
	)
	default boolean muteChivalry()
	{
		return false;
	}

	@ConfigItem(
		keyName = "mutePiety",
		name = "Piety",
		description = "Mutes the activation sound of Piety",
		titleSection = "prayersTitle",
		position = 24
	)
	default boolean mutePiety()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteRigour",
		name = "Rigour",
		description = "Mutes the activation sound of Rigour and Sharp Eye",
		titleSection = "prayersTitle",
		position = 25
	)
	default boolean muteRigour()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteAugury",
		name = "Augury",
		description = "Mutes the activation sound of Augury and Mystic Will",
		titleSection = "prayersTitle",
		position = 26
	)
	default boolean muteAugury()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteDeactivatePrayer",
		name = "Deactivate Prayer",
		description = "Mutes the prayer deactivation sound",
		titleSection = "prayersTitle",
		position = 27
	)
	default boolean muteDeactivatePrayer()
	{
		return false;
	}


}
