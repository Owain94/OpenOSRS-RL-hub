/*
 * Copyright (c) 2020 Adam <Adam@sigterm.info>
 * Copyright (c) 2020 0anth <https://github.com/0anth/damage-counter/>
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
package com.damagecounter;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import static net.runelite.api.NpcID.*;
import net.runelite.api.Player;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.ws.PartyMember;
import net.runelite.client.ws.PartyService;
import net.runelite.client.ws.WSClient;
import net.runelite.api.ChatMessageType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Damage Counter",
	description = "Counts damage done to bosses",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
@Slf4j
public class DamageCounterPlugin extends Plugin
{
	private static final ImmutableSet<Integer> BOSSES = ImmutableSet.of(
		ABYSSAL_SIRE, ABYSSAL_SIRE_5887, ABYSSAL_SIRE_5888, ABYSSAL_SIRE_5889, ABYSSAL_SIRE_5890, ABYSSAL_SIRE_5891, ABYSSAL_SIRE_5908,
		ALCHEMICAL_HYDRA, ALCHEMICAL_HYDRA_8616, ALCHEMICAL_HYDRA_8617, ALCHEMICAL_HYDRA_8618, ALCHEMICAL_HYDRA_8619, ALCHEMICAL_HYDRA_8620, ALCHEMICAL_HYDRA_8621, ALCHEMICAL_HYDRA_8622,
		AHRIM_THE_BLIGHTED, DHAROK_THE_WRETCHED, GUTHAN_THE_INFESTED, KARIL_THE_TAINTED, TORAG_THE_CORRUPTED, VERAC_THE_DEFILED,
		BRYOPHYTA,
		CALLISTO, CALLISTO_6609,
		CERBERUS, CERBERUS_5863, CERBERUS_5866,
		CHAOS_ELEMENTAL, CHAOS_ELEMENTAL_6505,
		CHAOS_FANATIC,
		COMMANDER_ZILYANA, COMMANDER_ZILYANA_6493,
		CORPOREAL_BEAST,
		CRAZY_ARCHAEOLOGIST,
		CRYSTALLINE_HUNLLEF, CRYSTALLINE_HUNLLEF_9022, CRYSTALLINE_HUNLLEF_9023, CRYSTALLINE_HUNLLEF_9024,
		DAGANNOTH_SUPREME, DAGANNOTH_PRIME, DAGANNOTH_REX, DAGANNOTH_SUPREME_6496, DAGANNOTH_PRIME_6497, DAGANNOTH_REX_6498,
		DUSK, DAWN, DUSK_7851, DAWN_7852, DAWN_7853, DUSK_7854, DUSK_7855,
		GENERAL_GRAARDOR, GENERAL_GRAARDOR_6494,
		GIANT_MOLE, GIANT_MOLE_6499,
		HESPORI,
		KALPHITE_QUEEN, KALPHITE_QUEEN_963, KALPHITE_QUEEN_965, KALPHITE_QUEEN_4303, KALPHITE_QUEEN_4304, KALPHITE_QUEEN_6500, KALPHITE_QUEEN_6501,
		KING_BLACK_DRAGON, KING_BLACK_DRAGON_2642, KING_BLACK_DRAGON_6502,
		KRAKEN, KRAKEN_6640, KRAKEN_6656,
		KREEARRA, KREEARRA_6492,
		KRIL_TSUTSAROTH, KRIL_TSUTSAROTH_6495,
		THE_MIMIC, THE_MIMIC_8633,
		THE_NIGHTMARE, THE_NIGHTMARE_9426, THE_NIGHTMARE_9427, THE_NIGHTMARE_9428, THE_NIGHTMARE_9429, THE_NIGHTMARE_9430, THE_NIGHTMARE_9431, THE_NIGHTMARE_9432, THE_NIGHTMARE_9433,
		OBOR,
		SARACHNIS,
		SCORPIA,
		SKOTIZO,
		THERMONUCLEAR_SMOKE_DEVIL,
		TZKALZUK,
		TZTOKJAD, TZTOKJAD_6506,
		VENENATIS, VENENATIS_6610,
		VETION, VETION_REBORN,
		VORKATH, VORKATH_8058, VORKATH_8059, VORKATH_8060, VORKATH_8061,
		ZALCANO, ZALCANO_9050,
		ZULRAH, ZULRAH_2043, ZULRAH_2044,

		// ToB
		THE_MAIDEN_OF_SUGADINTI, THE_MAIDEN_OF_SUGADINTI_8361, THE_MAIDEN_OF_SUGADINTI_8362, THE_MAIDEN_OF_SUGADINTI_8363, THE_MAIDEN_OF_SUGADINTI_8364, THE_MAIDEN_OF_SUGADINTI_8365,
		PESTILENT_BLOAT,
		NYLOCAS_VASILIAS, NYLOCAS_VASILIAS_8355, NYLOCAS_VASILIAS_8356, NYLOCAS_VASILIAS_8357,
		SOTETSEG, SOTETSEG_8388,
		XARPUS_8340, XARPUS_8341,
		VERZIK_VITUR_8370,
		VERZIK_VITUR_8372,
		VERZIK_VITUR_8374,

		// CoX
		TEKTON, TEKTON_7541, TEKTON_7542, TEKTON_ENRAGED, TEKTON_ENRAGED_7544, TEKTON_7545,
		VESPULA, VESPULA_7531, VESPULA_7532, ABYSSAL_PORTAL,
		VANGUARD, VANGUARD_7526, VANGUARD_7527, VANGUARD_7528, VANGUARD_7529,
		GREAT_OLM, GREAT_OLM_LEFT_CLAW, GREAT_OLM_RIGHT_CLAW_7553, GREAT_OLM_7554, GREAT_OLM_LEFT_CLAW_7555,
		DEATHLY_RANGER, DEATHLY_MAGE,
		MUTTADILE, MUTTADILE_7562, MUTTADILE_7563,
		VASA_NISTIRIO, VASA_NISTIRIO_7567,
		GUARDIAN, GUARDIAN_7570, GUARDIAN_7571, GUARDIAN_7572,
		LIZARDMAN_SHAMAN_7573, LIZARDMAN_SHAMAN_7574,
		ICE_DEMON, ICE_DEMON_7585,
		SKELETAL_MYSTIC, SKELETAL_MYSTIC_7605, SKELETAL_MYSTIC_7606
	);
	private static final ImmutableSet<Integer> BARROWS = ImmutableSet.of(
		AHRIM_THE_BLIGHTED, DHAROK_THE_WRETCHED, GUTHAN_THE_INFESTED, KARIL_THE_TAINTED, TORAG_THE_CORRUPTED, VERAC_THE_DEFILED
	);
	private String npcName = null;
	NPC barrows = null;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PartyService partyService;

	@Inject
	private WSClient wsClient;

	@Inject
	private DpsOverlay dpsOverlay;

	@Inject
	private DamageCounterConfig damageCounterConfig;

	@Getter(AccessLevel.PACKAGE)
	private final Map<String, DpsMember> members = new ConcurrentHashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final DpsMember total = new DpsMember("Total");

	@Provides
	DamageCounterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DamageCounterConfig.class);
	}

	@Override
	protected void startUp()
	{
		total.reset();
		overlayManager.add(dpsOverlay);
		wsClient.registerMessage(DpsUpdate.class);
	}

	@Override
	protected void shutDown()
	{
		wsClient.unregisterMessage(DpsUpdate.class);
		overlayManager.remove(dpsOverlay);
		members.clear();
	}

	@Subscribe
	public void onPartyChanged(PartyChanged partyChanged)
	{
		members.clear();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		Player player = client.getLocalPlayer();
		Actor actor = hitsplatApplied.getActor();
		if (!(actor instanceof NPC))
		{
			return;
		}

		Hitsplat hitsplat = hitsplatApplied.getHitsplat();
		final int npcId = ((NPC) actor).getId();
		boolean isBoss = BOSSES.contains(npcId);
		boolean isBarrows = BARROWS.contains(npcId);

		if (!isBoss)
		{
			// only track bosses
			return;
		}

		if (hitsplat.isMine())
		{
			int hit = hitsplat.getAmount();
			// Update local member
			PartyMember localMember = partyService.getLocalMember();
			// If not in a party, user local player name
			final String name = localMember == null ? player.getName() : localMember.getName();
			DpsMember dpsMember = members.computeIfAbsent(name, DpsMember::new);
			dpsMember.addDamage(hit);

			// get the NPC and store it if we are attacking a barrows bro
			if (isBarrows)
			{
				barrows = client.getHintArrowNpc();
			}

			// broadcast damage
			if (localMember != null)
			{
				final DpsUpdate specialCounterUpdate = new DpsUpdate(hit);
				specialCounterUpdate.setMemberId(localMember.getMemberId());
				wsClient.send(specialCounterUpdate);
			}
			// apply to total
		}
		else if (hitsplat.isOthers())
		{
			if (actor != player.getInteracting())
			{
				// only track damage to npcs we are attacking, or is a nearby common boss
				return;
			}
			else if (isBarrows)
			{
				// only track barrows bros we are attacking
				return;
			}
			// apply to total
		}
		else
		{
			return;
		}

		total.addDamage(hitsplat.getAmount());
	}

	@Subscribe
	public void onDpsUpdate(DpsUpdate dpsUpdate)
	{
		if (partyService.getLocalMember().getMemberId().equals(dpsUpdate.getMemberId()))
		{
			return;
		}

		String name = partyService.getMemberById(dpsUpdate.getMemberId()).getName();
		if (name == null)
		{
			return;
		}

		DpsMember dpsMember = members.computeIfAbsent(name, DpsMember::new);
		dpsMember.addDamage(dpsUpdate.getHit());
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked event)
	{
		if (event.getEntry() == DpsOverlay.RESET_ENTRY)
		{
			members.clear();
			total.reset();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();

		if (npc.isDead() && (BOSSES.contains(npc.getId()) || npc == barrows))
		{
			npcName = npc.getName();
			reset();
		}
	}

	private void reset()
	{
		Player player = client.getLocalPlayer();
		PartyMember localMember = partyService.getLocalMember();
		// If not in a party, user local player name
		final String name = localMember == null ? player.getName() : localMember.getName();
		boolean sendToChat = damageCounterConfig.sendToChat();
		barrows = null;

		DpsMember total = getTotal();
		Duration elapsed = total.elapsed();
		long s = elapsed.getSeconds();
		String killTime;
		if (s >= 3600)
		{
			killTime = String.format("%dh %02dm %02ds", s / 3600, (s % 3600) / 60, (s % 60));
		}
		else
		{
			killTime = String.format("%dm %02ds", s / 60, (s % 60));
		}

		for (DpsMember dpsMember : members.values())
		{
			if (name.equals(dpsMember.getName()) && sendToChat)
			{
				double damageDone = dpsMember.getDamage();
				double damageTotal = total.getDamage();
				double damagePercent = damageDone / damageTotal;
				DecimalFormat df = new DecimalFormat("##%");
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Damage Counter: You dealt " + QuantityFormatter.formatNumber(dpsMember.getDamage()) + " (" + df.format(damagePercent) + ") damage to " + npcName + " in " + killTime, null);
			}

			dpsMember.reset();
		}

		npcName = null;
		total.reset();
	}
}
