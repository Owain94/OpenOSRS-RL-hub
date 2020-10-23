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
package thestonedturtle.crabsolver;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Crab Solver",
	description = "Shows the correct color for each crab crystal",
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class CrabSolverPlugin extends Plugin
{
	@Inject
	private CrabSolverOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CrabSolverConfig config;

	@Inject
	private Client client;

	@Provides
	CrabSolverConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CrabSolverConfig.class);
	}

	@Getter
	private final Map<CrabCrystal, GameObject> crystalMap = new HashMap<>();

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

		crystalMap.put(crystal, e.getGameObject());
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
