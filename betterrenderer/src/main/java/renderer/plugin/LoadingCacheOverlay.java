package renderer.plugin;

import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class LoadingCacheOverlay extends Overlay {
    private final Client client;
    private final BetterRendererPlugin plugin;

    @Inject
    public LoadingCacheOverlay(Client client, BetterRendererPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.setFont(FontManager.getRunescapeBoldFont());

        String s = "Downloading game cache, this may take a few minutes";
        Rectangle2D bounds = graphics.getFontMetrics().getStringBounds(s, graphics);

        graphics.setColor(new Color(255, 255, 255));
        graphics.fillRect(
                client.getCanvasWidth() / 2 - (int) bounds.getWidth() /2 - 10,
                client.getCanvasHeight() / 2 - (int) bounds.getHeight()  - 10,
                (int) bounds.getWidth() + 20,
                (int) bounds.getHeight() + 10
        );

        graphics.setColor(new Color(0, 128, 255));
        graphics.drawString(s, client.getCanvasWidth() / 2 - (int) bounds.getWidth() / 2, client.getCanvasHeight() / 2 - (int) bounds.getHeight() / 2);
        return null;
    }
}
