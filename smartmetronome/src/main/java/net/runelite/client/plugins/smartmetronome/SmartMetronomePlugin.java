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
import net.runelite.client.events.ConfigChanged;
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
	static final String CONFIG_GROUP = "smartMetronome";

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

	// Bosses
	private final Map<Integer, Integer> ABYSSAL_SIRE_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> CERB_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> COMMANDER_ZILYANA_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> CORP_BEAST_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> DKS_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> GENERAL_GRAARDOR_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> GIANT_MOLE_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> KRIL_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> KALPHITE_QUEEN_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> KREE_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> SARACHNIS_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> THERMY_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> WINTERTODT_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> ZALCANO_REGIONS = new HashMap<>();

	// Minigames
	private final Map<Integer, Integer> BLAST_FURNACE_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> BRIMHAVEN_AGILITY_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> PYRAMID_PLUNDER_REGIONS = new HashMap<>();

	// Slayer
	private final Map<Integer, Integer> CATACOMBS_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> STRONGHOLD_CAVE_REGIONS = new HashMap<>();
	private final Map<Integer, Integer> FREMENNIK_SLAYER_REGIONS = new HashMap<>();

	@Override
	protected void startUp()
	{
		setMetronome();
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

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP))
		{
			previousID = -1;
			previousPlane = -1;
		}
	}

	// Bosses

	public void setABYSSAL_SIRE_REGIONS()
	{
		ABYSSAL_SIRE_REGIONS.put(11851, 0);
		ABYSSAL_SIRE_REGIONS.put(11850, 0);
		ABYSSAL_SIRE_REGIONS.put(12363, 0);
		ABYSSAL_SIRE_REGIONS.put(12362, 0);
	}

	public void setCERB_REGIONS()
	{
		CERB_REGIONS.put(4883, 0);
		CERB_REGIONS.put(5140, 0);
		CERB_REGIONS.put(5395, 0);
	}

	public void setCOMMANDER_ZILYANA_REGIONS()
	{
		COMMANDER_ZILYANA_REGIONS.put(11602, 0);
	}

	public void setCORP_BEAST_REGIONS()
	{
		CORP_BEAST_REGIONS.put(11842, 2);
		CORP_BEAST_REGIONS.put(11844, 2);
	}

	public void setDKS_REGIONS()
	{
		DKS_REGIONS.put(11588, 0);
		DKS_REGIONS.put(11589, 0);
	}

	public void setGENERAL_GRAARDOR_REGIONS()
	{
		GENERAL_GRAARDOR_REGIONS.put(11347, 2);
	}

	public void setGIANT_MOLE_REGIONS()
	{
		GIANT_MOLE_REGIONS.put(6993, 0);
		GIANT_MOLE_REGIONS.put(6992, 0);
	}

	public void setKRIL_REGIONS()
	{
		KRIL_REGIONS.put(11603, 2);
	}

	public void setKALPHITE_QUEEN_REGIONS()
	{
		KALPHITE_QUEEN_REGIONS.put(13972, 0);
	}

	public void setKREE_REGIONS()
	{
		KREE_REGIONS.put(11346, 2);
	}

	public void setSARACHNIS_REGIONS()
	{
		SARACHNIS_REGIONS.put(7322, 0);
	}

	public void setTHERMY_REGIONS()
	{
		THERMY_REGIONS.put(9363, 0);
		THERMY_REGIONS.put(9619, 0);
	}

	public void setWINTERTODT_REGIONS()
	{
		WINTERTODT_REGIONS.put(6462, 0);
	}

	public void setZALCANO_REGIONS()
	{
		ZALCANO_REGIONS.put(12126, 0);
	}

	// Minigames

	public void setBLAST_FURNACE_REGIONS()
	{
		BLAST_FURNACE_REGIONS.put(7757, 0);
	}

	public void setBRIMHAVEN_AGILITY_REGIONS()
	{
		BRIMHAVEN_AGILITY_REGIONS.put(11157, 3);
	}

	public void setPYRAMID_PLUNDER_REGIONS()
	{
		PYRAMID_PLUNDER_REGIONS.put(7749, 0);
	}

	// Slayer

	public void setCATACOMBS_REGIONS()
	{
		CATACOMBS_REGIONS.put(6556, 0);
		CATACOMBS_REGIONS.put(6557, 0);
		CATACOMBS_REGIONS.put(6812, 0);
		CATACOMBS_REGIONS.put(6813, 0);
	}

	public void setSTRONGHOLD_CAVE_REGIONS()
	{
		STRONGHOLD_CAVE_REGIONS.put(9624, 0);
		STRONGHOLD_CAVE_REGIONS.put(9625, 0);
		STRONGHOLD_CAVE_REGIONS.put(9880, 0);
		STRONGHOLD_CAVE_REGIONS.put(9881, 0);
	}

	public void setFREMENNIK_SLAYER_REGIONS()
	{
		FREMENNIK_SLAYER_REGIONS.put(11907, 0);
		FREMENNIK_SLAYER_REGIONS.put(10908, 0);
		FREMENNIK_SLAYER_REGIONS.put(11164, 0);
	}

	// Instanced Bosses
	private static final Set<Integer> ALCHEMICAL_HYDRA_REGIONS = Set.of(
		5536
	);

	private static final Set<Integer> BARROWS_REGIONS = Set.of(
		14231
	);

	private static final Set<Integer> GROTESQUE_GUARDIANS_REGIONS = Set.of(
		6727
	);

	private static final Set<Integer> NIGHTMARE_REGIONS = Set.of(
		15515
	);

	private static final Set<Integer> SKOTIZO_REGIONS = Set.of(
		6810
	);

	private static final Set<Integer> VORKATH_REGIONS = Set.of(
		9023
	);

	private static final Set<Integer> ZULRAH_REGIONS = Set.of(
		9007
	);

	// Instanced Minigames
	private static final Set<Integer> FIGHT_CAVE_REGIONS = Set.of(
		9551
	);

	private static final Set<Integer> GAUNTLET_REGIONS = Set.of(
		7512, 7768
	);

	private static final Set<Integer> HALLOWED_SEPULCHRE_REGIONS = Set.of(
		8797, 9051, 9052, 9053, 9054, 9309, 9563, 9821, 10074, 10075, 10077
	);

	private static final Set<Integer> INFERNO_REGIONS = Set.of(
		9043
	);

	private static final Set<Integer> PEST_CONTROL_REGIONS = Set.of(
		10536
	);

	private static final Set<Integer> TITHE_FARM_REGIONS = Set.of(
		7222
	);

	private static final Set<Integer> VOLCANIC_MINE_REGIONS = Set.of(
		15263, 15262
	);

	// Slayer

	private static final Set<Integer> SLAYER_TOWER_REGIONS = Set.of(
		13623, 13723
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
		9565, // Sepulchre Lobby
		13366, 13367, 13368, 13622, 13624, 13878, 13879, 13880, // Slayer Tower
		13466, 13467, 13722, 13978, 13979, // Slayer Tower Basement
		6710, 6711, 6965, 6966, 6967, 6968, 7221, 7223, 7224, 7478, 7479, // Tithe Farm
		15008, 15264, 15519, 15775, // Volcanic Mine
		6205, 6461, 6717, // Wintertodt
		8495, 8496, 8751, 8752, 9008, 9263, 9264, 9265 // Zulrah
	);

	public void setMetronome()
	{
		// Bosses
		setABYSSAL_SIRE_REGIONS();
		setCERB_REGIONS();
		setCOMMANDER_ZILYANA_REGIONS();
		setCORP_BEAST_REGIONS();
		setDKS_REGIONS();
		setGENERAL_GRAARDOR_REGIONS();
		setGIANT_MOLE_REGIONS();
		setKRIL_REGIONS();
		setKALPHITE_QUEEN_REGIONS();
		setKREE_REGIONS();
		setSARACHNIS_REGIONS();
		setTHERMY_REGIONS();
		setWINTERTODT_REGIONS();
		setZALCANO_REGIONS();

		// Minigames
		setBLAST_FURNACE_REGIONS();
		setBRIMHAVEN_AGILITY_REGIONS();
		setPYRAMID_PLUNDER_REGIONS();

		// Slayer
		setCATACOMBS_REGIONS();
		setFREMENNIK_SLAYER_REGIONS();
		setSTRONGHOLD_CAVE_REGIONS();
	}

	private boolean muteMetronomeIDs()
	{
		return MUTE_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID());
	}

	private boolean shouldActivateMetronome()
	{
		// Bosses
		if (config.abyssalSireMetronome() && ABYSSAL_SIRE_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (ABYSSAL_SIRE_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.cerberusMetronome() && CERB_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (CERB_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.commanderZilyanaMetronome() && COMMANDER_ZILYANA_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (COMMANDER_ZILYANA_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.corpBeastMetronome() && CORP_BEAST_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (CORP_BEAST_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.dksMetronome() && DKS_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (DKS_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.generalGraardorMetronome() && GENERAL_GRAARDOR_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (GENERAL_GRAARDOR_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.giantMoleMetronome() && GIANT_MOLE_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (GIANT_MOLE_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.krilMetronome() && KRIL_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (KRIL_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.kqMetronome() && KALPHITE_QUEEN_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (KALPHITE_QUEEN_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.kreeMetronome() && KREE_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (KREE_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.sarachnisMetronome() && SARACHNIS_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (SARACHNIS_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.thermyMetronome() && THERMY_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (THERMY_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.wintertodtMetronome() && WINTERTODT_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (WINTERTODT_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.zalcanoMetronome() && ZALCANO_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (ZALCANO_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		// Minigames

		else if (config.blastFurnaceMetronome() && BLAST_FURNACE_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (BLAST_FURNACE_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.brimhavenAgilityMetronome() && BRIMHAVEN_AGILITY_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (BRIMHAVEN_AGILITY_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.pyramidPlunderMetronome() && PYRAMID_PLUNDER_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (PYRAMID_PLUNDER_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		// Slayer

		else if (config.catacombsMetronome() && CATACOMBS_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (CATACOMBS_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.fremennikSlayerMetronome() && FREMENNIK_SLAYER_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (FREMENNIK_SLAYER_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
			{
				return true;
			}
		}

		else if (config.strongholdCaveMetronome() && STRONGHOLD_CAVE_REGIONS.containsKey(client.getLocalPlayer().getWorldLocation().getRegionID()))
		{
			if (STRONGHOLD_CAVE_REGIONS.get(client.getLocalPlayer().getWorldLocation().getRegionID()) == client.getLocalPlayer().getWorldLocation().getPlane())
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

			// Instanced Bosses
			else if (config.alchemicalHydraMetronome() && ALCHEMICAL_HYDRA_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.barrowsMetronome() && BARROWS_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.grotesqueGuardiansMetronome() && GROTESQUE_GUARDIANS_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.nightmareMetronome() && NIGHTMARE_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.skotizoMetronome() && SKOTIZO_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.vorkathMetronome() && VORKATH_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.zulrahMetronome() && ZULRAH_REGIONS.contains(mapregion))
			{
				return true;
			}

			// Instanced Minigames
			else if (config.fightCaveMetronome() && FIGHT_CAVE_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.gauntletMetronome() && GAUNTLET_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.hallowedSepulchreMetronome() && HALLOWED_SEPULCHRE_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.infernoMetronome() && INFERNO_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.pestControlMetronome() && PEST_CONTROL_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.titheFarmMetronome() && TITHE_FARM_REGIONS.contains(mapregion))
			{
				return true;
			}

			else if (config.volcanicMineMetronome() && VOLCANIC_MINE_REGIONS.contains(mapregion))
			{
				return true;
			}

			// Slayer
			else if (config.slayerTowerMetronome() && SLAYER_TOWER_REGIONS.contains(mapregion))
			{
				// Not actually an instance but we want each plane
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

		return (config.chambersMetronome() && inCoX)
			|| (config.theatreMetronome() && inToB)
			|| (config.pvpMetronome() && inPvP)
			|| (config.barbAssaultMetronome() && inBA);
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
		checkInventoryItems(itemContainer);
	}

	public void checkInventoryItems(ItemContainer itemContainer)
	{
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
			shouldTickTock = (shouldActivateMetronome() || isInInstance());
		}

		if (config.overrideMetronome() || shouldTickTock || (config.tickManipMetronome() && inventoryItems) || varbits())
		{
			tickTock();
		}

	}
}
