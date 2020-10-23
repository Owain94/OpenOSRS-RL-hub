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

package com.micro.petinfo;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class PetsOverlay extends Overlay
{

	private final Client client;
	private final PetsConfig config;
	private final PetInfoPlugin plugin;

	@Inject
	private PetsOverlay(Client client, PetsConfig config, PetInfoPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.config = config;
		this.client = client;
		this.plugin = plugin;
	}

	/**
	 * Draw the appropriate highlights in the appropriate colors for the pets on screen.
	 */
	@Override
	public Dimension render(Graphics2D graphics)
	{
		List<NPC> pets = plugin.getPets();

		if (config.highlight() == PetsConfig.HighlightMode.OFF || pets.isEmpty())
		{
			return null;
		}
		if (config.highlight() == PetsConfig.HighlightMode.ALL)
		{
			pets.forEach(pet -> { drawPet(graphics, pet); });
		}
		if (config.highlight() == PetsConfig.HighlightMode.OWN)
		{
			final Player localPlayer =  client.getLocalPlayer();
			pets.forEach(pet ->
				{
					if (pet.getInteracting() == localPlayer)
					{
						drawPet(graphics, pet);
					}
				});
		}

		return null;
	}

	private void drawPet(Graphics2D graphics, NPC pet)
	{
		String petName = pet.getName();
		if (config.getShowNpcId())
		{
			petName += " - (id: " + pet.getId() + ")";
		}

		Color color = plugin.npcToColor(pet);
		// Determine if we are drawing the box and the name or just the name
		if (plugin.showNpc(pet) == PetsConfig.PetMode.HIGHLIGHT && color != null)
		{
			drawHighlightedPet(graphics, pet, petName, color);
		} else if (plugin.showNpc(pet) == PetsConfig.PetMode.NAME_ONLY && color != null)
		{
			drawOnlyNamePet(graphics, pet, petName, color);
		}
	}

	/**
	 * Draws a square around the pet and the pet's name overhead, both in the color of the pet's group.
	 */
	private void drawHighlightedPet(Graphics2D graphics, Actor actor, String text, Color color)
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
	private void drawOnlyNamePet(Graphics2D graphics, Actor actor, String text, Color color)
	{
		Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight());
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
		}
	}
}
