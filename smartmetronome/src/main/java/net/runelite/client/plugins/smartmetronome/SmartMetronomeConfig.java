/*
 * Copyright (c) 2020, Brooklyn <https://github.com/Broooklyn>
 *
 * For their work on the original Metronome:
 * Copyright (c) 2018, SomeoneWithAnInternetConnection
 * Copyright (c) 2018, oplosthee <https://github.com/oplosthee>
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
package net.runelite.client.plugins.smartmetronome;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;

@ConfigGroup("smartMetronome")
public interface SmartMetronomeConfig extends Config
{
	int VOLUME_MAX = SoundEffectVolume.HIGH;
	String ENABLES = "Enables Smart Metronome ";

	@ConfigTitleSection(
		keyName = "settingsTitle",
		name = "Settings",
		description = "Smart Metronome Settings",
		position = 0
	)
	default Title settingsTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "bossesTitle",
		name = "Bosses",
		description = "Boss Regions in which to activate Smart Metronome",
		position = 1
	)
	default Title bossesTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "minigamesTitle",
		name = "Minigames",
		description = "Minigames in which to activate Smart Metronome",
		position = 2
	)
	default Title minigamesTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "raidsTitle",
		name = "Raids",
		description = "Raids in which to activate Smart Metronome",
		position = 3
	)
	default Title raidsTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "slayerTitle",
		name = "Slayer",
		description = "Slayer locations in which to activate Smart Metronome",
		position = 4
	)
	default Title slayerTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "otherTitle",
		name = "Other",
		description = "Other times in which to activate Smart Metronome",
		position = 5
	)
	default Title otherTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "tickCount",
		name = "Tick count",
		description = "Configures the tick on which a sound will be played.",
		titleSection = "settingsTitle"
	)
	default int tickCount()
	{
		return 1;
	}

	@Range(
		max = VOLUME_MAX
	)
	@ConfigItem(
		keyName = "tickVolume",
		name = "Tick volume",
		description = "Configures the volume of the tick sound. A value of 0 will disable tick sounds.",
		titleSection = "settingsTitle"
	)
	default int tickVolume()
	{
		return SoundEffectVolume.MEDIUM_HIGH;
	}

	@Range(
		max = VOLUME_MAX
	)
	@ConfigItem(
		keyName = "tockVolume",
		name = "Tock volume",
		description = "Configures the volume of the tock sound. A value of 0 will disable tock sounds.",
		titleSection = "settingsTitle"
	)
	default int tockVolume()
	{
		return SoundEffectVolume.MUTED;
	}

	@ConfigItem(
		keyName = "overrideMetronome",
		name = "Override",
		description = "Overrides Smart Metronome and enables the metronome everywhere",
		position = -2,
		titleSection = "settingsTitle"
	)
	default boolean overrideMetronome()
	{
		return false;
	}

	@ConfigItem(
		keyName = "abyssalSireMetronome",
		name = "Abyssal Sire",
		description = ENABLES + "at Abyssal Sire",
		titleSection = "bossesTitle"
	)
	default boolean abyssalSireMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "alchemicalHydraMetronome",
		name = "Alchemical Hydra",
		description = ENABLES + "at Alchemical Hydra",
		titleSection = "bossesTitle"
	)
	default boolean alchemicalHydraMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "barrowsMetronome",
		name = "Barrows",
		description = ENABLES + "at Barrows",
		titleSection = "bossesTitle"
	)
	default boolean barrowsMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "cerberusMetronome",
		name = "Cerberus",
		description = ENABLES + "at Cerberus",
		titleSection = "bossesTitle"
	)
	default boolean cerberusMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "commanderZilyanaMetronome",
		name = "Commander Zilyana",
		description = ENABLES + "at Commander Zilyana",
		titleSection = "bossesTitle"
	)
	default boolean commanderZilyanaMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "corpBeastMetronome",
		name = "Corporeal Beast",
		description = ENABLES + "at Corp",
		titleSection = "bossesTitle"
	)
	default boolean corpBeastMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "dksMetronome",
		name = "Dagannoth Kings",
		description = ENABLES + "at DKS",
		titleSection = "bossesTitle"
	)
	default boolean dksMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "generalGraardorMetronome",
		name = "General Graardor",
		description = ENABLES + "at General Graardor",
		titleSection = "bossesTitle"
	)
	default boolean generalGraardorMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "giantMoleMetronome",
		name = "Giant Mole",
		description = ENABLES + "in the Falador Mole Lair",
		titleSection = "bossesTitle"
	)
	default boolean giantMoleMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "grotesqueGuardiansMetronome",
		name = "Grotesque Guardians",
		description = ENABLES + "at Grotesque Guardians",
		titleSection = "bossesTitle"
	)
	default boolean grotesqueGuardiansMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "krilMetronome",
		name = "K'ril Tsutsaroth",
		description = ENABLES + "at K'ril Tsutsaroth",
		titleSection = "bossesTitle"
	)
	default boolean krilMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "kqMetronome",
		name = "Kalphite Queen",
		description = ENABLES + "at Kalphite Queen",
		titleSection = "bossesTitle"
	)
	default boolean kqMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "kreeMetronome",
		name = "Kree'arra",
		description = ENABLES + "at Kree'arra",
		titleSection = "bossesTitle"
	)
	default boolean kreeMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "nightmareMetronome",
		name = "Nightmare",
		description = ENABLES + "at the Nightmare",
		titleSection = "bossesTitle"
	)
	default boolean nightmareMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "sarachnisMetronome",
		name = "Sarachnis",
		description = ENABLES + "at Sarachnis",
		titleSection = "bossesTitle"
	)
	default boolean sarachnisMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "skotizoMetronome",
		name = "Skotizo",
		description = ENABLES + "at Skotizo",
		titleSection = "bossesTitle"
	)
	default boolean skotizoMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "thermyMetronome",
		name = "Thermonuclear Smoke Devil",
		description = ENABLES + "at the Thermonuclear Smoke Devil",
		titleSection = "bossesTitle"
	)
	default boolean thermyMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vorkathMetronome",
		name = "Vorkath",
		description = ENABLES + "at Vorkath",
		titleSection = "bossesTitle"
	)
	default boolean vorkathMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "wintertodtMetronome",
		name = "Wintertodt",
		description = ENABLES + "at Wintertodt",
		titleSection = "bossesTitle"
	)
	default boolean wintertodtMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "zalcanoMetronome",
		name = "Zalcano",
		description = ENABLES + "at Zalcano",
		titleSection = "bossesTitle"
	)
	default boolean zalcanoMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "zulrahMetronome",
		name = "Zulrah",
		description = ENABLES + "at Zulrah",
		titleSection = "bossesTitle"
	)
	default boolean zulrahMetronome()
	{
		return true;
	}

	// Minigames

	@ConfigItem(
		keyName = "barbAssaultMetronome",
		name = "Barbarian Assault",
		description = ENABLES + "in Barbarian Assault",
		titleSection = "minigamesTitle"
	)
	default boolean barbAssaultMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "blastFurnaceMetronome",
		name = "Blast Furnace",
		description = ENABLES + "at Blast Furnace",
		titleSection = "minigamesTitle"
	)
	default boolean blastFurnaceMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "brimhavenAgilityMetronome",
		name = "Brimhaven Agility",
		description = ENABLES + "at the Brimhaven Agility Course",
		titleSection = "minigamesTitle"
	)
	default boolean brimhavenAgilityMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "fightCaveMetronome",
		name = "Fight Cave",
		description = ENABLES + "in the Tzhaar Fight Cave",
		titleSection = "minigamesTitle"
	)
	default boolean fightCaveMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "gauntletMetronome",
		name = "Gauntlet",
		description = ENABLES + "in the Gauntlet",
		titleSection = "minigamesTitle"
	)
	default boolean gauntletMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hallowedSepulchreMetronome",
		name = "Hallowed Sepulchre",
		description = ENABLES + "in the Hallowed Sepulchre",
		titleSection = "minigamesTitle"
	)
	default boolean hallowedSepulchreMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "infernoMetronome",
		name = "Inferno",
		description = ENABLES + "in the Inferno",
		titleSection = "minigamesTitle"
	)
	default boolean infernoMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pestControlMetronome",
		name = "Pest Control",
		description = ENABLES + "at Pest Control",
		titleSection = "minigamesTitle"
	)
	default boolean pestControlMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pyramidPlunderMetronome",
		name = "Pyramid Plunder",
		description = ENABLES + "in Pyramid Plunder",
		titleSection = "minigamesTitle"
	)
	default boolean pyramidPlunderMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "titheFarmMetronome",
		name = "Tithe Farm",
		description = ENABLES + "at Tithe Farm",
		titleSection = "minigamesTitle"
	)
	default boolean titheFarmMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "volcanicMineMetronome",
		name = "Volcanic Mine",
		description = ENABLES + "in Volcanic Mine",
		titleSection = "minigamesTitle"
	)
	default boolean volcanicMineMetronome()
	{
		return true;
	}

	// Raids

	@ConfigItem(
		keyName = "chambersMetronome",
		name = "Chamber of Xeric",
		description = ENABLES + "in Chambers of Xeric",
		titleSection = "raidsTitle"
	)
	default boolean chambersMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "theatreMetronome",
		name = "Theatre of Blood",
		description = ENABLES + "in the Theatre of Blood",
		titleSection = "raidsTitle"
	)
	default boolean theatreMetronome()
	{
		return true;
	}

	// Slayer

	@ConfigItem(
		keyName = "catacombsMetronome",
		name = "Catacombs of Kourend",
		description = ENABLES + "in the Catacombs of Kourend",
		titleSection = "slayerTitle"
	)
	default boolean catacombsMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "fremennikSlayerMetronome",
		name = "Fremennik Slayer Dungeon",
		description = ENABLES + "in the Fremennik Slayer Dungeon",
		titleSection = "slayerTitle"
	)
	default boolean fremennikSlayerMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "slayerTowerMetronome",
		name = "Slayer Tower",
		description = ENABLES + "in the Slayer Tower",
		titleSection = "slayerTitle"
	)
	default boolean slayerTowerMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "strongholdCaveMetronome",
		name = "Stronghold Cave",
		description = ENABLES + "in the Stronghold Slayer Cave",
		titleSection = "slayerTitle"
	)
	default boolean strongholdCaveMetronome()
	{
		return true;
	}

	// Other

	@ConfigItem(
		keyName = "tickManipMetronome",
		name = "Tick Manipulation Items",
		description = ENABLES + "while tick manipulation items are in your inventory",
		titleSection = "otherTitle"
	)
	default boolean tickManipMetronome()
	{
		return true;
	}

	@ConfigItem(
		keyName = "pvpMetronome",
		name = "PvP",
		description = ENABLES + "in all PvP situations",
		titleSection = "otherTitle"
	)
	default boolean pvpMetronome()
	{
		return true;
	}
}
