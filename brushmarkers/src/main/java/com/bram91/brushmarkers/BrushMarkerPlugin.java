/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, Bram91 <https://github.com/Bram91>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bram91.brushmarkers;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Brush Markers",
	description = "Enable marking of tiles using the Shift key",
	tags = {"overlay", "tiles", "paint"},
	type = PluginType.UTILITY,
	enabledByDefault = false
)
public class BrushMarkerPlugin extends Plugin implements KeyListener
{
	private static final String CONFIG_GROUP = "brushMarkers";
	private static final String REGION_PREFIX = "region_";

	private static final Gson GSON = new Gson();

	@Getter(AccessLevel.PACKAGE)
	private final List<ColorTileMarker> points = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private BrushMarkerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BrushMarkerOverlay overlay;

	@Inject
	private BrushMarkerMinimapOverlay minimapOverlay;

	@Inject
	private BrushMarkerWorldmapOverlay worldmapOverlay;

	@Inject
	private KeyManager keyManager;

	private boolean ctrlHeld;
	private boolean shiftHeld;
	private int currentColor = 0;
	private Stack<BrushMemento> undoStack;
	private Stack<BrushMemento> redoStack;
	private Color pickedColor;

	private void savePoints(int regionId, Collection<BrushMarkerPoint> points)
	{
		if (points == null || points.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
			return;
		}

		String json = GSON.toJson(points);
		configManager.setConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId, json);
	}

	private Collection<BrushMarkerPoint> getPoints(int regionId)
	{
		String json = configManager.getConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
		if (Strings.isNullOrEmpty(json))
		{
			return Collections.emptyList();
		}

		// CHECKSTYLE:OFF
		return GSON.fromJson(json, new TypeToken<List<BrushMarkerPoint>>()
		{
		}.getType());
		// CHECKSTYLE:ON
	}

	public Collection<BrushMarkerPoint> getWorldPoints(int regionId)
	{
		String json = configManager.getConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
		if (Strings.isNullOrEmpty(json))
		{
			return Collections.emptyList();
		}

		// CHECKSTYLE:OFF
		return GSON.fromJson(json, new TypeToken<List<BrushMarkerPoint>>()
		{
		}.getType());
		// CHECKSTYLE:ON
	}

	@Provides
	BrushMarkerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BrushMarkerConfig.class);
	}

	private void loadPoints()
	{
		points.clear();

		int[] regions = client.getMapRegions();

		if (regions == null)
		{
			return;
		}

		for (int regionId : regions)
		{
			// load points for region
			log.debug("Loading points for region {}", regionId);
			Collection<BrushMarkerPoint> regionPoints = getPoints(regionId);
			Collection<ColorTileMarker> colorTileMarkers = translateToColorTileMarker(regionPoints);
			points.addAll(colorTileMarkers);
		}
	}

	/**
	 * Translate a collection of ground marker points to color tile markers, accounting for instances
	 *
	 * @param points {@link BrushMarkerPoint}s to be converted to {@link ColorTileMarker}s
	 * @return A collection of color tile markers, converted from the passed ground marker points, accounting for local
	 * instance points. See {@link WorldPoint#toLocalInstance(Client, WorldPoint)}
	 */
	private Collection<ColorTileMarker> translateToColorTileMarker(Collection<BrushMarkerPoint> points)
	{
		if (points.isEmpty())
		{
			return Collections.emptyList();
		}

		return points.stream()
			.map(point -> new ColorTileMarker(
				WorldPoint.fromRegion(point.getRegionId(), point.getRegionX(), point.getRegionY(), point.getZ()),
				point.getColor()))
			.flatMap(colorTile ->
			{
				final Collection<WorldPoint> localWorldPoints = WorldPoint.toLocalInstance(client, colorTile.getWorldPoint());
				return localWorldPoints.stream().map(wp -> new ColorTileMarker(wp, colorTile.getColor()));
			})
			.collect(Collectors.toList());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// map region has just been updated
		loadPoints();
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(minimapOverlay);
		overlayManager.add(worldmapOverlay);
		keyManager.registerKeyListener(this);
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		loadPoints();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(minimapOverlay);
		overlayManager.remove(worldmapOverlay);
		keyManager.unregisterKeyListener(this);
		points.clear();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (!config.paintMode())
		{
			return;
		}
		if (e.getKeyCode() == KeyEvent.VK_TAB)
		{
			if (config.doubleColors())
			{
				if (currentColor >= 11)
				{
					currentColor = 0;
				}
				else
				{
					currentColor++;
				}
			}
			else
			{
				if (currentColor >= 5)
				{
					currentColor = 0;
				}
				else
				{
					currentColor++;
				}
			}

			e.consume();
		}
		if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE)
		{
			if (config.doubleColors())
			{
				if (currentColor <= 0)
				{
					currentColor = 11;
				}
				else
				{
					currentColor--;
				}
			}
			else
			{
				if (currentColor <= 0)
				{
					currentColor = 5;
				}
				else
				{
					currentColor--;
				}
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shiftHeld = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrlHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shiftHeld = false;
		}
		else if (config.paintMode() && e.getKeyCode() == KeyEvent.VK_Z)
		{
			undo();
		}
		else if (config.paintMode() && e.getKeyCode() == KeyEvent.VK_X)
		{
			redo();
		}
		else if (config.paintMode() && e.getKeyCode() == KeyEvent.VK_Q)
		{
			pickColor();
		}
	}

	private void pickColor()
	{
		if(client.getSelectedSceneTile() != null)
		{
			WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, client.getSelectedSceneTile().getLocalLocation());
			int regionId = worldPoint.getRegionID();
			List<BrushMarkerPoint> brushMarkerPoints = new ArrayList<>(getPoints(regionId));
			BrushMarkerPoint point = new BrushMarkerPoint(regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), client.getPlane(), getColor());
			if(brushMarkerPoints.contains(point))
			{
				for (BrushMarkerPoint markerPoint : brushMarkerPoints)
				{
					if (markerPoint.getRegionY() == point.getRegionY() && markerPoint.getRegionX() == point.getRegionX())
					{
						pickedColor = markerPoint.getColor();
						currentColor = 12;
						break;
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}


	@Subscribe
	public void onClientTick(ClientTick tick)
	{
		if (config.paintMode() && client.getSelectedSceneTile() != null)
		{
			WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, client.getSelectedSceneTile().getLocalLocation());
			int regionId = worldPoint.getRegionID();
			BrushMarkerPoint point = new BrushMarkerPoint(regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), client.getPlane(), getColor());

			List<BrushMarkerPoint> brushMarkerPoints = new ArrayList<>(getPoints(regionId));

			if (shiftHeld)
			{
				for (BrushMarkerPoint brushMarkerPoint : getSelectedTiles(point))
				{
					if (brushMarkerPoint.getRegionX() >= 0 && brushMarkerPoint.getRegionX() < 64 && brushMarkerPoint.getRegionY() >= 0 && brushMarkerPoint.getRegionY() < 64)
					{
						redoStack.clear();
						if (config.replaceMode() && brushMarkerPoints.contains(brushMarkerPoint))
						{
							for (BrushMarkerPoint markerPoint : brushMarkerPoints)
							{
								if (markerPoint.getRegionY() == brushMarkerPoint.getRegionY() && markerPoint.getRegionX() == brushMarkerPoint.getRegionX())
								{
									if (undoStack.size() == 0 || !undoStack.peek().getPoint().equals(markerPoint))
									{
										undoStack.push(new BrushMemento(markerPoint, false));
									}
									break;
								}
							}
						}
						if (undoStack.size() == 0 || !undoStack.peek().getPoint().equals(brushMarkerPoint))
						{
							undoStack.push(new BrushMemento(brushMarkerPoint, true));
						}
						if (config.replaceMode())
						{
							brushMarkerPoints.remove(brushMarkerPoint);
							brushMarkerPoints.add(brushMarkerPoint);
						}
						else if (!brushMarkerPoints.contains(brushMarkerPoint))
						{
							brushMarkerPoints.add(brushMarkerPoint);
						}
					}
				}
				savePoints(regionId, brushMarkerPoints);
				loadPoints();
			}
			else if (ctrlHeld)
			{
				redoStack.clear();
				for (BrushMarkerPoint brushMarkerPoint : getSelectedTiles(point))
				{
					if (brushMarkerPoints.contains(brushMarkerPoint))
					{
						for (BrushMarkerPoint markerPoint : brushMarkerPoints)
						{
							if (markerPoint.getRegionY() == brushMarkerPoint.getRegionY() && markerPoint.getRegionX() == brushMarkerPoint.getRegionX())
							{
								undoStack.push(new BrushMemento(markerPoint, false));
								break;
							}
						}
					}
					if (brushMarkerPoints.contains(brushMarkerPoint))
					{
						brushMarkerPoints.remove(brushMarkerPoint);
					}
				}

				savePoints(regionId, brushMarkerPoints);

				loadPoints();
			}
		}
	}

	public ArrayList<BrushMarkerPoint> getSelectedTiles(BrushMarkerPoint point)
	{
		ArrayList<BrushMarkerPoint> points = new ArrayList<>();
		int size = config.brushSize().getSize();
		int offset = size / 2;

		for (int x = 0; x < size; x++)
		{
			for (int y = 0; y < size; y++)
			{
				points.add(new BrushMarkerPoint(point.getRegionId(), point.getRegionX() + x - offset, point.getRegionY() + y - offset, point.getZ(), point.getColor()));
			}
		}
		System.out.println(size+"-"+offset);
		return points;
	}

	public void undo()
	{
		if (undoStack.size() == 0)
		{
			return;
		}
		BrushMemento memento = undoStack.pop();
		redoStack.push(memento);
		List<BrushMarkerPoint> brushMarkerPoints = new ArrayList<>(getPoints(memento.getPoint().getRegionId()));
		if (!memento.isDraw())
		{
			while(brushMarkerPoints.contains(memento.getPoint()))
			{
				brushMarkerPoints.remove(memento.getPoint());
			}
			brushMarkerPoints.add(memento.getPoint());
		}
		else
		{
			while(brushMarkerPoints.contains(memento.getPoint()))
			{
				brushMarkerPoints.remove(memento.getPoint());
			}
		}
		savePoints(memento.getPoint().getRegionId(), brushMarkerPoints);
		loadPoints();
	}

	public void redo()
	{
		if (redoStack.size() == 0)
		{
			return;
		}
		BrushMemento memento = redoStack.pop();
		undoStack.push(memento);
		List<BrushMarkerPoint> brushMarkerPoints = new ArrayList<>(getPoints(memento.getPoint().getRegionId()));
		if (memento.isDraw())
		{
			brushMarkerPoints.remove(memento.getPoint());
			brushMarkerPoints.add(memento.getPoint());
		}
		else
		{
			brushMarkerPoints.remove(memento.getPoint());
		}
		savePoints(memento.getPoint().getRegionId(), brushMarkerPoints);
		loadPoints();
	}

	public Color getColor()
	{
		switch (currentColor)
		{
			case 0:
				return config.markerColor1();
			case 1:
				return config.markerColor2();
			case 2:
				return config.markerColor3();
			case 3:
				return config.markerColor4();
			case 4:
				return config.markerColor5();
			case 6:
				return config.markerColor7();
			case 7:
				return config.markerColor8();
			case 8:
				return config.markerColor9();
			case 9:
				return config.markerColor10();
			case 10:
				return config.markerColor11();
			case 11:
				return config.markerColor12();
			case 12:
				return pickedColor;
			default:
				return config.markerColor6();
		}
	}
}
