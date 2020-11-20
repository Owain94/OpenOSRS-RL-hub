/*
 * Copyright (c) 2016-2018, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, ConorLeckey <https://github.com/ConorLeckey>
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
package com.cannonhighlight;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;

import static net.runelite.api.Perspective.LOCAL_TILE_SIZE;

public class CannonHighlighterOverlay extends Overlay {
    // Anything but white text is quite hard to see since it is drawn on
    // a dark background
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);
    private static final int MAX_DISTANCE = 2500;


    enum HittableType {
        UNHITTABLE,
        SINGLE,
        DOUBLE
    }


    static {
        ((DecimalFormat) TIME_LEFT_FORMATTER).applyPattern("#0.0");
    }

    private final Client client;
    private final CannonHighlighterConfig config;
    private final CannonHighlighterPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    CannonHighlighterOverlay(Client client, CannonHighlighterConfig config, CannonHighlighterPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.getCannonPosition() == null || !plugin.isCannonPlaced()) {
            return null;
        }

        LocalPoint cannonPoint = LocalPoint.fromWorld(client, plugin.getCannonPosition());
        LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();

        if (cannonPoint == null || !plugin.isCannonPlaced()) {
            return null;
        }

        if (localLocation.distanceTo(cannonPoint) <= MAX_DISTANCE) {
            drawDoubleHitSpots(graphics, cannonPoint);
        }

        for (NPC npc : plugin.getCachedNPCs()) {
            if (npc != null && npc.getCombatLevel() > 0) {
                boolean marked = false;

                LocalPoint npcLocalPoint = npc.getLocalLocation();
                int size = npc.getTransformedDefinition().getSize();
                int x = npcLocalPoint.getX() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);
                int y = npcLocalPoint.getY() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);

                for (LocalPoint doubleHitSpot : plugin.cannonDoubleHitSpots) {
                    if (Math.abs(doubleHitSpot.getX() - x) < 64 && Math.abs(doubleHitSpot.getY() - y) < 64) {
                        if(config.highlightHull() || config.highlightSouthWestTile() || config.drawNames()){
                            renderNpcOverlay(graphics, npc, HittableType.DOUBLE);
                        }
                        marked = true;
                        break;
                    }
                }
                if (!marked) {
                    for (LocalPoint neverHitSpot : plugin.cannonNeverHitSpots) {
                        if (Math.abs(neverHitSpot.getX() - x) < 64 && Math.abs(neverHitSpot.getY() - y) < 64) {
                            if(config.highlightHull() || config.highlightSouthWestTile() || config.drawNames()) {
                                renderNpcOverlay(graphics, npc, HittableType.UNHITTABLE);
                            }
                            marked = true;
                            break;

                        }
                    }
                }
                if ((config.highlightHull() || config.highlightSouthWestTile() || config.drawNames())
                        && !marked && Math.abs(cannonPoint.getX() - x) <= 384 && Math.abs(cannonPoint.getY() - y) <= 384) {
                    renderNpcOverlay(graphics, npc, HittableType.SINGLE);
                }
            }
        }
        return null;
    }

    /**
     * Draw the double hit spots on a 6 by 6 grid around the cannon
     *
     * @param startTile The position of the cannon
     */
    private void drawDoubleHitSpots(Graphics2D graphics, LocalPoint startTile) {
        plugin.cannonNeverHitSpots.add(new LocalPoint(startTile.getX(), startTile.getY()));
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                if (y != 1 && x != 1 && y != -1 && x != -1) {
                    continue;
                }

                int xPos = startTile.getX() - (x * LOCAL_TILE_SIZE);
                int yPos = startTile.getY() - (y * LOCAL_TILE_SIZE);

                LocalPoint marker = new LocalPoint(xPos, yPos);
                Polygon poly = Perspective.getCanvasTilePoly(client, marker);

                if (poly == null) {
                    continue;
                }

                //Dont highlight cannon corners (1 hit area)
                if (Math.abs(y) == 1 && Math.abs(x) == 1) {
                    continue;
                }

                //Add Uncannonable Tiles (0 hit areas)
                if (Math.abs(y) <= 1 && x == 0 || Math.abs(x) <= 1 && y == 0) {
                    if (plugin.cannonNeverHitSpots.size() < 5) {
                        plugin.cannonNeverHitSpots.add(marker);
                    }
                    if(config.showHitZones()) {
                        OverlayUtil.renderPolygon(graphics, poly, config.unhittableColor());
                    }
                    continue;
                }

                //Add double tiles (2 hit areas)
                if(config.showHitZones()) {
                    OverlayUtil.renderPolygon(graphics, poly, config.doubleColor());
                }
                if (plugin.cannonDoubleHitSpots.size() < 16) {
                    plugin.cannonDoubleHitSpots.add(marker);
                }
            }
        }
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC actor, HittableType hittableType) {
        Color color = Color.yellow;
        String text = "";

        switch (hittableType) {
            case UNHITTABLE:
                color = config.unhittableColor();
                text = "Unhittable";
                break;
            case SINGLE:
                color = config.singleColor();
                text = "Single";
                break;
            case DOUBLE:
                color = config.doubleColor();
                text = "Double";
        }

        NPCDefinition npcComposition = actor.getTransformedDefinition();
        if (npcComposition == null || !npcComposition.isInteractible()
                || actor.isDead()) {
            return;
        }

        if(config.highlightHull()) {
            Shape objectClickbox = actor.getConvexHull();
            renderPoly(graphics, color, objectClickbox);
        }

        if (config.highlightSouthWestTile()) {
            int size = npcComposition.getSize();
            LocalPoint lp = actor.getLocalLocation();

            int x = lp.getX() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);
            int y = lp.getY() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);

            Polygon southWestTilePoly = Perspective.getCanvasTilePoly(client, new LocalPoint(x, y));

            renderPoly(graphics, color, southWestTilePoly);
        }

        if (config.drawNames()) {
            Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight() + 40);

            if (textLocation != null) {
                OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
            }
        }
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(polygon);
        }
    }
}
