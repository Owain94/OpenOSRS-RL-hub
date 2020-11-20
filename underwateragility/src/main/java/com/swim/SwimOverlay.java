package com.swim;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCDefinition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.NpcDefinitionChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import java.awt.*;
import java.util.Set;

@Slf4j
public class SwimOverlay extends Overlay
{
    private final SwimPlugin plugin;
    private final SwimConfig config;
    private final Client client;

    @Inject
    public SwimOverlay(Client client,  SwimPlugin plugin, SwimConfig config)
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
        try {
            if (!plugin.isUnderwater())
            {
                return null;
            }

            if (config.isHazardShown())
            {
                plugin.getHazards().forEach((object,tile) ->
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
                    NPCDefinition npcComposition = npc.getTransformedDefinition();
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

                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client,chest,1);

                if (tilePoly != null)
                {
                    OverlayUtil.renderPolygon(graphics, tilePoly, config.getChestColor());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
