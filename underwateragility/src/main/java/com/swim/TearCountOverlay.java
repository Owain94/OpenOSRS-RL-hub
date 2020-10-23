package com.swim;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import javax.inject.Inject;
import java.awt.*;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
public class TearCountOverlay extends OverlayPanel
{
    static final String SWIM_RESET = "Reset";
    private final SwimPlugin plugin;
    private final SwimConfig config;
    private static final String UNICODE_CHECK_MARK = "\u2713";
    private static final String UNICODE_BALLOT_X = "\u2717";

    @Inject
    private TearCountOverlay(SwimPlugin plugin, SwimConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Swim overlay"));
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, SWIM_RESET, "Swim overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isUnderwater())
        {
            return null;
        }

        if (config.isTearCountShown() && plugin.getTearCount() > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Tears:")
                    .right(Integer.toString(plugin.getTearCount()))
                    .build());
        }

        if (config.isTearsPerHourShown() && plugin.getStart() != null && plugin.getTearsPerHour() > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Tears per hour:")
                    .right(Integer.toString(plugin.getTearsPerHour()))
                    .build());
        }

        if (config.isDistanceShown() && plugin.getDistance() > 0)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Distance:")
                    .right(Integer.toString(plugin.getDistance()))
                    .build());
        }

        if (config.isDirectionShown() && plugin.getDirection() != null)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Direction:")
                    .right(plugin.getDirection())
                    .build());
        }

        if (config.isAdjacentBubbleShown())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Bubble:")
                    .right(plugin.isBubbleAdjacent() ? UNICODE_CHECK_MARK : UNICODE_BALLOT_X)
                    .rightColor(plugin.isBubbleAdjacent() ? Color.GREEN : Color.RED)
                    .build());
        }

        return super.render(graphics);
    }
}
