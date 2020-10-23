package com.essencerunning;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class EssenceRunningClanChatOverlay extends Overlay {

    private final EssenceRunningPlugin plugin;
    private final Client client;
    private final EssenceRunningConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public EssenceRunningClanChatOverlay(final EssenceRunningPlugin plugin, final Client client, final EssenceRunningConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(final Graphics2D graphics) {
        panelComponent.getChildren().clear();

        final Widget chatbox = client.getWidget(WidgetInfo.CHATBOX_TRANSPARENT_LINES);
        if (config.clanChatOverlayHeight().getOption() > 0 && chatbox != null && !chatbox.isHidden()) {
            panelComponent.setPreferredSize(new Dimension(chatbox.getWidth(), 0));
            plugin.getClanMessages().values().forEach(message -> panelComponent.getChildren().add(LineComponent.builder().left(message).build()));
        }

        return panelComponent.render(graphics);
    }
}
