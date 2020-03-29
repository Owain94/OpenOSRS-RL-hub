/*
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
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
package net.runelite.client.plugins.partypanel;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.account.AccountSession;
import net.runelite.client.account.SessionManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.partypanel.data.GameItem;
import net.runelite.client.plugins.partypanel.data.PartyPlayer;
import net.runelite.client.plugins.partypanel.data.Prayers;
import net.runelite.client.plugins.partypanel.ui.prayer.PrayerSprites;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ws.PartyService;
import net.runelite.client.ws.WSClient;
import net.runelite.http.api.ws.messages.party.UserJoin;
import net.runelite.http.api.ws.messages.party.UserPart;
import net.runelite.http.api.ws.messages.party.UserSync;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Party Panel",
	description = "Shows all kinds of information about your party members in a side panel",
	type = PluginType.UTILITY
)
@Slf4j
public class PartyPanelPlugin extends Plugin
{
	private static final BufferedImage ICON = ImageUtil.getResourceStreamFromClass(PartyPanelPlugin.class, "icon.png");

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private PartyPanelConfig config;

	@Inject
	private PartyService partyService;

	@Inject
	private SessionManager sessionManager;

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private SpriteManager spriteManager;

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private ItemManager itemManager;

	@Inject
	private WSClient wsClient;

	@Provides
	PartyPanelConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyPanelConfig.class);
	}

	@Getter
	private final Map<UUID, PartyPlayer> partyMembers = new HashMap<>();

	private NavigationButton navButton;
	private boolean addedButton = false;
	private PartyPanel panel;
	@Getter
	private PartyPlayer myPlayer = null;

	@Override
	protected void startUp()
	{
		panel = new PartyPanel(this);
		navButton = NavigationButton.builder()
			.tooltip("Party Panel")
			.icon(ICON)
			.priority(7)
			.panel(panel)
			.build();

		wsClient.registerMessage(PartyPlayer.class);

		// If there isn't already a session open, open one
		if (!wsClient.sessionExists())
		{
			AccountSession accountSession = sessionManager.getAccountSession();
			// Use the existing account session, if it exists, otherwise generate a new session id
			UUID uuid = accountSession != null ? accountSession.getUuid() : UUID.randomUUID();
			wsClient.changeSession(uuid);
		}

		if (isInParty() || config.alwaysShowIcon())
		{
			clientToolbar.addNavigation(navButton);
			addedButton = true;
		}

		if (isInParty())
		{
			clientThread.invokeLater(() ->
			{
				myPlayer = new PartyPlayer(partyService.getLocalMember(), client, itemManager);
				wsClient.send(myPlayer);
			});
		}
	}

	@Subscribe
	protected void onConfigChanged(final ConfigChanged c)
	{
		if (!c.getGroup().equals("partypanel"))
		{
			return;
		}

		if (config.alwaysShowIcon())
		{
			if (!addedButton)
			{
				clientToolbar.addNavigation(navButton);
				addedButton = true;
			}
		}
		else if (addedButton && !isInParty())
		{
			clientToolbar.removeNavigation(navButton);
			addedButton = false;
		}
		addedButton = config.alwaysShowIcon();
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		addedButton = false;
		partyMembers.clear();
		wsClient.unregisterMessage(PartyPlayer.class);
	}

	boolean isInParty()
	{
		// TODO: Determine if this is the correct way to check if we are in a party
		return wsClient.sessionExists() && partyService.getLocalMember() != null;
	}

	@Subscribe
	public void onPartyPlayer(final PartyPlayer player)
	{
		if (!isInParty())
		{
			return;
		}

		if (player.getMemberId().equals(partyService.getLocalMember().getMemberId()))
		{
			return;
		}

		player.setMember(partyService.getMemberById(player.getMemberId()));
		if (player.getMember() == null)
		{
			return;
		}

		partyMembers.put(player.getMemberId(), player);
		SwingUtilities.invokeLater(() -> panel.updatePartyPlayer(player));
	}

	@Subscribe
	public void onUserJoin(final UserJoin event)
	{
		// TODO: Figure out how to support people not using the plugin
		if (partyService.getLocalMember() == null)
		{
			return;
		}

		if (!addedButton)
		{
			clientToolbar.addNavigation(navButton);
			addedButton = true;
		}

		// Self joined
		if (event.getMemberId().equals(partyService.getLocalMember().getMemberId()))
		{
			if (myPlayer == null)
			{
				clientThread.invoke(() ->
				{
					myPlayer = new PartyPlayer(partyService.getLocalMember(), client, itemManager);
					wsClient.send(myPlayer);
					return true;
				});
			}
			else
			{
				wsClient.send(myPlayer);
			}
		}
	}

	@Subscribe
	public void onUserPart(final UserPart event)
	{
		final PartyPlayer removed = partyMembers.remove(event.getMemberId());
		if (removed != null)
		{
			SwingUtilities.invokeLater(() -> panel.removePartyPlayer(removed));
		}

		if (addedButton && (!isInParty() || partyService.getMembers().size() == 0) && !config.alwaysShowIcon())
		{
			clientToolbar.removeNavigation(navButton);
			addedButton = false;
		}
	}

	@Subscribe
	public void onUserSync(final UserSync event)
	{
		wsClient.send(myPlayer);
	}

	@Subscribe
	public void onPartyChanged(final PartyChanged event)
	{
		partyMembers.clear();
		SwingUtilities.invokeLater(panel::refreshUI);
		myPlayer = null;

		if (!isInParty() && !config.alwaysShowIcon())
		{
			clientToolbar.removeNavigation(navButton);
			addedButton = false;
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (!isInParty())
		{
			return;
		}

		if (event.getGameState().equals(GameState.LOGIN_SCREEN))
		{
			myPlayer = new PartyPlayer(partyService.getLocalMember(), client, itemManager);
			wsClient.send(myPlayer);
		}
	}

	@Subscribe
	public void onGameTick(final GameTick tick)
	{
		if (!isInParty() || client.getLocalPlayer() == null)
		{
			return;
		}

		boolean changed = false;

		if (myPlayer == null)
		{
			myPlayer = new PartyPlayer(partyService.getLocalMember(), client, itemManager);
			// member changed account, send new data to all members
			wsClient.send(myPlayer);
			return;
		}

		if (myPlayer.getStats() == null)
		{
			myPlayer.updatePlayerInfo(client, itemManager);
			changed = true;
		}
		else
		{
			final int energy = client.getEnergy();
			if (myPlayer.getStats().getRunEnergy() != energy)
			{
				myPlayer.getStats().setRunEnergy(energy);
				changed = true;
			}
		}

		if (!Objects.equals(client.getLocalPlayer().getName(), myPlayer.getUsername()))
		{
			myPlayer.setUsername(client.getLocalPlayer().getName());
			changed = true;
		}

		if (myPlayer.getPrayers() == null)
		{
			myPlayer.setPrayers(new Prayers(client));
			changed = true;
		}
		else
		{
			for (final PrayerSprites prayer : PrayerSprites.values())
			{
				changed = myPlayer.getPrayers().updatePrayerState(prayer, client) || changed;
			}
		}

		if (changed)
		{
			wsClient.send(myPlayer);
		}
	}

	@Subscribe
	public void onStatChanged(final StatChanged event)
	{
		if (myPlayer == null || myPlayer.getStats() == null || !isInParty())
		{
			return;
		}

		final Skill s = event.getSkill();
		if (myPlayer.getSkillBoostedLevel(s) == event.getBoostedLevel() && myPlayer.getSkillRealLevel(s) == event.getLevel())
		{
			return;
		}

		myPlayer.setSkillsBoostedLevel(event.getSkill(), event.getBoostedLevel());
		myPlayer.setSkillsRealLevel(event.getSkill(), event.getLevel());
		myPlayer.getStats().setTotalLevel(client.getTotalLevel());
		wsClient.send(myPlayer);
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged c)
	{
		if (!isInParty())
		{
			return;
		}

		if (c.getContainerId() == InventoryID.INVENTORY.getId())
		{
			myPlayer.setInventory(GameItem.convertItemsToGameItems(c.getItemContainer().getItems(), itemManager));
		}
		else if (c.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			myPlayer.setEquipment(GameItem.convertItemsToGameItems(c.getItemContainer().getItems(), itemManager));
		}
		else
		{
			return;
		}

		wsClient.send(myPlayer);
	}

	@Subscribe
	public void onVarbitChanged(final VarbitChanged event)
	{
		if (myPlayer == null || myPlayer.getStats() == null || !isInParty())
		{
			return;
		}

		final int specialPercent = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
		if (specialPercent != myPlayer.getStats().getSpecialPercent())
		{
			myPlayer.getStats().setSpecialPercent(specialPercent);
			wsClient.send(myPlayer);
		}
	}

	@Nullable
	PartyPlayer getPartyPlayerData(final UUID uuid)
	{
		if (!isInParty())
		{
			return null;
		}

		if (uuid.equals(myPlayer.getMemberId()))
		{
			return myPlayer;
		}

		return partyMembers.get(uuid);
	}

	public void leaveParty()
	{
		partyService.changeParty(null);
	}
}