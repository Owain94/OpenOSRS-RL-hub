package com.iipom.mapwaypoint;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class WaypointTileOverlay extends Overlay
{

    private static final int DEFAULT_DRAW_DISTANCE = 25;
    private static final Color TILE_COLOR = new Color(0, 201, 198);

    private final Client client;
    private final MapWaypointPlugin plugin;
    private final MapWaypointConfig config;

    @Inject
    private WaypointTileOverlay(Client client, MapWaypointPlugin plugin, MapWaypointConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getWaypoint() != null && config.drawTile())
        {
            final WorldPoint waypoint = plugin.getWaypoint().getWorldPoint();

            if (client.getPlane() == waypoint.getPlane())
            {
                drawTile(graphics, waypoint);
            }
        }

        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint waypoint)
    {
        final Player player = client.getLocalPlayer();
        if (player == null)
        {
            return;
        }

        final int drawDistance = client.getScene().getDrawDistance() != 0 ? client.getScene().getDrawDistance() : DEFAULT_DRAW_DISTANCE;

        final WorldPoint playerLocation = player.getWorldLocation();
        if (waypoint.distanceTo(playerLocation) >= drawDistance)
        {
            return;
        }

        final LocalPoint lp = LocalPoint.fromWorld(client, waypoint);
        if (lp == null)
        {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
        {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, TILE_COLOR);
    }
}
