package net.runelite.client.plugins.optimalnmzpoints;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GraphicID;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.WildcardMatcher;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "NMZ Optimal Points",
	description = "Highlight NMZ bosses and displays their point value",
	tags = {"highlight", "npcs", "nmz"},
	type = PluginType.MINIGAME,
	enabledByDefault = false
)
@Slf4j
public class OptimalPointsPlugin extends Plugin
{
	private static final int[] NMZ_MAP_REGION = {9033};
	private static final int MAX_ACTOR_VIEW_RANGE = 15;

	private static final String HARD_IDENTIFIER = "(hard)";

	@Inject
	private Client client;

	@Inject
	private OptimalPointsConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private OptimalPointsSceneOverlay optimalPointsSceneOverlay;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ClientThread clientThread;

	/**
	 * NPCs to highlight
	 */
	@Getter(AccessLevel.PACKAGE)
	private final List<CurrentBossData> highlightedNpcs = new ArrayList<>();

	/**
	 * Dead NPCs that should be displayed with a respawn indicator if the config is on.
	 */
	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, MemorizedNpc> deadNpcsToDisplay = new HashMap<>();

	/**
	 * The time when the last game tick event ran.
	 */
	@Getter(AccessLevel.PACKAGE)
	private Instant lastTickUpdate;

	/**
	 * Tagged NPCs that have died at some point, which are memorized to
	 * remember when and where they will respawn
	 */
	private final Map<Integer, MemorizedNpc> memorizedNpcs = new HashMap<>();

	/**
	 * Highlight strings from the configuration
	 */
	private final List<String> highlights = new ArrayList<>();

	/**
	 * Highlight strings from the configuration
	 */
	private final List<NMZBoss> nmzBosses = new ArrayList<>();

	/**
	 * NPC ids marked with the Tag option
	 */
	private final Set<Integer> npcTags = new HashSet<>();

	/**
	 * Tagged NPCs that spawned this tick, which need to be verified that
	 * they actually spawned and didn't just walk into view range.
	 */
	private final List<NPC> spawnedNpcsThisTick = new ArrayList<>();

	/**
	 * Tagged NPCs that despawned this tick, which need to be verified that
	 * they actually spawned and didn't just walk into view range.
	 */
	private final List<NPC> despawnedNpcsThisTick = new ArrayList<>();

	/**
	 * World locations of graphics object which indicate that an
	 * NPC teleported that were played this tick.
	 */
	private final Set<WorldPoint> teleportGraphicsObjectSpawnedThisTick = new HashSet<>();

	/**
	 * The players location on the last game tick.
	 */
	private WorldPoint lastPlayerLocation;

	/**
	 * When hopping worlds, NPCs can spawn without them actually respawning,
	 * so we would not want to mark it as a real spawn in those cases.
	 */
	private boolean skipNextSpawnCheck = false;

