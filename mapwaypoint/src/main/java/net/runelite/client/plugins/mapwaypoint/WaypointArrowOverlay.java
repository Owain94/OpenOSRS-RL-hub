package net.runelite.client.plugins.mapwaypoint;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

public class WaypointArrowOverlay extends Overlay
{

	private static final BufferedImage ARROW_ICON = ImageUtil.getResourceStreamFromClass(MapWaypointPlugin.class, "arrow.png");

	private final Client client;
	private final MapWaypointPlugin plugin;
	private final PanelComponent panelComponent = new PanelComponent();
	private final TitleComponent stepsComponent = TitleComponent.builder().build();

	private int steps;

	@Inject
	private WaypointArrowOverlay(Client client, MapWaypointPlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;
		setPosition(OverlayPosition.TOP_CENTER);

		steps = 0;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.getWaypoint() == null)
		{
			return null;
		}

		WorldPoint currentLocation = Objects.requireNonNull(client.getLocalPlayer()).getWorldLocation();
		WorldPoint destination = plugin.getWaypoint().getWorldPoint();

		double angle = calculateAngle(currentLocation, destination);

		BufferedImage rotatedImage = ImageUtil.rotateImage(ARROW_ICON, 2.0 * Math.PI - angle);
		BufferedImage finalImage = rotatedImage.getSubimage(0, 40, 130, 50);
		panelComponent.getChildren().clear();
		panelComponent.getChildren().add(new ImageComponent(finalImage));

		stepsComponent.setText("Steps: " + steps);
		panelComponent.getChildren().add(stepsComponent);

		return panelComponent.render(graphics);
	}

	private double calculateAngle(WorldPoint currentLocation, WorldPoint destination)
	{
		int dx = destination.getX() - currentLocation.getX();
		int dy = destination.getY() - currentLocation.getY();
		steps = calculateSteps(dx, dy);

		double angle = Math.atan(Math.abs(((double) dy) / dx));
		if (dx == 0)
		{
			if (dy > 0)
			{
				angle = Math.PI / 2.0;
			}
			else
			{
				angle = 3.0 * Math.PI / 2.0;
			}
		}
		else if (dy == 0)
		{
			if (dx > 0)
			{
				angle = 0.0;
			}
			else
			{
				angle = Math.PI;
			}
		}
		else if (dx < 0 && dy > 0)
		{
			angle = Math.PI - angle;
		}
		else if (dx < 0 && dy < 0)
		{
			angle += Math.PI;
		}
		else if (dx > 0 && dy < 0)
		{
			angle = 2.0 * Math.PI - angle;
		}

		double clientAngle = (client.getMapAngle() / 2048.0) * 2.0 * Math.PI;
		return angle - clientAngle;
	}

	private int calculateSteps(int dx, int dy)
	{
		return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
	}
}