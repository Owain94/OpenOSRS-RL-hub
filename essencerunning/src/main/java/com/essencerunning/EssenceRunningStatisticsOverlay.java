package com.essencerunning;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.inject.Inject;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class EssenceRunningStatisticsOverlay extends Overlay {

    private final EssenceRunningPlugin plugin;
    private final EssenceRunningConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private EssenceRunningStatisticsOverlay(final EssenceRunningPlugin plugin, final EssenceRunningConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        if (config.enableRunecrafterMode() && config.sessionStatistics()) {
            final EssenceRunningSession session = plugin.getSession();

            panelComponent.getChildren().add(LineComponent.builder().left("Runner").right("Ess/Neck").build());

            session.getRunners().forEach(runner -> panelComponent.getChildren().add(
                LineComponent.builder()
                    .left(runner.getRsn())
                    .right(runner.getPureEssenceTraded() + "/" + runner.getBindingNecklaceTraded())
                    .build()));

            if (session.getTotalFireRunesCrafted() > 0) {
                panelComponent.getChildren().add(LineComponent.builder().left("Fire Runes Crafted").right(
                    Integer.toString(session.getTotalFireRunesCrafted())).build());
            }
        }

        return panelComponent.render(graphics);
    }
}
