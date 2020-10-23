package com.stepcounter;

import net.runelite.api.MenuOpcode;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class StepCounterOverlay extends OverlayPanel {

    private final StepCounterConfig config;
    private final StepCounterPlugin plugin;

    @Inject
    StepCounterOverlay(StepCounterPlugin pluginIn, StepCounterConfig configIn) {
        super(pluginIn);
        setPosition(OverlayPosition.TOP_RIGHT);
        this.config = configIn;
        this.plugin = pluginIn;

        // Not actually sure what this does, but I copied it from another plugin
        getMenuEntries().add(new OverlayMenuEntry(MenuOpcode.RUNELITE_OVERLAY_CONFIG, OverlayManager.OPTION_CONFIGURE,
                "Keep Walking Overlay"));

    }

    @Override
    public Dimension render(Graphics2D graphics) {

        // If you don't want to show it, return a null Dimension
        if (!config.ShowSteps()) {
            return null;
        }

        // Have you achieved your goal?
        boolean goalComplete = this.plugin.getSteps() >= this.plugin.getGoal();

        // String to display
        String remainingSteps = this.plugin.getSteps() + " steps of " +
                this.plugin.getGoal() + " goal";

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(remainingSteps)
                .color(goalComplete ? Color.GREEN : Color.WHITE)
                .build());

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(remainingSteps) + 10,
                0));

        return super.render(graphics);
    }
}
