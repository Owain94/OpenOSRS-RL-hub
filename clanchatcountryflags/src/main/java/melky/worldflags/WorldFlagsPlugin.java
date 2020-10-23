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
package melky.worldflags;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.ScriptID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "World country flags",
	description = "Shows the flag of the world next to the world number",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class WorldFlagsPlugin extends Plugin
{
	private int modIconsStart = -1;

	@Inject
	private Client client;

	@Inject
	private WorldService worldService;

	@Inject
	private ClientThread clientThread;

	@Inject
	private WorldFlagsConfig config;

	@Provides
	WorldFlagsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WorldFlagsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(() -> {
			loadRegionIcons();
			toggleWorldsToFlags(config.showClanFlags(), true);
			toggleWorldsToFlags(config.showFriendsFlags(), false);
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientThread.invoke(() -> {
			unloadRegionIcons();
			toggleWorldsToFlags(false, true);
			toggleWorldsToFlags(false, false);
		});
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::loadRegionIcons);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("worldflags"))
		{
			return;
		}

		if (event.getKey().equals("showClanFlags"))
		{
			clientThread.invoke(() -> toggleWorldsToFlags(config.showClanFlags(), true));
		}
		else if (event.getKey().equals("showFriendsFlags"))
		{
			clientThread.invoke(() -> toggleWorldsToFlags(config.showFriendsFlags(), false));
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.FRIENDS_CHAT_CHANNEL_REBUILD)
		{
			clientThread.invoke(() -> toggleWorldsToFlags(config.showClanFlags(), true));
		}
		else if (event.getScriptId() == ScriptID.FRIENDS_UPDATE)
		{
			clientThread.invoke(() -> toggleWorldsToFlags(config.showFriendsFlags(), false));
		}

	}

	private void loadRegionIcons()
	{
		final IndexedSprite[] modIcons = client.getModIcons();

		if (modIconsStart != -1 || modIcons == null)
		{
			return;
		}

		final WorldRegionFlag[] worldRegions = WorldRegionFlag.values();
		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + worldRegions.length);
		modIconsStart = modIcons.length;

		for (int i = 0; i < worldRegions.length; i++)
		{
			final WorldRegionFlag worldRegion = worldRegions[i];

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

		final WorldRegionFlag[] worldRegions = WorldRegionFlag.values();
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

	private void toggleWorldsToFlags(boolean worldsToFlags, boolean flagMode)
	{
		Widget containerWidget;
		if (flagMode)
		{
			containerWidget = client.getWidget(WidgetInfo.FRIENDS_CHAT_LIST);
		}
		else
		{
			containerWidget = client.getWidget(WidgetInfo.FRIEND_LIST_NAMES_CONTAINER);
		}

		if (containerWidget == null || containerWidget.getChildren() == null)
		{
			return;
		}

		if (worldsToFlags)
		{
			changeWorldsToFlags(containerWidget, flagMode);
		}
		else
		{
			changeFlagsToWorlds(containerWidget, flagMode);
		}
	}

	private void changeWorldsToFlags(Widget containerWidget, boolean flagMode) // true - clan, false - friends
	{
		final WorldResult worldResult = worldService.getWorlds();
		// Iterate every 3 widgets starting at 1, since the order of widgets is name, world, icon (for clan chat)
		// Iterate every 3 widget starting at 2, since the order is name, previous name icon, world (for friends)
		for (int i = flagMode ? 1 : 2; i < containerWidget.getChildren().length; i += 3)
		{
			final Widget listWidget = containerWidget.getChild(i);
			String worldString = Text.removeTags(listWidget.getText());
			// In case the string already contains a country flag
			if (!worldString.matches("^World\\s?.*$"))
			{
				continue;
			}
			worldString = worldString.replace("World ", "");
			final int worldNumber = Integer.parseInt(worldString);

			final World targetPlayerWorld = worldResult.findWorld(worldNumber);
			if (targetPlayerWorld == null)
			{
				continue;
			}

			final int worldRegionId = targetPlayerWorld.getLocation(); // 0 - us, 1 - gb, 3 - au, 7 - de
			final int regionModIconId = WorldRegionFlag.getByRegionId(worldRegionId).ordinal() + modIconsStart;

			listWidget.setText(listWidget.getText().replace("World ", "") + " <img=" + (regionModIconId) + ">");
		}
	}

	private void changeFlagsToWorlds(Widget containerWidget, boolean flagMode) // true - clan, false - friends
	{
		// Iterate every 3 widgets starting at 1, since the order of widgets is name, world, icon (for clan chat)
		// Iterate every 3 widget starting at 2, since the order is name, previous name icon, world (for friends)
		for (int i = flagMode ? 1 : 2; i < containerWidget.getChildren().length; i += 3)
		{
			final Widget listWidget = containerWidget.getChild(i);
			final String worldString = removeColorTags(listWidget.getText());
			// In case the string already has been changed back to World
			if (!worldString.matches("^\\d+\\s?<img=\\d+>$") || !listWidget.getName().equals(""))
			{
				continue;
			}
			final String worldNum = listWidget.getText().replaceAll("\\s?<img=\\d+>$", "");
			listWidget.setText("World " + worldNum);
		}
	}

	private String removeColorTags(String text) {
		return text.replaceAll("<(/)?col(=([0-9]|[a-z]){6})*>", "");
	}
}
