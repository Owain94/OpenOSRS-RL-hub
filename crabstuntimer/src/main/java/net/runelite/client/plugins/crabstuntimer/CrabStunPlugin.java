/*
 * Copyright (c) 2018, Seth <Sethtroll3@gmail.com>
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

package net.runelite.client.plugins.crabstuntimer;

import com.google.inject.Provides;
import java.awt.Point;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.SpotAnimationChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Crab Stun Timers",
	description = "Timers for when crabs in CoX get unstunned.",
	tags = {"overlay", "raid", "pvm", "timers"},
	type = PluginType.MINIGAME
)
@Slf4j
public class CrabStunPlugin extends Plugin
{
	@Inject
	private Client client;

	@Provides
	CrabStunConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CrabStunConfig.class);
	}

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CrabStunOverlay overlay;

	@Getter(AccessLevel.PACKAGE)
	private final List<CrabStun> stunEvents = new ArrayList<>();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onSpotAnimationChanged(SpotAnimationChanged event)
	{
		if (client.getVar(Varbits.IN_RAID) != 1)
		{
			return;
		}
		final int CRAB_STUN_GRAPHIC = 245;
		Actor actor = event.getActor();
		if (actor.getName() != null && actor.getName().contains("Jewelled Crab") && actor.getAnimation() == CRAB_STUN_GRAPHIC)
		{
			WorldPoint worldPoint = actor.getWorldLocation();
			CrabStun stunEvent = new CrabStun(actor, worldPoint, Instant.now(), getStunDurationTicks(), 0);
			for (CrabStun stun : stunEvents)
			{
				if (stun.getCrab().equals(actor))
				{
					stun.setStartTime(Instant.now());
				}
			}
			overlay.getRandomIntervalTimers().removeIf(timer -> (timer.getCrab().equals(stunEvent.getCrab())));
			stunEvents.add(stunEvent);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		for (Iterator<CrabStun> it = overlay.getRandomIntervalTimers().iterator(); it.hasNext(); )
		{
			try
			{
				CrabStun stun = it.next();
				Point crabStunPoint = new Point(stun.getWorldPoint().getX(), stun.getWorldPoint().getY());
				Point crabCurrentPoint = new Point(stun.getCrab().getWorldLocation().getX(), stun.getCrab().getWorldLocation().getY());

				if (crabStunPoint.distance(crabCurrentPoint) > 0)
				{
					it.remove();
				}
			}
			catch (Exception e)
			{
				return;
			}
		}
	}

	private int getStunDurationTicks()
	{
		switch (client.getVar(Varbits.RAID_PARTY_SIZE))
		{
			case 1:
				return TeamSize.ONE.getStunDuration();
			case 2:
			case 3:
				return TeamSize.TWO_TO_THREE.getStunDuration();
			case 4:
			case 5:
				return TeamSize.FOUR_TO_FIVE.getStunDuration();
			default:
				return TeamSize.SIX_PLUS.getStunDuration();
		}
	}
}