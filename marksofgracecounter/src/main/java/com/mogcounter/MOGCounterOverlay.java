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

import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class MOGCounterOverlay extends OverlayPanel
{
	static final String MARK_CLEAR = "Clear";
	static final String GROUND_RESET = "Reset Ground Counter";
	private final MOGCounterPlugin plugin;
	private final MOGCounterConfig config;

	@Inject
	private MOGCounterOverlay(MOGCounterPlugin plugin, MOGCounterConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.plugin = plugin;
		this.config = config;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Mark overlay"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, GROUND_RESET, "Mark overlay"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, MARK_CLEAR, "Mark overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		MOGSession session = plugin.getMogSession();

		if (!config.showMarkCount() ||
			session == null ||
			session.getLastMarkSpawnTime() == null)
		{
			return null;
		}

		Duration markTimeout = Duration.ofMinutes(config.markTimeout());
		Duration sinceMark = Duration.between(session.getLastMarkSpawnTime(), Instant.now());

		if (sinceMark.compareTo(markTimeout) >= 0)
		{
			// timeout session
			session.clearCounters();
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder().text("Marks of Grace").build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Total Spawns:")
			.right(Integer.toString(session.getTotalMarkSpawnEvents()))
			.build());

		if (config.showMarksSpawned())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Marks on Ground:")
				.right(Integer.toString(session.getMarksSpawned()))
				.build());
		}

		if (config.showMarkLastSpawn())
		{
			long s = sinceMark.getSeconds();
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Last Spawn:")
				.right(String.format("%d:%02d", (s % 3600) / 60, (s % 60)))
				.build());
		}

		if (config.showMarksPerHour() && session.getTotalMarkSpawnEvents() >= 2)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Spawns per Hour:")
				.right(Integer.toString(session.getSpawnsPerHour()))
				.build());
		}


		return super.render(graphics);
	}
}
