package com.essencerunning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class EssenceRunningOverlay extends Overlay {

    private final EssenceRunningPlugin plugin;
    private final Client client;
    private final EssenceRunningConfig config;

    private static final String FREE_INVENTORY_SLOTS = " free inventory slots.";

    @Inject
    private EssenceRunningOverlay(final EssenceRunningPlugin plugin, final Client client, final EssenceRunningConfig config) {

        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(final Graphics2D graphics) {

        renderBindingNecklace(graphics);

        if (config.enableRunnerMode()) {
            final Widget chatbox = client.getWidget(WidgetInfo.CHATBOX);
            if (config.highlightTradeSent() && chatbox != null && !chatbox.isHidden()) {
                drawShape(graphics, chatbox.getBounds(), plugin.isTradeSent() ? Color.GREEN : Color.RED);
            }

            if (config.highlightRingOfDueling() && !plugin.isRingEquipped()) {
                drawWidgetChildren(graphics, client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER), ItemID.RING_OF_DUELING8);
                drawWidgetChildren(graphics, client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER), ItemID.RING_OF_DUELING8);
            }
        }

        return null;
    }

    private void renderBindingNecklace(final Graphics2D graphics) {
        if (config.enableRunecrafterMode()) {
            if (config.highlightEquipBindingNecklace() == EssenceRunningItemDropdown.HighlightEquipBindingNecklace.EQUIP) {
                if (!plugin.isAmuletEquipped()) {
                    final Widget inventory = client.getWidget(WidgetInfo.INVENTORY);
                    if (!inventory.isHidden()) {
                        for (final WidgetItem item : inventory.getWidgetItems()) {
                            if (!item.getWidget().isHidden() && item.getId() == ItemID.BINDING_NECKLACE) {
                                drawShape(graphics, item.getCanvasBounds(), Color.RED);
                            }
                        }
                    } else {
                        drawWidgetChildren(graphics, client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER), ItemID.BINDING_NECKLACE);
                        drawWidgetChildren(graphics, client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER), ItemID.BINDING_NECKLACE);
                    }
                }
            }
        }
        if (config.enableRunnerMode()) {
            switch (config.highlightTradeBindingNecklace()) {
                case TWENTY_FIVE:
                case TWENTY_SIX:
                    if (matchFreeInventorySlots()) {
                        // Widget that contains the inventory while inside a trade transaction
                        drawWidgetChildren(graphics, client.getWidget(WidgetID.PLAYER_TRADE_INVENTORY_GROUP_ID, 0), ItemID.BINDING_NECKLACE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void drawWidgetChildren(final Graphics2D graphics, final Widget widget, final int itemId) {
        if (widget != null && widget.getChildren() != null) {
            for (final Widget item : widget.getChildren()) {
                if (!item.isHidden() && item.getItemId() == itemId) {
                    drawShape(graphics, item.getBounds(), Color.RED);
                }
            }
        }
    }

    private void drawShape(final Graphics2D graphics, final Shape shape, final Color color) {
        final Color previousColor = graphics.getColor();
        graphics.setColor(color);
        graphics.draw(shape);
        graphics.setColor(previousColor);
    }

    private boolean matchFreeInventorySlots() {
        // Widget that contains the trading partner's number of free inventory slots
        final Widget freeSlots = client.getWidget(WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID, 9);
        return freeSlots != null && freeSlots.getText().endsWith(config.highlightTradeBindingNecklace().getOption() + FREE_INVENTORY_SLOTS);
    }
}
