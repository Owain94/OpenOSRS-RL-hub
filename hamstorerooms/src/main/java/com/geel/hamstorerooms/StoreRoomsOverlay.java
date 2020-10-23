package com.geel.hamstorerooms;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
class StoreRoomsOverlay extends Overlay {
    private final Client client;
    private final StoreRoomsPlugin plugin;

    @Inject
    private StoreRoomsOverlay(Client client, StoreRoomsPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isInStoreRooms())
            return null;

        final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
        if (itemContainer == null) {
            return null;
        }

        plugin.getChests().forEach((tileObject, chest) ->
        {
            final int numKeys = itemContainer.count(chest.getKey());

            if (numKeys == 0)
                return;

            final String chestText = chest.name() + " x" + numKeys;

            highlightObject(graphics, tileObject, Color.GREEN);

            Point textLocation = tileObject.getCanvasTextLocation(graphics, chestText, 130);

            if (textLocation != null) {
                OverlayUtil.renderTextLocation(graphics, textLocation, chestText, Color.WHITE);
            }
        });

        return null;
    }

    private void highlightObject(Graphics2D graphics, TileObject object, Color color) {
        Point mousePosition = client.getMouseCanvasPosition();

        Shape objectClickbox = object.getClickbox();
        if (objectClickbox != null) {
            if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY())) {
                graphics.setColor(color.darker());
            } else {
                graphics.setColor(color);
            }

            graphics.draw(objectClickbox);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            graphics.fill(objectClickbox);
        }
    }
}
