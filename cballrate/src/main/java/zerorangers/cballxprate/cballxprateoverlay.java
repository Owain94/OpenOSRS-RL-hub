package zerorangers.cballxprate;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class cballxprateoverlay extends Overlay
{
    private zerorangers.cballxprate.cballXpRatePlugin plugin;

    private PanelComponent panelComponent = new PanelComponent();

    @Inject
    public cballxprateoverlay(zerorangers.cballxprate.cballXpRatePlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Slayer XP/Cball:")
                .right(Double.toString(plugin.getSlayerXPperCball()))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Ranged XP/Cball:")
                .right(Double.toString(plugin.getRangedXPperCball()))
                .build());

        return panelComponent.render(graphics);
    }
}
