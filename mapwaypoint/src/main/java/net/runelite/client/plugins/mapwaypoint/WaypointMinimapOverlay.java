package net.runelite.client.plugins.mapwaypoint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

public class WaypointMinimapOverlay extends Overlay
{
	private static final int MAX_DRAW_DISTANCE = 16;
	private static final int TILE_WIDTH = 4;
	private static final int TILE_HEIGHT = 4;
	private static final Color TILE_COLOR = new Color(0, 201, 198);

	private final Client client;
	private final MapWaypointPlugin plugin;
	private final MapWaypointConfig config;

	@Inject
	private WaypointMinimapOverlay(Client client, MapWaypointPlugin plugin, MapWaypointConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.drawMinimap())
		{
			WorldMapPoint waypoint = plugin.getWaypoint();
			if (waypoint != null)
			{
				drawOnMinimap(graphics, waypoint.getWorldPoint());
			}
		}

		return null;
	}

	private void drawOnMinimap(Graphics2D graphics, WorldPoint point)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		if (point.distanceTo(player.getWorldLocation()) >= MAX_DRAW_DISTANCE)
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		Point posOnMinimap = Perspective.localToMinimap(client, lp);
		if (posOnMinimap == null)
		{
			return;
		}

		OverlayUtil.renderMinimapRect(client, graphics, posOnMinimap, TILE_WIDTH, TILE_HEIGHT, TILE_COLOR);
	}
}