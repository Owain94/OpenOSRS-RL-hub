/*
 * Copyright (c) 2019, Adam <Adam@sigterm.info>
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
package com.crabstuntimer;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class CrabStunOverlay extends Overlay {
    private final Client client;
    private final CrabStunPlugin plugin;

    @Getter(AccessLevel.PACKAGE)
    private List<CrabStun> randomIntervalTimers = new ArrayList<>();

    @Inject
    private CrabStunConfig config;

    @Inject
    private CrabStunOverlay(Client client, CrabStunPlugin plugin) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<CrabStun> stunEvents = plugin.getStunEvents();
        renderGraphicsFromCrabStunList(stunEvents, graphics, false);
        renderGraphicsFromCrabStunList(randomIntervalTimers, graphics, true);
        return null;
    }

    private void renderGraphicsFromCrabStunList(List<CrabStun> stunEvents, Graphics2D graphics, boolean inRandomInterval) {
        if (stunEvents.isEmpty()) {
            return;
        }

        Instant now = Instant.now();
        for (Iterator<CrabStun> it = stunEvents.iterator(); it.hasNext(); ) {
            Color pieFillColor = (inRandomInterval ? config.randomTimerColor() : config.normalTimerColor());
            Color pieBorderColor = (inRandomInterval ? config.randomBorderColor() : config.timerBorderColor());

            CrabStun stun = it.next();
            float stunDurationMillis = (float) (stun.getStunDurationTicks() * 0.6 * 1000.0);
            float percent = (now.toEpochMilli() - stun.getStartTime().toEpochMilli()) / stunDurationMillis;
            float millisLeft = (stunDurationMillis - (now.toEpochMilli() - stun.getStartTime().toEpochMilli()));
            double secondsLeft = Math.round(millisLeft / 100.0) / 10.0;
            WorldPoint worldPoint = stun.getWorldPoint();
            LocalPoint loc = LocalPoint.fromWorld(client, worldPoint);

            if (percent >= .9) {
                pieFillColor = config.timerWarningColor();
            }

            if (loc == null) {
                it.remove();
                continue;
            }

            if (percent > 1.0f) {
                if (!inRandomInterval) {
                    randomIntervalTimers.add(new CrabStun(stun.getCrab(), stun.getWorldPoint(), Instant.now(), 10, 0));
                }
                it.remove();
                continue;
            }

            Point point = Perspective.localToCanvas(client, loc, client.getPlane(), stun.getZOffset());
            if (point == null) {
                it.remove();
                continue;
            }

            if (config.showTimer()) {
                ProgressPieComponent ppc = new ProgressPieComponent();
                ppc.setBorderColor(pieBorderColor);
                ppc.setFill(pieFillColor);
                ppc.setPosition(point);
                ppc.setProgress(percent);
                ppc.setDiameter(config.timerDiameter());
                ppc.render(graphics);
            }

            if (config.showText()) {
                TextComponent tc = new TextComponent();
                switch (config.textType()) {
                    case SECONDS:
                        tc.setText(secondsLeft + (inRandomInterval ? "?" : ""));
                        break;
                    case TICKS:
                        tc.setText(Math.round((millisLeft / 1000.0) / .6) + (inRandomInterval ? "?" : ""));
                        break;
                }
                tc.setColor(pieFillColor);
                tc.setPosition(new java.awt.Point(point.getX() - 5, point.getY() - 17));
                tc.render(graphics);
            }
        }
    }
}
