/*
 *  BSD 2-Clause License
 *
 *  Copyright (c) 2020, wikiworm (Brandon Ripley)
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.wikiworm.inventoryvalue;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

/**
 * The InventoryValueOverlay class is used to display the value of the users inventory as an overlay
 * on the RuneLite client gameplay panel.
 */
public class InventoryValueOverlay extends Overlay {
    private Long inventoryValue;
    private final InventoryValueConfig ivConfig;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private InventoryValueOverlay(InventoryValueConfig config)
    {
        // TODO -- this should be part of config...
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        inventoryValue = 0L;
        ivConfig = config;
    }

    /**
     * Render the item value overlay.
     * @param graphics the 2D graphics
     * @return the value of {@link PanelComponent#render(Graphics2D)} from this panel implementation.
     */
    @Override
    public Dimension render(Graphics2D graphics) {
        String titleText = "Inventory Value:";
        String valueString = ivConfig.useHighAlchemyValue() ? "HA Price:" : "GE price:";

        // Not sure how this can occur, but it was recommended to do so
        panelComponent.getChildren().clear();

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(titleText)
                .color(Color.GREEN)
                .build());

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(titleText) + 30,
                0));

        // Build line on the overlay for world number
        panelComponent.getChildren().add(LineComponent.builder()
                .left(valueString)
                .right(Long.toString(inventoryValue))
                .build());

        return panelComponent.render(graphics);
    }

    /**
     * Updates inventory value display
     * @param newValue the value to update the InventoryValue's {{@link #panelComponent}} with.
     */
    public void updateInventoryValue(final long newValue) {
        SwingUtilities.invokeLater(() -> {
            inventoryValue = newValue;
        });
    }


}
