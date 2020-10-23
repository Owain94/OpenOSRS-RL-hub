/*
 * Copyright (c) 2020, Lotto <https://github.com/devLotto>
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
package com.monkeymetrics;

import com.google.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class AttackMetricsOverlay extends Overlay
{
	private final MonkeyMetricsConfig config;

	private final PanelComponent panelComponent = new PanelComponent();

	private AttackMetrics metrics;

	@Inject
	AttackMetricsOverlay(MonkeyMetricsPlugin monkeyMetricsPlugin, MonkeyMetricsConfig config)
	{
		super(monkeyMetricsPlugin);
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showMetrics())
		{
			return null;
		}

		panelComponent.getChildren().clear();

		panelComponent.getChildren().add(
			TitleComponent.builder()
				.text("Previous attack")
				.build());

		if (metrics != null)
		{
			panelComponent.getChildren().add(
				LineComponent.builder()
					.left("Hitsplats")
					.right(String.valueOf(metrics.getHitsplats()))
					.build());

			panelComponent.getChildren().add(
				LineComponent.builder()
					.left("Total damage")
					.right(metrics.getDamage() + " hp")
					.build());

			metrics.getGainedExp().forEach((skill, exp) ->
			{
				panelComponent.getChildren().add(
					LineComponent.builder()
						.left(skill.getName())
						.right("+" + exp + " xp")
						.build());
			});
		}
		else
		{
			panelComponent.getChildren().add(
				LineComponent.builder()
					.left("Waiting for NPC damage..")
					.build());
		}

		return panelComponent.render(graphics);
	}

	public void setMetrics(AttackMetrics metrics)
	{
		this.metrics = metrics;
	}
}
