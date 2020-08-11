package net.runelite.client.plugins.underwateragility;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCDefinition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

@Slf4j
public class SwimOverlay extends Overlay
{
	private final SwimPlugin plugin;
	private final SwimConfig config;
	private final Client client;

	@Inject
	public SwimOverlay(Client client, SwimPlugin plugin, SwimConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.config = config;
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		try
		{
			if (!plugin.isUnderwater())
			{
				return null;
			}

			if (config.isHazardShown())
			{
				plugin.getHazards().forEach((object, tile) ->
				{
					if (tile.getPlane() == client.getPlane())
					{
						Polygon polygon = object.getCanvasTilePoly();

						if (polygon != null)
						{
							OverlayUtil.renderPolygon(graphics, polygon, config.getHazardColor());
						}
					}
				});
			}

			Set<NPC> npcs = plugin.getPuffers();

			if (!npcs.isEmpty() && config.isPufferShown())
			{
				Color color = config.getPufferColor();

				for (NPC npc : npcs)
				{
					NPCDefinition npcComposition = npc.getDefinition();
					int size = npcComposition.getSize();
					LocalPoint lp = npc.getLocalLocation();

					Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);

					if (tilePoly != null)
					{
						OverlayUtil.renderPolygon(graphics, tilePoly, color);
					}
				}
			}

			if (config.isChestShown() && plugin.isChestLoaded())
			{
				LocalPoint chest = LocalPoint.fromWorld(client, plugin.getLastChestPosition());

				if (chest == null)
				{
					return null;
				}

				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, chest, 1);

				if (tilePoly != null)
				{
					OverlayUtil.renderPolygon(graphics, tilePoly, config.getChestColor());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
