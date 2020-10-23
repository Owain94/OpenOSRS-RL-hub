package com.iipom.mapwaypoint;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

import static net.runelite.api.SpriteID.MINIMAP_GUIDE_ARROW_YELLOW;

public class WaypointArrowOverlay extends Overlay
{
    private BufferedImage ARROW_ICON;

    private final Client client;
    private final MapWaypointPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private final TitleComponent stepsComponent = TitleComponent.builder().build();

    @Inject
    private WaypointArrowOverlay(Client client, MapWaypointPlugin plugin, SpriteManager spriteManager)
    {
        setPosition(OverlayPosition.TOP_CENTER);
        this.client = client;
        this.plugin = plugin;

        spriteManager.getSpriteAsync(MINIMAP_GUIDE_ARROW_YELLOW, 1, sprite ->
        {
            ARROW_ICON = ImageUtil.rotateImage(sprite, 3 * Math.PI / 2);
        });
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getWaypoint() == null)
        {
            return null;
        }

        final Player player = client.getLocalPlayer();
        if (player == null)
        {
            return null;
        }

        final WorldPoint currentLocation = player.getWorldLocation();
        final WorldPoint destination = plugin.getWaypoint().getWorldPoint();

        final int distance = currentLocation.distanceTo(destination);
        final String steps = "Steps: " + distance;

        final BufferedImage arrow = calculateImageRotation(currentLocation, destination, graphics.getFontMetrics().stringWidth(steps));

        stepsComponent.setText(steps);
        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(new ImageComponent(arrow));
        panelComponent.getChildren().add(stepsComponent);
        panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(steps) + 10, 0));

        return panelComponent.render(graphics);
    }

    private BufferedImage calculateImageRotation(WorldPoint currentLocation, WorldPoint destination, int textLen)
    {
        double angle = calculateAngle(currentLocation, destination);
        final int dx = (textLen - ARROW_ICON.getWidth()) / 2;

        final BufferedImage rotatedImage = ImageUtil.rotateImage(ARROW_ICON, 2.0 * Math.PI - angle);
        final BufferedImage finalImage = new BufferedImage(rotatedImage.getWidth() + dx, ARROW_ICON.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
        finalImage.getGraphics().drawImage(rotatedImage, dx, 0, null);

        return finalImage;
    }

    private double calculateAngle(WorldPoint currentLocation, WorldPoint destination)
    {
        final int dx = destination.getX() - currentLocation.getX();
        final int dy = destination.getY() - currentLocation.getY();

        final double angle = Math.atan2(dy, dx);
        final double clientAngle = (client.getMapAngle() / 2048.0) * 2.0 * Math.PI;

        return angle - clientAngle;
    }
}
