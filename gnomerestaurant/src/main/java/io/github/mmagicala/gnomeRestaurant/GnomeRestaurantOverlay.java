/*
 * Copyright (c) 2020, MMagicala <https://github.com/MMagicala>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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

package io.github.mmagicala.gnomeRestaurant;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.MenuOpcode;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class GnomeRestaurantOverlay extends OverlayPanel
{
	private final Hashtable<Integer, OverlayEntry> currentStageOverlayTable, futureItemsOverlayTable;
	private final GnomeRestaurantPlugin plugin;

	@Inject
	protected GnomeRestaurantOverlay(GnomeRestaurantPlugin plugin, Hashtable<Integer, OverlayEntry> currentStageOverlayTable,
									 Hashtable<Integer, OverlayEntry> futureItemsOverlayTable)
	{
		super(plugin);
		this.plugin = plugin;
		this.currentStageOverlayTable = currentStageOverlayTable;
		this.futureItemsOverlayTable = futureItemsOverlayTable;
		panelComponent.setBorder(new Rectangle());
		panelComponent.setGap(new Point(0, ComponentConstants.STANDARD_BORDER / 2));
		getMenuEntries().add(new OverlayMenuEntry(MenuOpcode.RUNELITE_OVERLAY, GnomeRestaurantPlugin.OVERLAY_MENU_ENTRY_TEXT, "Gnome Restaurant Overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		String sb = "Step " +
			(plugin.getCurrentStageNodeIndex() + 1) +
			": " +
			plugin.getCurrentStageDirections();
		LineComponent stageRow = LineComponent.builder().left(sb).build();
		panelComponent.getChildren().add(stageRow);

		renderOverlayTable(currentStageOverlayTable, "Current Items");

		if (!futureItemsOverlayTable.isEmpty())
		{
			renderOverlayTable(futureItemsOverlayTable, "Later Items");
		}

		return super.render(graphics);
	}

	private void renderOverlayTable(Hashtable<Integer, OverlayEntry> overlayTable, String title)
	{
		TitleComponent titleComponent = TitleComponent.builder().text(title).build();
		panelComponent.getChildren().add(titleComponent);

		for (Map.Entry<Integer, OverlayEntry> ingredient : overlayTable.entrySet())
		{
			Color ingredientColor;
			if (ingredient.getValue().getInventoryCount() >= ingredient.getValue().getRequiredCount())
			{
				ingredientColor = Color.GREEN;
			}
			else if (ingredient.getValue().getInventoryCount() == 0)
			{
				ingredientColor = Color.RED;
			}
			else
			{
				ingredientColor = Color.YELLOW;
			}

			LineComponent ingredientRow = LineComponent.builder()
				.left(ingredient.getValue().getItemName())
				.leftColor(ingredientColor)
				.right(ingredient.getValue().getInventoryCount() + "/" + ingredient.getValue().getRequiredCount())
				.rightColor(ingredientColor)
				.build();
			panelComponent.getChildren().add(ingredientRow);
		}
	}
}
