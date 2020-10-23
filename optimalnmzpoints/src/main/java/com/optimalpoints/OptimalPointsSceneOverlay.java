/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package com.optimalpoints;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCDefinition;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.BasicStroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OptimalPointsSceneOverlay extends Overlay {
    private static final Color TOP_TEXT_COLOR = Color.RED;
    private static final Color STANDARD_TEXT_COLOR = Color.WHITE;

    private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);

    static {
        ((DecimalFormat) TIME_LEFT_FORMATTER).applyPattern("#0.0");
    }

    private final Client client;
    private final OptimalPointsConfig config;
    private final OptimalPointsPlugin plugin;

    @Inject
    OptimalPointsSceneOverlay(Client client, OptimalPointsConfig config, OptimalPointsPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isInNightmareZone())
        {
            return null;
        }
        List<CurrentBossData> bossDataList = plugin.getHighlightedNpcs();
        if (bossDataList.size() == 0) {
            return null;
        }
        int rank = 1;
        for (CurrentBossData bossData : bossDataList) {
            renderNpcOverlay(graphics, bossData.getNpcData(), bossData.getScore(), rank);
            rank++;
        }
        return null;
    }


    private void renderNpcOverlay(Graphics2D graphics, NPC actor, Integer score, Integer rank) {
        if (rank > config.maxRankToShow()){
            return;
        }

        Color colour = getColorForRank(rank);
        NPCDefinition npcComposition = actor.getTransformedDefinition();
        if (npcComposition == null || !npcComposition.isInteractible()
                || (actor.isDead())) {
            return;
        }

        if (config.highlightHull()) {
            Shape objectClickbox = actor.getConvexHull();
            renderPoly(graphics, colour, objectClickbox);
        }

        if (config.highlightTile()) {
            int size = npcComposition.getSize();
            LocalPoint lp = actor.getLocalLocation();
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);

            renderPoly(graphics, colour, tilePoly);
        }

        if (config.highlightSouthWestTile()) {
            int size = npcComposition.getSize();
            LocalPoint lp = actor.getLocalLocation();

            int x = lp.getX() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);
            int y = lp.getY() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);

            Polygon southWestTilePoly = Perspective.getCanvasTilePoly(client, new LocalPoint(x, y));

            renderPoly(graphics, colour, southWestTilePoly);
        }

        {
            String displayText = score.toString();
            Point textLocation = actor.getCanvasTextLocation(graphics, displayText, actor.getLogicalHeight() + 40);

            if (textLocation != null) {
                OverlayUtil.renderTextLocation(graphics, textLocation, displayText, rank == 1 ? TOP_TEXT_COLOR : STANDARD_TEXT_COLOR);
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

    private Color getColorForRank(int rank) {
        switch (rank) {
            case 1:
                return config.getHighlightColor1();
            case 2:
                return config.getHighlightColor2();
            case 3:
                return config.getHighlightColor3();
            case 4:
                return config.getHighlightColor4();
            default:
                return Color.WHITE;
        }
    }
}
