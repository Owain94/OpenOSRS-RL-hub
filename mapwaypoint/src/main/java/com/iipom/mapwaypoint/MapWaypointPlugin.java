package com.iipom.mapwaypoint;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Map Waypoints",
	description = "Adds waypoint functionality to the world map (via double-click) with a direction overlay",
	tags = {"map", "waypoint", "distance"},
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class MapWaypointPlugin extends Plugin
{
    private static final String WALK_HERE = "Walk here";
    private static final String FOCUS = "Focus";
    private static final String CLOSE = "Close";
    private static final String SET_WAYPOINT = "Set Waypoint";
    private static final String FOCUS_WAYPOINT = "Focus Waypoint";
    private static final String REMOVE_WAYPOINT = "Remove Waypoint";

    private static final BufferedImage WAYPOINT_ICON;

    static
    {
        WAYPOINT_ICON = new BufferedImage(37, 37, BufferedImage.TYPE_INT_ARGB);
        final BufferedImage waypointIcon = ImageUtil.getResourceStreamFromClass(MapWaypointPlugin.class, "waypoint.png");
        WAYPOINT_ICON.getGraphics().drawImage(waypointIcon, 0, 0, null);
    }

    private Point lastMenuOpenedPoint;

    @Getter(AccessLevel.PACKAGE)
    private WorldMapPoint waypoint;

    @Inject
    private Client client;

    @Inject
    private MapWaypointConfig config;

    @Inject
    private MapWaypointInputListener inputListener;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private WorldMapPointManager worldMapPointManager;

    @Inject
    private WorldMapOverlay worldMapOverlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WaypointArrowOverlay waypointArrowOverlay;

    @Inject
    private WaypointMinimapOverlay waypointMinimapOverlay;

    @Inject
    private WaypointTileOverlay waypointTileOverlay;

    public void mouseClicked()
    {
        if (isMouseInWorldMap())
        {
            final Point mousePos = client.getMouseCanvasPosition();

            if (waypoint != null && waypoint.getClickbox().contains(mousePos.getX(), mousePos.getY()))
            {
                removeWaypoint();
            }
            else
            {
                setWaypoint(mousePos);
            }
        }
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event)
    {
        if (isMouseInWorldMap())
        {
            lastMenuOpenedPoint = client.getMouseCanvasPosition();
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (waypoint != null)
        {
            if (event.getOption().equals(WALK_HERE))
            {
                final Tile selectedSceneTile = client.getSelectedSceneTile();
                if (selectedSceneTile == null)
                {
                    return;
                }

                if (selectedSceneTile.getWorldLocation().equals(waypoint.getWorldPoint()))
                {
                    addMenuEntry(event, REMOVE_WAYPOINT);
                    return;
                }
            }
        }

        if (isMouseInWorldMap())
        {
            if (event.getOption().equals(WALK_HERE))
            {
                return;
            }

            final Point mousePos = client.getMouseCanvasPosition();
            try
            {
                if (waypoint != null && waypoint.getClickbox().contains(mousePos.getX(), mousePos.getY()))
                {
                    if (!event.getOption().equals(FOCUS) && !event.getOption().equals(CLOSE))
                    {
                        addMenuEntry(event, REMOVE_WAYPOINT);
                        addMenuEntry(event, FOCUS_WAYPOINT);
                    }
                }
                else if (!event.getOption().equals(FOCUS) && !event.getOption().equals(CLOSE))
                {
                    addMenuEntry(event, SET_WAYPOINT);
                }
            }
            catch (NullPointerException e)
            {
                // Nothing wrong actually happened here, this only ever gets thrown sometimes when you add a waypoint and it checks its clickbox while being created
                // I added the try...catch so the users console doesn't get output from it because I'm not sure how to fix it
                // I'm assuming it has something to do with the mouse already being positioned over the waypoint when it's created and the way Runelite/Runescape handles menu creation
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.getMenuOpcode().getId() != MenuOpcode.RUNELITE.getId() || !client.isMenuOpen())
        {
            return;
        }

        if (event.getOption().equals(SET_WAYPOINT))
        {
            setWaypoint(lastMenuOpenedPoint);
        }
        else if (event.getOption().equals(REMOVE_WAYPOINT))
        {
            if (waypoint != null)
            {
                removeWaypoint();
            }
        }
        else if (event.getOption().equals(FOCUS_WAYPOINT))
        {
            if (waypoint != null)
            {
                client.getRenderOverview().setWorldMapPositionTarget(waypoint.getWorldPoint());
                playSoundEffect();
            }
        }
    }

    @Override
    public void startUp()
    {
        mouseManager.registerMouseListener(inputListener);

        overlayManager.add(waypointArrowOverlay);
        overlayManager.add(waypointMinimapOverlay);
        overlayManager.add(waypointTileOverlay);

        waypoint = null;
    }

    @Override
    public void shutDown()
    {
        mouseManager.unregisterMouseListener(inputListener);

        overlayManager.remove(waypointArrowOverlay);
        overlayManager.remove(waypointMinimapOverlay);
        overlayManager.remove(waypointTileOverlay);

        worldMapPointManager.removeIf(x -> x == waypoint);

        waypoint = null;
    }

    private void setWaypoint(final Point mousePos)
    {
        final RenderOverview renderOverview = client.getRenderOverview();

        final float zoom = renderOverview.getWorldMapZoom();
        final WorldPoint destination = calculateMapPoint(renderOverview, mousePos, zoom);

        worldMapPointManager.removeIf(x -> x == waypoint);
        waypoint = new WorldMapPoint(destination, WAYPOINT_ICON);
        waypoint.setTarget(waypoint.getWorldPoint());
        waypoint.setJumpOnClick(true);
        worldMapPointManager.add(waypoint);

        playSoundEffect();
    }

    private void removeWaypoint()
    {
        worldMapPointManager.remove(waypoint);
        waypoint = null;

        playSoundEffect();
    }

    private WorldPoint calculateMapPoint(RenderOverview renderOverview, Point mousePos, float zoom)
    {
        final WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(), renderOverview.getWorldMapPosition().getY(), 0);
        final Point middle = worldMapOverlay.mapWorldPointToGraphicsPoint(mapPoint);

        final int dx = (int) ((mousePos.getX() - middle.getX()) / zoom);
        final int dy = (int) ((-(mousePos.getY() - middle.getY())) / zoom);

        return mapPoint.dx(dx).dy(dy);
    }

    private boolean isMouseInWorldMap()
    {
        final Point mousePos = client.getMouseCanvasPosition();
        final Widget view = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
        if (view == null)
        {
            return false;
        }

        final Rectangle worldMapBounds = view.getBounds();
        return worldMapBounds.contains(mousePos.getX(), mousePos.getY());
    }

    private void addMenuEntry(MenuEntryAdded event, String option)
    {
        MenuEntry[] menuEntries = client.getMenuEntries();
        menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);
        final MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();

        menuEntry.setOption(option);
        menuEntry.setTarget(event.getTarget());
        menuEntry.setOpcode(MenuOpcode.RUNELITE.getId());

        client.setMenuEntries(menuEntries);
    }

    private void playSoundEffect()
    {
        if (config.playSoundEffect())
        {
            client.playSoundEffect(SoundEffectID.UI_BOOP);
        }
    }

    @Provides
    MapWaypointConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MapWaypointConfig.class);
    }
}
