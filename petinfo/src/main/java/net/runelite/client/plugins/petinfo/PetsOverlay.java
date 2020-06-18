/* Copyright (c) 2020 by micro
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
 *
 * Portions of the code are based off of the "Implings" RuneLite plugin.
 * The "Implings" is:
 * Copyright (c) 2017, Robin <robin.weymans@gmail.com>
 * All rights reserved.
 */

package net.runelite.client.plugins.petinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class PetsOverlay extends Overlay
{

	private final PetsConfig config;
	private final PetInfoPlugin plugin;

	@Inject
	private PetsOverlay(PetsConfig config, PetInfoPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.config = config;
		this.plugin = plugin;
	}

	/**
	 * Draw the appropriate highlights in the appropriate colors for the pets on screen.
	 */
	@Override
	public Dimension render(Graphics2D graphics)
	{
		List<NPC> pets = plugin.getPets();

		if (pets.isEmpty())
		{
			return null;
		}

		for (NPC pet : pets)
		{
			// There were some overlays being drawn where there weren't any pets, hopefully this'll fix it.
			// But to be honest I don't know what the cause was. Perhaps pets morphing?
			// THIS DID NOT RESOLVE THE ISSUE.
			if (pet.getConvexHull() == null)
			{
				continue;
			}

			String petName = pet.getName();
			if (config.getShowNpcId())
			{
				petName += " - (id: " + pet.getId() + ")";
			}

			Color color = plugin.npcToColor(pet);
			// Determine if we are drawing the box and the name or just the name
			if (plugin.showNpc(pet) == PetsConfig.PetMode.HIGHLIGHT && color != null)
			{
				drawPet(graphics, pet, petName, color);
			}
			else if (plugin.showNpc(pet) == PetsConfig.PetMode.NAME_ONLY && color != null)
			{
				drawPetName(graphics, pet, petName, color);
			}


		}

		return null;
	}

	/**
	 * Draws a square around the pet and the pet's name overhead, both in the color of the pet's group.
	 */
	private void drawPet(Graphics2D graphics, Actor actor, String text, Color color)
	{
		Polygon poly = actor.getCanvasTilePoly();
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, color);
		}

		Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight());
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
		}
	}

	/**
	 * Just draws the pet's name overhead, in the color of the pet's group.
	 */
	private void drawPetName(Graphics2D graphics, Actor actor, String text, Color color)
	{
		Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight());
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
		}
	}
}