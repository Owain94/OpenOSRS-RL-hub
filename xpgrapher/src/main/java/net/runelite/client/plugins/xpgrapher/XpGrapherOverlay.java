package net.runelite.client.plugins.xpgrapher;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class XpGrapherOverlay extends Overlay
{
	private final XpGrapherPlugin grapherPlugin;

	@Inject
	private XpGrapherOverlay(XpGrapherPlugin grapherPlugin)
	{
		this.grapherPlugin = grapherPlugin;
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		graphics.setColor(Color.GRAY);
		graphics.drawRect(0, 0, grapherPlugin.width, grapherPlugin.height);
		graphics.setColor(Color.WHITE);

		int numberOfPoints = grapherPlugin.graphPoints.size();
		int oldX = -1;
		int oldY = -1;
		for (int i = 0; i < numberOfPoints; i++)
		{
			Integer[] point = grapherPlugin.graphPoints.get(i);
			int x = point[0];
			int y = point[1];
			if (y < grapherPlugin.height && y >= 0)
			{
				graphics.drawLine(x, y, x, y);
			}
			if (oldX != -1 && oldY != -1)
			{
				graphics.drawLine(oldX + 1, oldY, x, y);
			}

			oldX = x;
			oldY = y;
		}
		return null;
	}
}
