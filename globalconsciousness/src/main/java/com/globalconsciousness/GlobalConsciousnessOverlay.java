package com.globalconsciousness;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GraphicID;
import net.runelite.api.Item;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.http.api.item.ItemPrice;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GlobalConsciousnessOverlay extends Overlay {

    private GlobalConsciousnessPlugin plugin;

    private Client client;

    private final ItemManager itemManager;

    private PanelComponent panelComponent = new PanelComponent();

    public int x = 0;
    public int y = 0;

    public boolean down = true;
    public boolean right = true;

    public int speed;

    @Inject
    public GlobalConsciousnessOverlay(Client client, GlobalConsciousnessPlugin plugin, ItemManager itemManager) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
        this.itemManager = itemManager;
        this.speed = plugin.iconSpeed;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        BufferedImage image = new BufferedImage(client.getCanvasWidth(), client.getCanvasHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();

        this.speed = plugin.iconSpeed;

        java.util.List<ItemPrice> items = itemManager.search(plugin.itemName);

        Image icon;

        if (items.size() == 0 | plugin.itemName == "") {
            icon = itemManager.getImage(20594);
        } else {
            icon = itemManager.getImage(items.get(0).getId());
        }

        icon = icon.getScaledInstance(icon.getWidth(null) * plugin.iconScale, icon.getHeight(null) * plugin.iconScale, 0);

        if (this.right) {
            if (this.x + this.speed > client.getViewportWidth() - icon.getWidth(null)) {
                this.x = client.getViewportWidth() - icon.getWidth(null);
            } else {
                this.x += this.speed;
            }
        } else {
            if (this.x - this.speed < 0) {
                this.x = 0;
            } else {
                this.x -= this.speed;
            }
        }

        if (this.down) {
            if (this.y + this.speed > client.getViewportHeight() - icon.getHeight(null)) {
                this.y = client.getViewportHeight() - icon.getHeight(null);
            } else {
                this.y += this.speed;
            }
        } else {
            if (this.y - this.speed < 0) {
                this.y = 0;
            } else {
                this.y -= this.speed;
            }
        }

        if(this.x == 0 | this.x == client.getViewportWidth() - icon.getWidth(null)) {
            this.right = !this.right;
        }

        if(this.y == 0 | this.y == client.getViewportHeight() - icon.getHeight(null)) {
            this.down = !this.down;
        }

        float opacity = (float)plugin.iconOpacity / 100;
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        graphics.drawImage(icon, this.x, this.y, null);

        return null;
    }
}
