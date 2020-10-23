/*
 * Copyright (c) 2020, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.mogcounter;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Player;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Marks of Grace Counter",
	description = "Counts Marks of Grace spawns",
	tags = {"marks", "grace", "agility", "counter"},
	enabledByDefault = false,
	type = PluginType.SKILLING
)
public class MOGCounterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MOGCounterConfig config;

	@Inject
	private MOGCounterOverlay mogOverlay;

	@Getter
	private MOGSession mogSession;

	@Provides
	MOGCounterConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MOGCounterConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(mogOverlay);
		mogSession = new MOGSession();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(mogOverlay);
		mogSession = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		switch (gameStateChanged.getGameState())
		{
			case HOPPING:
			case LOGIN_SCREEN:
				mogSession = null;
				break;
			case LOGGED_IN:
				if (mogSession == null)
				{
					mogSession = new MOGSession();
				}
				break;
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		final TileItem item = itemSpawned.getItem();
		if (item.getId() == ItemID.MARK_OF_GRACE)
		{
			WorldPoint wp = itemSpawned.getTile().getWorldLocation();
			Player player = client.getLocalPlayer();
			if (player != null)
			{
				if (wp.equals(player.getWorldLocation()))
				{
					mogSession.addIgnoreTile(wp);
				}
				else
				{
					mogSession.addMarkTile(wp, item.getQuantity());
				}
			}
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		final TileItem item = itemDespawned.getItem();
		if (item.getId() == ItemID.MARK_OF_GRACE)
		{
			WorldPoint wp = itemDespawned.getTile().getWorldLocation();
			Player player = client.getLocalPlayer();
			if (player != null)
			{
				if (wp.equals(player.getWorldLocation()))
				{
					mogSession.removeIgnoreTile(wp);
				}
				mogSession.removeMarkTile(wp);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (mogSession != null)
		{
			mogSession.checkMarkSpawned();
		}
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked overlayMenuClicked)
	{
		OverlayMenuEntry overlayMenuEntry = overlayMenuClicked.getEntry();
		if (overlayMenuEntry.getMenuOpcode() == MenuOpcode.RUNELITE_OVERLAY
			&& overlayMenuClicked.getOverlay() == mogOverlay)
		{
			switch (overlayMenuClicked.getEntry().getOption())
			{
				case MOGCounterOverlay.MARK_CLEAR:
					mogSession.clearCounters();
					break;
				case MOGCounterOverlay.GROUND_RESET:
					mogSession.clearSpawnedMarks();
					break;
			}
		}
	}
}
