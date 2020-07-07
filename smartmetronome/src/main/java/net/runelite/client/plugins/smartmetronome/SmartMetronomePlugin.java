/*
 * Copyright (c) 2020, Brooklyn <https://github.com/Broooklyn>
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

import com.google.inject.Provides;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.SoundEffectID;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Smart Metronome",
	description = "A metronome that automatically ticks when useful",
	tags = {"skilling", "skill", "minigame", "tick", "timers", "1t", "2t", "3t", "hub", "fishing", "sound", "brooklyn"},
	type = PluginType.UTILITY,
	enabledByDefault = false
)
public class SmartMetronomePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SmartMetronomeConfig config;

	@Provides
	SmartMetronomeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SmartMetronomeConfig.class);
	}

	private int tickCounter = 0;
	private boolean shouldTock = false;
	private boolean willTickManipulate;
	private final int[] herbSet = {249, 255, 251, 253};
	private final int[] vambSet = {1065, 2487, 2489, 2491};

	private static final Set<Integer> BOSS_REGIONS = Set.of(
		11851, 11850, 12363, 12362, // Abyssal Sire
		5536, // Alchemical Hydra
		4883, 5140, 5395, // Cerberus
		11602, // Commander Zilyana
		11588, 11589, // Dagannoth Kings
		11347, // General Graardor
		6993, 6992, // Giant Mole
		6727, // Grotesque Guardians
		11603, // K'ril Tsutsaroth
		13972, // Kalphite Queen
		11346, // Kree'arra
		15515, // Nightmare of Ashihama
		7322, // Sarachnis
		6810, // Skotizo
		9363, 9619, // Thermonuclear smoke devil
		9023, // Vorkath
		6462, // Wintertodt
		12126, // Zalcano
		9007 // Zulrah
	);

	private static final Set<Integer> MINIGAME_REGIONS = Set.of(
		10332, // Barbarian Assault
		14131, 14231, // Barrows
		7757, // Blast Furnace
		11157, // Brimhaven agility
		9520, // Castle Wars
		9551, // Fight Cave
		7512, 7768, // Gauntlet
		8797, 9051, 9052, 9053, 9054, 9309, 9563, 9821, 10074, 10075, 10077, // Hallowed Sepulchre
		9043, // Inferno
		13660, 13659, 13658, 13916, 13915, 13914, // Last Man Standing
		10536, // Pest Control
		7749, // Pyramid Plunder
		6968, // Tithe Farm
		15263, 15262 // Volcanic Mine
	);

	private static final Set<Integer> MUTE_METRONOME_IDS = Set.of(
		9565 // Sepulchre Lobby
	);

	private boolean muteMetronomeIDs()
	{
		return MUTE_METRONOME_IDS.contains(client.getLocalPlayer().getWorldLocation().getRegionID());
	}

	private boolean isInMinigame()
	{
		for (int mapregion : client.getMapRegions())
		{
			if (MINIGAME_REGIONS.contains(mapregion))
			{
				return true;
			}
			if (muteMetronomeIDs())
			{
				return false;
			}
		}
		return false;
	}

	private boolean isInBoss()
	{
		for (int mapregion : client.getMapRegions())
		{
			if (BOSS_REGIONS.contains(mapregion))
			{
				return true;
			}
			if (muteMetronomeIDs())
			{
				return false;
			}
		}
		return false;
	}

	public void tickTock()
	{
		if (++tickCounter % config.tickCount() == 0)
		{
			int previousVolume = client.getSoundEffectVolume();

			if (shouldTock && config.tockVolume() > 0)
			{
				client.setSoundEffectVolume(config.tockVolume());
				client.playSoundEffect(SoundEffectID.GE_DECREMENT_PLOP, config.tockVolume());
			}
			else if (config.tickVolume() > 0)
			{
				client.setSoundEffectVolume(config.tickVolume());
				client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP, config.tickVolume());
			}

			client.setSoundEffectVolume(previousVolume);

			shouldTock = !shouldTock;
		}
	}

	@Override
	protected void startUp()
	{
		willTickManipulate = false;
	}

	@Override
	protected void shutDown()
	{
		tickCounter = 0;
		shouldTock = false;
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY))
		{
			return;
		}

		ItemContainer itemContainer = event.getItemContainer();
		willTickManipulate = false;

		for (int Herb : herbSet)
		{
			for (int Vamb : vambSet)
			{
				if ((itemContainer.contains(Herb) && itemContainer.contains(ItemID.SWAMP_TAR))
					|| (itemContainer.contains(Vamb) && itemContainer.contains(ItemID.KEBBIT_CLAWS))
					|| (itemContainer.contains(ItemID.KNIFE) && itemContainer.contains(ItemID.TEAK_LOGS)))
				{
					willTickManipulate = true;
					break;
				}
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		boolean inCoX = client.getVar(Varbits.IN_RAID) == 1;
		boolean inToB = client.getVar(Varbits.THEATRE_OF_BLOOD) == 2;
		boolean inPvP = client.getVar(Varbits.PVP_SPEC_ORB) == 1;

		if (config.tickCount() == 0)
		{
			return;
		}
		if (config.smartMetronome() && (isInMinigame() || isInBoss() || inCoX || inToB || inPvP || willTickManipulate))
		{
			tickTock();
		}
		else if (!config.smartMetronome())
		{
			tickTock();
		}
	}
}
