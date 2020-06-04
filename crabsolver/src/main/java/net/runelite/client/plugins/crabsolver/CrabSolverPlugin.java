package net.runelite.client.plugins.crabsolver;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Crab Solver",
	description = "Shows the correct color for each crab crystal",
	enabledByDefault = false,
	type = PluginType.PVM
)
public class CrabSolverPlugin extends Plugin
{
	@Inject
	private CrabSolverOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Provides
	CrabSolverConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CrabSolverConfig.class);
	}

	@Getter
	private final Map<CrabCrystal, LocalPoint> crystalMap = new HashMap<>();

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	public void shutDown()
	{
		crystalMap.clear();
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged c)
	{
		if (c.getGameState() == GameState.LOADING)
		{
			crystalMap.clear();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned e)
	{
		final CrabCrystal crystal = CrabCrystal.getByObjectID(e.getGameObject().getId());
		if (crystal == null)
		{
			return;
		}

		crystalMap.put(crystal, e.getGameObject().getLocalLocation());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned e)
	{
		final CrabCrystal crystal = CrabCrystal.getByObjectID(e.getGameObject().getId());
		if (crystal == null)
		{
			return;
		}

		crystalMap.remove(crystal);
	}
}