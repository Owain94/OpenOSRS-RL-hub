/*
 * Copyright (c) 2020, melky <https://github.com/melkypie>
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
package net.runelite.client.plugins.clanchatcountryflags;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Clan Chat Country flags",
	description = "Shows the flag of the world next to the world number",
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class ClanChatCountryFlagsPlugin extends Plugin
{
	private int modIconsStart = -1;

	@Inject
	private Client client;

	@Inject
	private WorldService worldService;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp()
	{
		loadRegionIcons();
		clientThread.invoke(() -> toggleWorldsToFlags(true));
	}

	@Override
	protected void shutDown()
	{
		unloadRegionIcons();
		clientThread.invoke(() -> toggleWorldsToFlags(false));
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			loadRegionIcons();
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		if (!scriptCallbackEvent.getEventName().equals("clanChatChannelRebuild"))
		{
			return;
		}
		clientThread.invoke(() -> toggleWorldsToFlags(true));
	}

	private void loadRegionIcons()
	{
		final IndexedSprite[] modIcons = client.getModIcons();

		if (modIconsStart != -1 || modIcons == null)
		{
			return;
		}

		final ClanWorldRegion[] worldRegions = ClanWorldRegion.values();
		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + worldRegions.length);
		modIconsStart = modIcons.length;

		for (int i = 0; i < worldRegions.length; i++)
		{
			final ClanWorldRegion worldRegion = worldRegions[i];

			final BufferedImage image = worldRegion.loadImage();
			final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
			newModIcons[modIconsStart + i] = sprite;
		}

		log.debug("Loaded region icons");
		client.setModIcons(newModIcons);
	}

	private void unloadRegionIcons()
	{
		final IndexedSprite[] modIcons = client.getModIcons();

		if (modIconsStart == -1 || modIcons == null)
		{
			return;
		}

		final ClanWorldRegion[] worldRegions = ClanWorldRegion.values();
		//Icons that were loaded before region icons were loaded
		final IndexedSprite[] oldModIcons = Arrays.copyOf(modIcons, modIconsStart);

		//Icons that were loaded after region icons were loaded
		final IndexedSprite[] futureModIcons = Arrays.copyOfRange(modIcons, modIconsStart + worldRegions.length,
			modIcons.length);

		//Array without the region icons
		final IndexedSprite[] newModIcons = ArrayUtils.addAll(oldModIcons, futureModIcons);

		modIconsStart = -1;
		log.debug("Unloaded region icons");
		client.setModIcons(newModIcons);
	}

	private void toggleWorldsToFlags(boolean worldsToFlags)
	{
		Widget clanChatList = client.getWidget(WidgetInfo.CLAN_CHAT_LIST);
		if (clanChatList == null || clanChatList.getChildren() == null)
		{
			return;
		}

		if (worldsToFlags)
		{
			changeWorldsToFlags(clanChatList);
		}
		else
		{
			changeFlagsToWorlds(clanChatList);
		}
	}

	private void changeWorldsToFlags(Widget clanChatList)
	{
		final WorldResult worldResult = worldService.getWorlds();

		if (worldResult == null)
		{
			return;
		}

		// Iterate every 3 widgets, since the order of widgets is name, world, icon
		for (int i = 1; i < clanChatList.getChildren().length; i += 3)
		{
			Widget listWidget = clanChatList.getChild(i);
			final String worldString = listWidget.getText();
			// In case the string already contains a country flag
			if (!worldString.matches("^World\\s?.*$"))
			{
				continue;
			}
			final int worldNumber = Integer.parseInt(listWidget.getText().replace("World ", ""));

			final World clanMemberWorld = worldResult.findWorld(worldNumber);
			if (clanMemberWorld == null)
			{
				continue;
			}

			final int worldRegionId = clanMemberWorld.getLocation(); // 0 - us, 1 - gb, 3 - au, 7 - de
			final int regionModIconId = ClanWorldRegion.getByRegionId(worldRegionId).ordinal() + modIconsStart;

			listWidget.setText(worldNumber + " <img=" + (regionModIconId) + ">");
		}
	}

	private void changeFlagsToWorlds(Widget clanChatList)
	{
		// Iterate every 3 widgets, since the order of widgets is name, world, icon
		for (int i = 1; i < clanChatList.getChildren().length; i += 3)
		{
			Widget listWidget = clanChatList.getChild(i);
			final String worldString = listWidget.getText();
			// In case the string already has been changed back to World
			if (!worldString.matches("^.*\\s?<img=\\d+>$"))
			{
				continue;
			}

			final String worldNum = listWidget.getText().replaceAll("\\s?<img=\\d+>$", "");
			listWidget.setText("World " + worldNum);
		}
	}
}