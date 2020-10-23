package com.npcidletimer;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "NPC Idle Timer",
	description = "A utility to add overhead timers to select npcs to keep track of how long they have been standing on the same tile",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class NPCIdleTimerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private NPCIdleTimerConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NPCIdleTimerOverlay npcidletimeroverlay;

	@Getter(AccessLevel.PACKAGE)
	private Instant lastTickUpdate;

	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, WanderingNPC> wanderingNPCs = new HashMap<>();

	private List<String> selectedNPCs = new ArrayList<>();

	@Provides
	NPCIdleTimerConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NPCIdleTimerConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(npcidletimeroverlay);
		selectedNPCs = getSelectedNPCs();
		rebuildAllNpcs();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(npcidletimeroverlay);
		wanderingNPCs.clear();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null || !selectedNPCs.contains(npcName.toLowerCase()))
		{
			return;
		}

		wanderingNPCs.putIfAbsent(npc.getIndex(), new WanderingNPC(npc));
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null || !selectedNPCs.contains(npcName.toLowerCase()))
		{
			return;
		}

		wanderingNPCs.remove(npc.getIndex());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN ||
			event.getGameState() == GameState.HOPPING)
		{
			wanderingNPCs.clear();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		lastTickUpdate = Instant.now();

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null || !selectedNPCs.contains(npcName.toLowerCase()))
			{
				continue;
			}

			final WanderingNPC wnpc = wanderingNPCs.get(npc.getIndex());

			if (wnpc == null)
			{
				continue;
			}

			if (wnpc.getCurrentLocation().getX() != npc.getWorldLocation().getX() || wnpc.getCurrentLocation().getY() != npc.getWorldLocation().getY())
			{
				wnpc.setCurrentLocation(npc.getWorldLocation());
				wnpc.setTimeWithoutMoving(0);
				wnpc.setStoppedMovingTick(Instant.now());
				wnpc.setNpc(npc);
			}
			else
			{
				wnpc.setTimeWithoutMoving(lastTickUpdate.getEpochSecond() - wnpc.getStoppedMovingTick().getEpochSecond());
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("npcidletimerplugin"))
		{
			return;
		}

		selectedNPCs = getSelectedNPCs();
		rebuildAllNpcs();
	}

	@VisibleForTesting
	List<String> getSelectedNPCs()
	{
		final String configNPCs = config.npcToShowTimer().toLowerCase();

		if (configNPCs.isEmpty())
		{
			return Collections.emptyList();
		}

		return Text.fromCSV(configNPCs);
	}

	private void rebuildAllNpcs()
	{
		wanderingNPCs.clear();

		if (client.getGameState() != GameState.LOGGED_IN &&
			client.getGameState() != GameState.LOADING)
		{
			// NPCs are still in the client after logging out, ignore them
			return;
		}

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null || !selectedNPCs.contains(npcName.toLowerCase()))
			{
				continue;
			}

			wanderingNPCs.putIfAbsent(npc.getIndex(), new WanderingNPC(npc));
		}
	}
}
