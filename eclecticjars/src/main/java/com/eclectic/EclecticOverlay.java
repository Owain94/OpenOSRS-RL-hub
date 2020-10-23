package com.eclectic;

import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class EclecticOverlay extends Overlay {
    private com.eclectic.EclecticPlugin plugin;

    private PanelComponent panelComponent = new PanelComponent();

    @Inject
    public EclecticOverlay(com.eclectic.EclecticPlugin plugin){
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics){
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Eclectic Jars")
                .build());

        panelComponent.getChildren().add(LineComponent.builder().left("Jars Opened:")
                .right(Integer.toString(plugin.getJarsOpened())).build());
        panelComponent.getChildren().add(LineComponent.builder().left("GP Spent:")
                .right(Double.toString(plugin.getMoneySpent())).build());
        panelComponent.getChildren().add(LineComponent.builder().left("GP Gained:")
                .right(Double.toString(plugin.getMoneyGained())).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Profit:")
                .right(Double.toString(plugin.getProfit())).rightColor(plugin.getProfitColor()).build());

        return panelComponent.render(graphics);
    }
}
