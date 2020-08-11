package net.runelite.client.plugins.underwateragility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class SwimMinimapOverlay extends Overlay
{
	private final SwimConfig config;
	private final SwimPlugin plugin;


	@Inject
	private SwimMinimapOverlay(SwimPlugin plugin, SwimConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.isPufferShown() || plugin.getPuffers() == null)
		{
			return null;
		}

		for (NPC puffer : plugin.getPuffers())
		{
			Point pufferLocation = puffer.getMinimapLocation();
			Color color = config.getPufferColor();
			if (pufferLocation != null)
			{
				OverlayUtil.renderMinimapLocation(graphics, pufferLocation, color);
			}
		}

		return null;
	}
}