	@Provides
	OptimalPointsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OptimalPointsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(optimalPointsSceneOverlay);
		clientThread.invoke(() ->
		{
			skipNextSpawnCheck = true;
			rebuildAllNpcs();
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(optimalPointsSceneOverlay);
		clientThread.invoke(() ->
		{
			deadNpcsToDisplay.clear();
			memorizedNpcs.clear();
			spawnedNpcsThisTick.clear();
			despawnedNpcsThisTick.clear();
			teleportGraphicsObjectSpawnedThisTick.clear();
			npcTags.clear();
			highlightedNpcs.clear();
		});
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN ||
			event.getGameState() == GameState.HOPPING)
		{
			highlightedNpcs.clear();
			deadNpcsToDisplay.clear();
			memorizedNpcs.forEach((id, npc) -> npc.setDiedOnTick(-1));
			lastPlayerLocation = null;
			skipNextSpawnCheck = true;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("npcindicators"))
		{
			return;
		}

		clientThread.invoke(this::rebuildAllNpcs);
	}


	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null)
		{
			return;
		}

		if (npcTags.contains(npc.getIndex()))
		{
			memorizeNpc(npc);
			addHighlightedNpc(npc);
			spawnedNpcsThisTick.add(npc);
			return;
		}

		if (highlightMatchesNPCName(npcName))
		{
			addHighlightedNpc(npc);
			if (!client.isInInstancedRegion())
			{
				memorizeNpc(npc);
				spawnedNpcsThisTick.add(npc);
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();

		if (memorizedNpcs.containsKey(npc.getIndex()))
		{
			despawnedNpcsThisTick.add(npc);
		}

		highlightedNpcs.removeIf(t -> t.getNpcData() == npc);
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		final GraphicsObject go = event.getGraphicsObject();

		if (go.getId() == GraphicID.GREY_BUBBLE_TELEPORT)
		{
			teleportGraphicsObjectSpawnedThisTick.add(WorldPoint.fromLocal(client, go.getLocation()));
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		removeOldHighlightedRespawns();
		validateSpawnedNpcs();
		lastTickUpdate = Instant.now();
		lastPlayerLocation = client.getLocalPlayer().getWorldLocation();
	}

	private static boolean isInViewRange(WorldPoint wp1, WorldPoint wp2)
	{
		int distance = wp1.distanceTo(wp2);
		return distance < MAX_ACTOR_VIEW_RANGE;
	}

	private static WorldPoint getWorldLocationBehind(NPC npc)
	{
		final int orientation = npc.getOrientation() / 256;
		int dx = 0, dy = 0;

		switch (orientation)
		{
			case 0: // South
				dy = -1;
				break;
			case 1: // Southwest
				dx = -1;
				dy = -1;
				break;
			case 2: // West
				dx = -1;
				break;
			case 3: // Northwest
				dx = -1;
				dy = 1;
				break;
			case 4: // North
				dy = 1;
				break;
			case 5: // Northeast
				dx = 1;
				dy = 1;
				break;
			case 6: // East
				dx = 1;
				break;
			case 7: // Southeast
				dx = 1;
				dy = -1;
				break;
		}

		final WorldPoint currWP = npc.getWorldLocation();
		return new WorldPoint(currWP.getX() - dx, currWP.getY() - dy, currWP.getPlane());
	}

	private void memorizeNpc(NPC npc)
	{
		final int npcIndex = npc.getIndex();
		memorizedNpcs.putIfAbsent(npcIndex, new MemorizedNpc(npc));
	}

	private void removeOldHighlightedRespawns()
	{
		deadNpcsToDisplay.values().removeIf(x -> x.getDiedOnTick() + x.getRespawnTime() <= client.getTickCount() + 1);
	}

	private void getBosses()
	{
		nmzBosses.add(new NMZBoss("Trapped Soul", 34, 783));
		nmzBosses.add(new NMZBoss("Count Draynor", 43, 1588));
		nmzBosses.add(new NMZBoss("Corsair Traitor", 69, 845));
		nmzBosses.add(new NMZBoss("Sand Snake", 61, 1178));
		nmzBosses.add(new NMZBoss("Corrupt Lizardman", 425, 4827));
		nmzBosses.add(new NMZBoss("King Roald", 86, 1792));
		nmzBosses.add(new NMZBoss("Witch's experiment", 111, 513));
		nmzBosses.add(new NMZBoss("The Kendal", 192, 2253));
		nmzBosses.add(new NMZBoss("Me", 232, 8264));
		nmzBosses.add(new NMZBoss("Elvarg", 1190, 3601));
		nmzBosses.add(new NMZBoss("Moss Guardian", 1225, 6818));
		nmzBosses.add(new NMZBoss("Slagilith", 1471, 8264));
		nmzBosses.add(new NMZBoss("Nazastarool", 1506, 6611));
		nmzBosses.add(new NMZBoss("Treus Dayth", 1576, 7644));
		nmzBosses.add(new NMZBoss("Skeleton Hellhound", 369, 1995));
		nmzBosses.add(new NMZBoss("Dagannoth mother", 1751, 8264));
		nmzBosses.add(new NMZBoss("Agrith Naar", 1753, 7851));
		nmzBosses.add(new NMZBoss("Tree spirit", 400, 2176));
		nmzBosses.add(new NMZBoss("Dad", 1786, 8264));
		nmzBosses.add(new NMZBoss("Tanglefoot", 2154, 8057));
		nmzBosses.add(new NMZBoss("Khazard warlord", 373, 2003));
		nmzBosses.add(new NMZBoss("Arrg", 2224, 9090));
		nmzBosses.add(new NMZBoss("Black Knight Titan", 2521, 9090));
		nmzBosses.add(new NMZBoss("Ice Troll King", 2591, 9297));
		nmzBosses.add(new NMZBoss("Bouncer", 3274, 12190));
		nmzBosses.add(new NMZBoss("Glod", 3327, 15702));
		nmzBosses.add(new NMZBoss("Evil Chicken", 4413, 16735));
		nmzBosses.add(new NMZBoss("Agrith-Na-Na", 3730, 11363));
		nmzBosses.add(new NMZBoss("Flambeed", 3887, 11570));
		nmzBosses.add(new NMZBoss("Karamel", 3222, 7024));
		nmzBosses.add(new NMZBoss("Dessourt", 2556, 9710));
		nmzBosses.add(new NMZBoss("Gelatinnoth Mother", 2959, 8264));
		nmzBosses.add(new NMZBoss("Culinaromancer", 980, 8884));
		nmzBosses.add(new NMZBoss("Chronozon", 5061, 18181));
		nmzBosses.add(new NMZBoss("Black demon", 5166, 17561));
		nmzBosses.add(new NMZBoss("Giant Roc", 5166, 13636));
		nmzBosses.add(new NMZBoss("Dessous", 3380, 9710));
		nmzBosses.add(new NMZBoss("Damis", 5288, 15082));
		nmzBosses.add(new NMZBoss("Fareed", 4868, 18388));
		nmzBosses.add(new NMZBoss("Kamil", 4150, 15289));
		nmzBosses.add(new NMZBoss("Nezikchened", 6112, 17975));
		nmzBosses.add(new NMZBoss("Barrelchest", 6322, 29752));
		nmzBosses.add(new NMZBoss("Giant scarab", 6374, 20454));
		nmzBosses.add(new NMZBoss("Jungle Demon", 6654, 21900));
		nmzBosses.add(new NMZBoss("Arianwyn", 10000, 30651));
		nmzBosses.add(new NMZBoss("Essyllt", 20000, 70836));
		nmzBosses.add(new NMZBoss("The Untouchable", 13134, 39876));
		nmzBosses.add(new NMZBoss("The Everlasting", 8704, 27479));
		nmzBosses.add(new NMZBoss("The Inadequacy", 20595, 74380));
	}

	@VisibleForTesting
	void rebuildAllNpcs()
	{
		getBosses();
		for (NMZBoss boss : nmzBosses)
		{
			highlights.add(boss.getName() + "*");
		}

		highlightedNpcs.clear();

		if (client.getGameState() != GameState.LOGGED_IN &&
			client.getGameState() != GameState.LOADING || !isInNightmareZone())
		{
			// NPCs are still in the client after logging out,
			// but we don't want to highlight those.
			return;
		}

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null)
			{
				continue;
			}

			if (npcTags.contains(npc.getIndex()))
			{
				addHighlightedNpc(npc);
				continue;
			}

			if (highlightMatchesNPCName(npcName))
			{
				if (!client.isInInstancedRegion())
				{
					memorizeNpc(npc);
				}
				addHighlightedNpc(npc);
				continue;
			}

			// NPC is not highlighted
			memorizedNpcs.remove(npc.getIndex());
		}
	}

	private boolean highlightMatchesNPCName(String npcName)
	{
		for (String highlight : highlights)
		{
			if (WildcardMatcher.matches(highlight, npcName))
			{
				return true;
			}
		}

		return false;
	}

	private void validateSpawnedNpcs()
	{
		if (skipNextSpawnCheck)
		{
			skipNextSpawnCheck = false;
		}
		else
		{
			for (NPC npc : despawnedNpcsThisTick)
			{
				if (!teleportGraphicsObjectSpawnedThisTick.isEmpty())
				{
					if (teleportGraphicsObjectSpawnedThisTick.contains(npc.getWorldLocation()))
					{
						// NPC teleported away, so we don't want to add the respawn timer
						continue;
					}
				}

				if (isInViewRange(client.getLocalPlayer().getWorldLocation(), npc.getWorldLocation()))
				{
					final MemorizedNpc mn = memorizedNpcs.get(npc.getIndex());

					if (mn != null)
					{
						mn.setDiedOnTick(client.getTickCount() + 1); // This runs before tickCounter updates, so we add 1

						if (!mn.getPossibleRespawnLocations().isEmpty())
						{
							log.debug("Starting {} tick countdown for {}", mn.getRespawnTime(), mn.getNpcName());
							deadNpcsToDisplay.put(mn.getNpcIndex(), mn);
						}
					}
				}
			}

			for (NPC npc : spawnedNpcsThisTick)
			{
				if (!teleportGraphicsObjectSpawnedThisTick.isEmpty())
				{
					if (teleportGraphicsObjectSpawnedThisTick.contains(npc.getWorldLocation()) ||
						teleportGraphicsObjectSpawnedThisTick.contains(getWorldLocationBehind(npc)))
					{
						// NPC teleported here, so we don't want to update the respawn timer
						continue;
					}
				}

				if (lastPlayerLocation != null && isInViewRange(lastPlayerLocation, npc.getWorldLocation()))
				{
					final MemorizedNpc mn = memorizedNpcs.get(npc.getIndex());

					if (mn.getDiedOnTick() != -1)
					{
						final int respawnTime = client.getTickCount() + 1 - mn.getDiedOnTick();

						// By killing a monster and leaving the area before seeing it again, an erroneously lengthy
						// respawn time can be recorded. Thus, if the respawn time is already set and is greater than
						// the observed time, assume that the lower observed respawn time is correct.
						if (mn.getRespawnTime() == -1 || respawnTime < mn.getRespawnTime())
						{
							mn.setRespawnTime(respawnTime);
						}

						mn.setDiedOnTick(-1);
					}

					final WorldPoint npcLocation = npc.getWorldLocation();

					// An NPC can move in the same tick as it spawns, so we also have
					// to consider whatever tile is behind the npc
					final WorldPoint possibleOtherNpcLocation = getWorldLocationBehind(npc);

					mn.getPossibleRespawnLocations().removeIf(x ->
						x.distanceTo(npcLocation) != 0 && x.distanceTo(possibleOtherNpcLocation) != 0);

					if (mn.getPossibleRespawnLocations().isEmpty())
					{
						mn.getPossibleRespawnLocations().add(npcLocation);
						mn.getPossibleRespawnLocations().add(possibleOtherNpcLocation);
					}
				}
			}
		}

		spawnedNpcsThisTick.clear();
		despawnedNpcsThisTick.clear();
		teleportGraphicsObjectSpawnedThisTick.clear();
	}

	void addHighlightedNpc(NPC npc)
	{
		for (NMZBoss boss : nmzBosses)
		{
			if (WildcardMatcher.matches(boss.getName() + "*", npc.getName()))
			{
				highlightedNpcs.add(new CurrentBossData(npc,
					(npc.getName()).contains(HARD_IDENTIFIER) ? boss.getHardValue() : boss.getNormalValue()));
			}
		}
		highlightedNpcs.sort(Collections.reverseOrder());
	}

	public boolean isInNightmareZone()
	{
		if (client.getLocalPlayer() == null)
		{
			return false;
		}

		// NMZ and the KBD lair uses the same region ID but NMZ uses planes 1-3 and KBD uses plane 0
		return client.getLocalPlayer().getWorldLocation().getPlane() > 0 && Arrays.equals(client.getMapRegions(), NMZ_MAP_REGION);
	}
}
