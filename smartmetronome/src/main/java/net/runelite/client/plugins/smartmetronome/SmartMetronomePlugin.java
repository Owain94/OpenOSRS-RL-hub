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
import java.util.HashMap;
import java.util.Map;
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

	private int previousID;
	private int previousPlane;
	private int tickCounter = 0;
	private boolean inventoryItems;
	private boolean shouldTickTock;
	private boolean shouldTock = false;
	private final int[] herbSet = {249, 255, 251, 253};
	private final int[] vambSet = {1065, 2487, 2489, 2491};
	private final Map<Integer, Integer> METRONOME_REGIONS = new HashMap<>();

	@Override
	protected void startUp()
	{
		setRegions();
		previousID = -1;
		previousPlane = -1;
		inventoryItems = false;
	}

	@Override
	protected void shutDown()
	{
		tickCounter = 0;
		shouldTock = false;
	}

	public void setRegions()
	{
		//Abyssal Sire
		METRONOME_REGIONS.put(11851, 0);
		METRONOME_REGIONS.put(11850, 0);
		METRONOME_REGIONS.put(12363, 0);
		METRONOME_REGIONS.put(12362, 0);

		// Blast Furnace
		METRONOME_REGIONS.put(7757, 0);

		// Brimhaven Agility
		METRONOME_REGIONS.put(11157, 3);

		// Cerberus
		METRONOME_REGIONS.put(4883, 0);
		METRONOME_REGIONS.put(5140, 0);
		METRONOME_REGIONS.put(5395, 0);

		// Commander Zilyana
		METRONOME_REGIONS.put(11602, 0);

		// Corp
		METRONOME_REGIONS.put(11842, 2);
		METRONOME_REGIONS.put(11844, 2);

		// DKs
		METRONOME_REGIONS.put(11588, 0);
		METRONOME_REGIONS.put(11589, 0);

		// General Graardor
		METRONOME_REGIONS.put(11347, 2);

		// Giant Mole
		METRONOME_REGIONS.put(6993, 0);
		METRONOME_REGIONS.put(6992, 0);

		// K'ril Tsutsaroth
		METRONOME_REGIONS.put(11603, 2);

		// Kalphite Queen
		METRONOME_REGIONS.put(13972, 0);

		// Kree'arra
		METRONOME_REGIONS.put(11346, 2);

		// Pyramid Plunder
		METRONOME_REGIONS.put(7749, 0);

		// Sarachnis
		METRONOME_REGIONS.put(7322, 0);

		// Thermonuclear Smoke Devil
		METRONOME_REGIONS.put(9363, 0);
		METRONOME_REGIONS.put(9619, 0);

		// Wintertodt
		METRONOME_REGIONS.put(6462, 0);

		// Zalcano
		METRONOME_REGIONS.put(12126, 0);
	}

	private static final Set<Integer> INSTANCE_REGIONS = Set.of(
		5536, // Alchemical Hydra
		14231, // Barrows
		9551, // Fight Cave
		7512, 7768, // Gauntlet
		6727, // Grotesque Guardians
		8797, 9051, 9052, 9053, 9054, 9309, 9563, 9821, 10074, 10075, 10077, // Hallowed Sepulchre
		9043, // Inferno
		15515, // Nightmare
		10536, // Pest Control
		6810, // Skotizo
		7222, // Tithe Farm
		15263, 15262, // Volcanic Mine
		9023, // Vorkath
		9007 // Zulrah
	);

	private static final Set<Integer> MUTE_REGIONS = Set.of(
		12106, 12107, // Abyss
		11412, 11413, 11414, 10901, 10899, 10900, 10645, // Agility Arena
		13619, 13874, 13875, 13876, 14130, 14488, 14232, 14487,  // Barrows
		5139, // Cerberus lobby
		11586, 11587, 11841, 11843, 11845, 12097, 12098, 12099, 12100, 12101, // Corp
		9808, 9807, 9552, // Fight Cave
		5535, 5280, 5279, 5023, 5278, // Hydra
		14387, 14388, 14132, // Meyerditch/Darkmeyer
		10537, // Pest Control
		7492, 7748, // Pyramid Plunder
		7323, // Sarachnis
		9565, // Sepulchre Lobby
		6710, 6711, 7224, 7478, 7479, 7223, 6965, 6967, 6966, 7221, // Tithe Farm
		15008, 15264, 15519, 15775, // Volcanic Mine
		6205, 6461, 6717, // Wintertodt
		8495, 8496, 8751, 8752, 9008, 9263, 9264, 9265 // Zulrah
	);

	private boolean muteMetronomeIDs()
	{
		return MUTE_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID());
	}

	private boolean metronomeIDs()
	{
		if (METRONOME_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (METRONOME_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		muteMetronomeIDs();
		return false;
	}

	private boolean isInInstance()
	{
		for (int mapregion : client.getMapRegions())
		{
			if (muteMetronomeIDs())
			{
				return false;
			}

			if (INSTANCE_REGIONS.contains(mapregion))
			{
				return true;
			}
		}
		return false;
	}

	private boolean varbits()
	{
		boolean inCoX = client.getVar(Varbits.IN_RAID) == 1;
		boolean inBA = client.getVar(Varbits.IN_GAME_BA) == 1;
		boolean inPvP = client.getVar(Varbits.PVP_SPEC_ORB) == 1;
		boolean inToB = client.getVar(Varbits.THEATRE_OF_BLOOD) == 2;

		return inCoX || inBA || inPvP || inToB;
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

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY))
		{
			return;
		}

		ItemContainer itemContainer = event.getItemContainer();
		inventoryItems = false;

		for (int Herb : herbSet)
		{
			if (itemContainer.contains(Herb) && itemContainer.contains(ItemID.SWAMP_TAR) && itemContainer.contains(ItemID.PESTLE_AND_MORTAR))
			{
				inventoryItems = true;
				break;
			}
		}

		for (int Vamb : vambSet)
		{
			if (itemContainer.contains(Vamb) && itemContainer.contains(ItemID.KEBBIT_CLAWS))
			{
				inventoryItems = true;
				break;
			}
		}

		if (itemContainer.contains(ItemID.KNIFE) && itemContainer.contains(ItemID.TEAK_LOGS))
		{
			inventoryItems = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (config.tickCount() == 0)
		{
			return;
		}

		if (client.getLocalPlayer().getWorldLocation().getRegionID() != previousID
			|| client.getLocalPlayer().getWorldLocation().getPlane() != previousPlane)
		{
			previousID = client.getLocalPlayer().getWorldLocation().getRegionID();
			previousPlane = client.getLocalPlayer().getWorldLocation().getPlane();
			shouldTickTock = (metronomeIDs() || isInInstance());
		}

		if (config.smartMetronome() && (shouldTickTock || inventoryItems || varbits()))
		{
			tickTock();
		}

		else if (!config.smartMetronome())
		{
			tickTock();
		}
	}
}
