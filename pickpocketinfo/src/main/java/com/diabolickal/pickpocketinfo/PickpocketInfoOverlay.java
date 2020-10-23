package com.diabolickal.pickpocketinfo;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class PickpocketInfoOverlay extends OverlayPanel
{

    private final Client client;
    private final PickpocketInfoPlugin plugin;
    private final PickpocketInfoConfig config;

    @Inject
    PickpocketInfoOverlay(Client client, PickpocketInfoPlugin plugin, PickpocketInfoConfig config)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "PP overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        String charges = "?";
        if(plugin.dodgyCharges() >= 0)
            charges = ""+plugin.dodgyCharges();
        if(!plugin.hasDodgy())
            charges = "";

        if(plugin.lastPickpocket() != null && (config.overlayDuration() <1  || Duration.between(plugin.lastPickpocket(), Instant.now()).getSeconds() < config.overlayDuration()))
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Pickpocket Info")
                    .color(Color.WHITE)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Rate")
                    .right(String.format("%.1f",plugin.percent())+"%")
                    .build());
            if(config.showSessionTotal())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Attempts")
                        .right(String.format("%.0f",plugin.attempts()))
                        .build());
            }
            if(config.showDodgy())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(!plugin.hasDodgy() ? "No Dodgy" : "Dodgy Charges")
                        .leftColor(plugin.dodgyCharges() <= config.warnThreshold() || !plugin.hasDodgy() ? Color.RED : Color.WHITE)
                        .right(charges)
                        .rightColor(plugin.dodgyCharges() <= config.warnThreshold() && plugin.hasDodgy() ? Color.RED : Color.WHITE)
                        .build());
            }
            if(config.showPouches() && plugin.targetHasPouches())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Pouches")
                        .leftColor(plugin.pouchNum() >= 27 ? Color.RED : Color.WHITE)
                        .right(""+plugin.pouchNum())
                        .rightColor(plugin.pouchNum() >= 27 ? Color.RED : Color.WHITE)
                        .build());
            }
            if(config.showBrokenDodgys())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Broken Dodgies")
                        .right((String.format("%d",plugin.brokenDodgy())))
                        .build());
            }
            if(config.showPouchesTotal())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Total Pouches")
                        .right((String.format("%d",plugin.totalPouches())))
                        .build());
            }
        }
        return super.render(graphics);
    }
}
