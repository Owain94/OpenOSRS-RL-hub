/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, Bram91 <https://github.com/Bram91>
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
package com.bram91.brushmarkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class BrushMarkerOverlay extends Overlay
{
	private static final int MAX_DRAW_DISTANCE = 32;

	private final Client client;
	private final BrushMarkerConfig config;
	private final BrushMarkerPlugin plugin;
	@Inject
	ItemManager itemManager;

	@Inject
	private BrushMarkerOverlay(Client client, BrushMarkerConfig config, BrushMarkerPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Collection<ColorTileMarker> points = plugin.getPoints();
		try
		{
			for (final ColorTileMarker point : points)
			{
				WorldPoint worldPoint = point.getWorldPoint();
				if (worldPoint.getPlane() != client.getPlane())
				{
					continue;
				}

				Color tileColor = point.getColor();
				if (tileColor == null)
				{
					// If this is an old tile which has no color, or rememberTileColors is off, use marker color
					tileColor = plugin.getColor();
				}

				drawTile(graphics, worldPoint, tileColor);
			}

			if (config.paintMode() && client.getSelectedSceneTile() != null)
			{
				final Polygon poly = Perspective.getCanvasTileAreaPoly(client, client.getSelectedSceneTile().getLocalLocation(), config.brushSize().getSize());

				if (poly != null)
				{
					final BufferedImage image = resize(itemManager.getImage(670), poly.getBounds().width, poly.getBounds().height);
					final Point imageLoc = Perspective.getCanvasImageLocation(client, client.getSelectedSceneTile().getLocalLocation(), image, 0);
					OverlayUtil.renderImageLocation(graphics, imageLoc, image);
					OverlayUtil.renderPolygon(graphics, poly, plugin.getColor());
				}
			}
		}
		catch(ConcurrentModificationException e)
		{

		}

		return null;
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH)
	{
		Image tmp = img.getScaledInstance(newW / 2, newH / 2, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW / 2, newH / 2, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	private void drawTile(Graphics2D graphics, WorldPoint point, Color color)
	{
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		if (point.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE)
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}
		if (config.fillPoly())
		{
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), config.polyAlpha()));
			graphics.fillPolygon(poly);
		}
		OverlayUtil.renderPolygon(graphics, poly, color);
	}
}
