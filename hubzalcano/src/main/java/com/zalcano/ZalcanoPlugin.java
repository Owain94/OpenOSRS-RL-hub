/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package com.zalcano;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Hub Zalcano",
	description = "Zalcano Utility Plugin",
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class ZalcanoPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ZalcanoConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ZalcanoOverlay zalcanoOverlay;

	private Widget hpBar;
	private int hpBarWidgetId = 19857428;
	private boolean hpBarHidden = true;

	@Getter
	private int zalcanoState;
	@Getter
	private int throwingHp;
	@Getter
	private int miningHp;

	private List<Player> playersInSight;
	@Getter
	private List<Player> playersParticipating = new ArrayList<>();

	private final List<WorldPoint> excludedWorldPoints = new ArrayList<>();
	private static final int ZALCANO_REGION = 12126;

	@Getter
	private int shieldDamageDealt;
	@Getter
	private final int minimumDamageLootShield = 30;
	@Getter
	private final int minimumDamageUniquesShield = 50;

	@Getter
	private int miningDamageDealt;
	@Getter
	private final int minimumDamageLootMining = 30;
	@Getter
	private final int minimumDamageUniquesMining = 50;

	@Getter
	private float chanceOfToolSeedTable = 0;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(zalcanoOverlay);

		addExcludedWorldPoints();

		playersParticipating = new ArrayList<>();
		playersInSight = new ArrayList<>(client.getPlayers());
		// Clear null values
		while (true) if (!playersInSight.remove(null)) break;

		shieldDamageDealt = 0;
		miningDamageDealt = 0;
		zalcanoState = ZalcanoStates.THROWING;

	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(zalcanoOverlay);
	}

	@Subscribe
	public void onWidgetHiddenChanged(WidgetHiddenChanged widgetHiddenChanged)
	{
		Widget widget = widgetHiddenChanged.getWidget();
		if (widget.getId() == hpBarWidgetId)
		{
			hpBarHidden = widget.isHidden();
			if (!hpBarHidden) hpBar = widget;
		}
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned playerDespawned)
	{
		playersParticipating.remove(playerDespawned.getPlayer());
		playersInSight.remove(playerDespawned.getPlayer());
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned playerSpawned)
	{
		playersInSight.add(playerSpawned.getPlayer());
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		filterPlayersAtGate(playersInSight);
		updateZalcanoStatus();
		updateZalcanoHealth();
	}

	private void updateZalcanoStatus()
	{
		for (NPC npc: client.getNpcs())
		{
			if (npc.getId() == ZalcanoStates.THROWING)
			{
				if (zalcanoState == ZalcanoStates.MINING)
				{
					// Reset shield hp after transition from mining to throwing
					throwingHp = 300;
				}
				zalcanoState = npc.getId();
			}
			if (npc.getId() == ZalcanoStates.MINING)
			{
				zalcanoState = npc.getId();
			}
		}
	}

	private void updateZalcanoHealth() {
		if (!hpBarHidden)
		{
			if (zalcanoState == ZalcanoStates.THROWING)
			{
				throwingHp = parseHealthbar(hpBar.getText());
			}
			else if (zalcanoState == ZalcanoStates.MINING)
			{
				miningHp = parseHealthbar(hpBar.getText());
			}
		}
		else
		{
			for (NPC npc : client.getNpcs())
			{
				int healthRatio = npc.getHealthRatio();
				if (healthRatio >= 0)
				{
					if (npc.getId() == ZalcanoStates.THROWING)
					{
						throwingHp = healthRatio * 3;
					}
					else if (npc.getId() == ZalcanoStates.MINING)
					{
						miningHp = healthRatio * 10;
					}
				}
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (npc.getId() == ZalcanoStates.THROWING || npc.getId() == ZalcanoStates.MINING)
		{
			if (zalcanoState == ZalcanoStates.DEAD)
			{
				shieldDamageDealt = 0;
				miningDamageDealt = 0;
				miningHp = 1000;
				throwingHp = 300;
				chanceOfToolSeedTable = 0;
			}
			zalcanoState = npc.getId();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
		if (npc.isDead() && npc.getId() == ZalcanoStates.MINING)
		{
			zalcanoState = ZalcanoStates.DEAD;
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		if (hitsplatApplied.getHitsplat().isMine())
		{
			if (hitsplatApplied.getActor() instanceof NPC)
			{
				NPC npc = (NPC) hitsplatApplied.getActor();
				if (npc.getId() == ZalcanoStates.THROWING)
				{
					shieldDamageDealt += hitsplatApplied.getHitsplat().getAmount();
				}
				else if (npc.getId() == ZalcanoStates.MINING)
				{
					miningDamageDealt += hitsplatApplied.getHitsplat().getAmount();
				}
				calculateToolSeedTableChances();
			}
		}
	}

	private void calculateToolSeedTableChances()
	{
		int cap = 1000;
		float points =  miningDamageDealt + (shieldDamageDealt * 2);
		if (points > cap) points = cap;
		float contribution = points / 2800;
		chanceOfToolSeedTable = contribution / 200;
	}

	private int parseHealthbar(String healthbar)
	{
		return Integer.parseInt(healthbar.substring(0, healthbar.indexOf("/") - 1));
	}

	private void filterPlayersAtGate(List<Player> players)
	{
		for(Player p : players) {
			WorldPoint playerLocation = p.getWorldLocation();
			if (excludedWorldPoints.contains(playerLocation))
			{
				playersParticipating.remove(p);
			}
			else
			{
				if (!playersParticipating.contains(p)) playersParticipating.add(p);
			}
		}
	}

	private void addExcludedWorldPoints() {
		int plane = 0;
		int minExcludedX = 3033;
		int maxExcludedX = 3034;

		int minExcludedY = 6063;
		int maxExcludedY = 6065;

		for(int x = minExcludedX; x <= maxExcludedX; x++)
		{
			for(int y =minExcludedY; y <= maxExcludedY; y++)
			{
				excludedWorldPoints.add(new WorldPoint(x, y, plane));
			}
		}
	}

	@Provides
	ZalcanoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZalcanoConfig.class);
	}

	public boolean playerInZalcanoArea()
	{
		return Objects.requireNonNull(client.getLocalPlayer()).getWorldLocation().getRegionID() == ZALCANO_REGION;
	}
}
