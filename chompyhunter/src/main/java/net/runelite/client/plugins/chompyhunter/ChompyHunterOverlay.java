package net.runelite.client.plugins.chompyhunter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.api.util.Text;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;


public class ChompyHunterOverlay extends Overlay
{

	private final ChompyHunterPlugin plugin;

	@Inject
	public ChompyHunterOverlay(ChompyHunterPlugin plugin)
	{
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		renderChompy(graphics);
		return null;
	}

	private void renderChompy(Graphics2D graphics)
	{
		for (Chompy chompy : plugin.getChompies().values())
		{
			Shape objectClickbox = chompy.getNpc().getConvexHull();
			long timeLeft = Duration.between(Instant.now(), chompy.getSpawnTime()).getSeconds();
			String timeLeftFormatted = timeLeft + "";
			Color color = Color.GREEN;
			if (timeLeft < 30 && timeLeft > 15)
			{
				color = Color.ORANGE;
			}
			else if (timeLeft < 15)
			{
				color = Color.RED;
			}
			if (chompy.getNpc().getName() != null && chompy.getNpc().getId() == 1475 && timeLeft > -1)
			{
				renderPoly(graphics, color, objectClickbox);
				String npcName = Text.removeTags(chompy.getNpc().getName());
				Point textLocation = chompy.getNpc().getCanvasTextLocation(graphics, npcName, chompy.getNpc().getLogicalHeight() + 40);
				if (textLocation != null)
				{
					OverlayUtil.renderTextLocation(graphics, textLocation, timeLeftFormatted, color);
				}
			}
		}
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
	{
		if (polygon != null)
		{
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(polygon);
		}
	}
}

