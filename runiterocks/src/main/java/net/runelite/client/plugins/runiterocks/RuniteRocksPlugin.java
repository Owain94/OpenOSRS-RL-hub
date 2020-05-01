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
package net.runelite.client.plugins.runiterocks;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Runite Rocks",
	description = "Tracks when runite rocks will respawn",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
@Slf4j
public class RuniteRocksPlugin extends Plugin
{
	private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	public RuniteRocksConfig config;

	@Inject
	private WorldService worldService;

	@Inject
	private ScheduledExecutorService executorService;

	private ScheduledFuture panelUpdateFuture;

	@Provides
	RuniteRocksConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RuniteRocksConfig.class);
	}

	// Rocks that were spawned and not despawned should have their last visited time updated when hopping or logging out
	private final Map<WorldPoint, GameObject> spawnedRocks = new HashMap<>();
	private final Map<WorldPoint, GameObject> queue = new HashMap<>();
	@Getter
	private final Map<Integer, WorldTracker> worldMap = new HashMap<>();
	@Getter
	private WorldTracker tracker;

	private NavigationButton navButton;
	private RuniteRocksPanel panel;

	private net.runelite.api.World quickHopTargetWorld;
	private int displaySwitcherAttempts = 0;

	// Game state will change to loading between hopping and LOGGED_IN
	// We need to ignore this state as the game loads with all Runite rocks available
	private boolean isHopping = true;

	@Override
	protected void startUp() throws Exception
	{
		panel = new RuniteRocksPanel(this);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "icon.png");
		navButton = NavigationButton.builder()
			.tooltip("Runite Rocks")
			.icon(icon)
			.priority(10)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		if (client.getGameState().equals(GameState.LOGGED_IN))
		{
			final World world = getWorld(client.getWorld());
			if (world == null)
			{
				log.warn("couldn't find world for id: {}", client.getWorld());
				return;
			}
			tracker = new WorldTracker(world);
			worldMap.put(client.getWorld(), tracker);
		}

		isHopping = client.getGameState().equals(GameState.HOPPING);

		panelUpdateFuture = executorService.scheduleAtFixedRate(this::updatePanel, 1000, 500, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void shutDown() throws Exception
	{
		if (panelUpdateFuture != null)
		{
			panelUpdateFuture.cancel(true);
			panelUpdateFuture = null;
		}
		clientToolbar.removeNavigation(navButton);
		panel = null;
		queue.clear();
		worldMap.clear();
		tracker = null;
		spawnedRocks.clear();
		isHopping = false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		switch (gameStateChanged.getGameState())
		{
			case LOADING:
				spawnedRocks.clear();
				if (isHopping)
				{
					return;
				}
				break;
			case HOPPING:
				isHopping = true;
				// intentional fall through
			case LOGIN_SCREEN:
				processSpawnedRocks();
				break;
			case LOGGING_IN:
			case CONNECTION_LOST:
				spawnedRocks.clear();
				return;
			case LOGGED_IN:
				isHopping = false;
				break;
			default:
				return;
		}

		final int currentWorld = client.getWorld();
		if (tracker != null)
		{
			if (currentWorld == tracker.getWorld().getId())
			{
				return;
			}

			final int oldWorld = tracker.getWorld().getId();
			SwingUtilities.invokeLater(() -> panel.switchCurrentHighlight(currentWorld, oldWorld));
		}

		final World world = getWorld(currentWorld);
		if (world == null)
		{
			log.warn("couldn't find world for id: {}", currentWorld);
			return;
		}

		tracker = worldMap.getOrDefault(currentWorld, new WorldTracker(world));
		// Ensure it exists on the map since getOrDefault doesn't do that
		worldMap.put(currentWorld, tracker);
	}

	@Subscribe
	public void onGameObjectChanged(final GameObjectChanged e)
	{
		if (tracker == null)
		{
			return;
		}

		final WorldPoint tileLocation = e.getTile().getWorldLocation();
		if (Rock.getByWorldPoint(tileLocation) != null)
		{
			queue.put(tileLocation, e.getGameObject());
		}
	}

	@Subscribe
	public void onGameObjectSpawned(final GameObjectSpawned e)
	{
		if (tracker == null)
		{
			return;
		}

		final WorldPoint tileLocation = e.getTile().getWorldLocation();
		if (Rock.getByWorldPoint(tileLocation) != null)
		{
			queue.put(tileLocation, e.getGameObject());
		}
	}

	@Subscribe
	public void onGameObjectDespawned(final GameObjectDespawned e)
	{
		if (tracker == null)
		{
			return;
		}

		final WorldPoint tileLocation = e.getTile().getWorldLocation();
		if (Rock.getByWorldPoint(tileLocation) != null)
		{
			queue.put(tileLocation, e.getGameObject());
		}
	}

	@Subscribe
	public void onGameTick(final GameTick tick)
	{
		// Quick hoping
		if (quickHopTargetWorld != null)
		{
			if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
			{
				client.openWorldHopper();

				if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS)
				{
					String chatMessage = new ChatMessageBuilder()
						.append(ChatColorType.NORMAL)
						.append("Failed to hop after ")
						.append(ChatColorType.HIGHLIGHT)
						.append(Integer.toString(displaySwitcherAttempts))
						.append(ChatColorType.NORMAL)
						.append(" attempts.")
						.build();

					chatMessageManager
						.queue(QueuedMessage.builder()
							.type(ChatMessageType.CONSOLE)
							.runeLiteFormattedMessage(chatMessage)
							.build());

					resetQuickHopper();
				}
			}
			else
			{
				client.hopToWorld(quickHopTargetWorld);
				resetQuickHopper();
			}
		}

		if (tracker != null && queue.size() > 0)
		{
			final Collection<RuniteRock> rocks = new ArrayList<>();
			for (final Map.Entry<WorldPoint, GameObject> entry : queue.entrySet())
			{
				final RuniteRock rock = tracker.updateRockState(entry.getKey(), entry.getValue());
				if (rock == null)
				{
					log.warn("Error updating rock state: {} | {}", entry.getKey(), entry.getValue());
					continue;
				}
				rocks.add(rock);
				spawnedRocks.put(entry.getKey(), entry.getValue());
			}

			queue.clear();
			SwingUtilities.invokeLater(() -> panel.updateRuniteRocks(rocks));
		}
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged e)
	{
		if (!e.getGroup().equals(config.GROUP))
		{
			return;
		}

		SwingUtilities.invokeLater(panel::populate);
	}

	private void processSpawnedRocks()
	{
		if (spawnedRocks.size() == 0)
		{
			return;
		}

		final Collection<RuniteRock> rocks = new ArrayList<>();
		for (final Map.Entry<WorldPoint, GameObject> entry : spawnedRocks.entrySet())
		{
			final RuniteRock rock = tracker.updateRockState(entry.getKey(), entry.getValue());
			if (rock == null)
			{
				log.warn("Error updating spawned rock state: {} | {}", entry.getKey(), entry.getValue());
				continue;
			}
			rocks.add(rock);
		}

		spawnedRocks.clear();
		SwingUtilities.invokeLater(() -> panel.updateRuniteRocks(rocks));
	}

	@Subscribe
	public void onChatMessage(final ChatMessage event)
	{
		if (event.getMessage().equals("Please finish what you're doing before using the World Switcher."))
		{
			resetQuickHopper();
		}
	}

	@Nullable
	private World getWorld(final int worldNumber)
	{
		WorldResult worldResult = worldService.getWorlds();
		if (worldResult != null)
		{
			return worldResult.findWorld(worldNumber);
		}

		return null;
	}

	void hopToWorld(final World world)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (world.getId() == client.getWorld())
		{
			final String chatMessage = new ChatMessageBuilder()
				.append(ChatColorType.NORMAL)
				.append("You are already in World ")
				.append(ChatColorType.HIGHLIGHT)
				.append(Integer.toString(world.getId()))
				.build();

			chatMessageManager
				.queue(QueuedMessage.builder()
					.type(ChatMessageType.CONSOLE)
					.runeLiteFormattedMessage(chatMessage)
					.build());
			return;
		}

		final String chatMessage = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append("Attempting to hop to World ")
			.append(ChatColorType.HIGHLIGHT)
			.append(Integer.toString(world.getId()))
			.append(ChatColorType.NORMAL)
			.append("..")
			.build();

		chatMessageManager
			.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(chatMessage)
				.build());

		quickHopTargetWorld = toRsWorld(world);
		client.changeWorld(quickHopTargetWorld);
		displaySwitcherAttempts = 0;
	}

	private net.runelite.api.World toRsWorld(final World world)
	{
		final net.runelite.api.World rsWorld = client.createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

		return rsWorld;
	}

	private void resetQuickHopper()
	{
		displaySwitcherAttempts = 0;
		quickHopTargetWorld = null;
	}

	private void updatePanel()
	{
		if (tracker == null || panel.getRows().size() == 0)
		{
			return;
		}

		SwingUtilities.invokeLater(panel::updateList);
	}

	void removeRock(final int world, final Rock rock)
	{
		final WorldTracker track = worldMap.get(world);
		if (track == null)
		{
			return;
		}

		track.removeRock(rock);
		SwingUtilities.invokeLater(panel::populate);
	}

	void clearRocks()
	{
		worldMap.clear();
		tracker.clear();
		worldMap.put(client.getWorld(), tracker);
		SwingUtilities.invokeLater(panel::populate);
	}
}