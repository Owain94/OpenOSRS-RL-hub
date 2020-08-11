package net.runelite.client.plugins.examinetooltip;

import com.google.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.NPC;
import net.runelite.api.Node;
import net.runelite.api.ObjectDefinition;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.TileItemPile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import static net.runelite.api.widgets.WidgetInfo.TO_CHILD;
import static net.runelite.api.widgets.WidgetInfo.TO_GROUP;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.plugins.examinetooltip.components.AlphaTooltipComponent;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.apache.commons.text.WordUtils;

public class ExamineTooltipOverlay extends Overlay
{
	private final static int SCREEN_PADDING = 5;
	private final static int EXAMINE_PADDING = 10;

	@Inject
	private TooltipManager tooltipManager;

	@Inject
	private ExamineTooltipConfig config;

	@Inject
	private ExamineTooltipPlugin plugin;

	@Inject
	private RuneLiteConfig runeLiteConfig;

	@Inject
	private Client client;

	private final Map<ExamineTextTime, Dimension> dimMap = new HashMap<>();
	private final Map<ExamineTextTime, Rectangle> rectMap = new HashMap<>();

	public ExamineTooltipOverlay()
	{
		setPosition(OverlayPosition.TOOLTIP);
		setPriority(OverlayPriority.HIGHEST);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Instant now = Instant.now();
		Duration timeout = Duration.ofSeconds(config.tooltipTimeout());
		boolean shouldClearDimMap = !dimMap.isEmpty();
		boolean shouldClearRectMap = !rectMap.isEmpty();

		for (ExamineTextTime examine : plugin.getExamines())
		{
			Duration since = Duration.between(examine.getTime(), now);
			if (since.compareTo(timeout) < 0)
			{
				long timeLeft = (timeout.minus(since)).toMillis();
				int fadeout = config.tooltipFadeout();
				double alpha;
				if (timeLeft < fadeout && fadeout > 0)
				{
					alpha = Math.min(1.0, timeLeft / (double) fadeout);
				}
				else
				{
					alpha = 1.0;
				}

				if (!config.rs3Style() || examine.getType() == ExamineType.PRICE_CHECK)
				{
					renderAsTooltip(examine, alpha);
				}
				else
				{
					renderAsRS3(examine, graphics, alpha);
					shouldClearDimMap = false;
					shouldClearRectMap = false;
				}
			}
		}

		if (shouldClearDimMap || dimMap.size() > 10)
		{
			dimMap.clear();
		}

		if (shouldClearRectMap || rectMap.size() > 10)
		{
			rectMap.clear();
		}

		return null;
	}

	private LayoutableRenderableEntity getRenderableEntity(ExamineTextTime examine, double alphaModifier)
	{
		final AlphaTooltipComponent tooltipComponent = new AlphaTooltipComponent();
		tooltipComponent.setText(getWrappedText(examine.getText()));
		tooltipComponent.setModIcons(client.getModIcons());
		tooltipComponent.setAlphaModifier(alphaModifier);

		if (config.customBackgroundColor() != null)
		{
			tooltipComponent.setBackgroundColor(config.customBackgroundColor());
		}
		else
		{
			tooltipComponent.setBackgroundColor(runeLiteConfig.overlayBackgroundColor());
		}

		return tooltipComponent;
	}

	private void renderAsTooltip(ExamineTextTime examine, double alphaModifier)
	{
		tooltipManager.add(new Tooltip(getRenderableEntity(examine, alphaModifier)));
	}

	private void renderAsRS3(ExamineTextTime examine, Graphics2D graphics, double alphaModifier)
	{
		ExamineType type = examine.getType();
		Rectangle bounds = null;
		switch (type)
		{
			case NPC:
				final NPC[] cachedNPCs = client.getCachedNPCs();
				final NPC npc = cachedNPCs[examine.getId()];
				if (npc != null)
				{
					Shape shape = npc.getConvexHull();
					if (shape != null)
					{
						bounds = shape.getBounds();
					}
				}
				break;

			case ITEM:
				int wId = examine.getWidgetId();
				Widget widget = client.getWidget(TO_GROUP(wId), TO_CHILD(wId));
				if (widget != null)
				{
					WidgetItem widgetItem = widget.getWidgetItem(examine.getActionParam());
					if (widgetItem != null)
					{
						bounds = widgetItem.getCanvasBounds(false);
					}
				}
				break;

			case ITEM_INTERFACE:
				bounds = findWidgetBounds(examine.getWidgetId(), examine.getActionParam());
				break;

			case ITEM_GROUND:
			case OBJECT:
				// Yes, for these, ActionParam and WidgetID are scene coordinates
				LocalPoint point = LocalPoint.fromScene(examine.getActionParam(), examine.getWidgetId());
				int id = examine.getId();

				Tile tile = client.getScene().getTiles()
					[client.getPlane()][point.getSceneX()][point.getSceneY()];

				if (tile != null)
				{
					Shape shape = getObjectShapeFromTile(tile, type, id);
					if (shape == null)
					{
						Tile bridge = tile.getBridge();
						if (bridge != null)
						{
							shape = getObjectShapeFromTile(bridge, type, id);
						}
					}

					if (shape == null)
					{
						// Fallback to tile
						shape = Perspective.getCanvasTilePoly(client, point);
					}

					if (shape != null)
					{
						bounds = shape.getBounds();
					}
				}

				break;

			default:
				return;
		}

		// Try previously known location
		if (bounds == null && config.previousBoundsFallback())
		{
			bounds = rectMap.get(examine);
		}

		// Give up and render as tooltip if target not found
		if (bounds == null)
		{
			if (config.tooltipFallback())
			{
				renderAsTooltip(examine, alphaModifier);
			}
			return;
		}

		boolean isInterfaceExamine = type == ExamineType.ITEM || type == ExamineType.ITEM_INTERFACE;

		int x = bounds.x;
		int y = bounds.height + bounds.y;

		if (!isInterfaceExamine)
		{
			x -= EXAMINE_PADDING;
			y += EXAMINE_PADDING;
		}

		final LayoutableRenderableEntity tooltipComponent = getRenderableEntity(examine, alphaModifier);

		if (isInterfaceExamine || config.clampRS3())
		{
			Dimension dim = dimMap.get(examine);
			if (dim != null)
			{
				int xMin, xMax, yMin, yMax;

				if (isInterfaceExamine)
				{
					xMin = 0;
					xMax = client.getCanvas().getSize().width;
					yMin = 0;
					yMax = client.getCanvas().getSize().height;
				}
				else
				{
					xMin = client.getViewportXOffset();
					xMax = client.getViewportWidth() + xMin;
					yMin = client.getViewportYOffset();
					yMax = client.getViewportHeight() + yMin;
				}

				xMin += SCREEN_PADDING;
				xMax -= SCREEN_PADDING;
				yMin += SCREEN_PADDING;
				yMax -= SCREEN_PADDING;

				if (x < xMin)
				{
					x = xMin;
				}
				else if (x + dim.width > xMax)
				{
					x = xMax - dim.width;
				}

				if (y < yMin)
				{
					y = yMin;
				}
				else if (y + dim.height > yMax)
				{
					y = yMax - dim.height;
				}
			}
		}

		tooltipComponent.setPreferredLocation(new Point(x, y));
		dimMap.put(examine, tooltipComponent.render(graphics));
		rectMap.put(examine, bounds);
	}

	private String getWrappedText(String text)
	{
		if (config.wrapTooltip())
		{
			return WordUtils.wrap(text, config.wrapTooltipColumns(), "</br>", false);
		}
		else
		{
			return text;
		}
	}

	private Rectangle findWidgetBounds(int widgetId, int actionParam)
	{
		Widget widget = client.getWidget(TO_GROUP(widgetId), TO_CHILD(widgetId));

		if (widget == null)
		{
			return null;
		}

		if (actionParam < 0)
		{
			return widget.getBounds();
		}

		try
		{
			Widget widgetItem = widget.getChild(actionParam);
			if (widgetItem != null)
			{
				return widgetItem.getBounds();
			}
		}
		catch (Exception e)
		{
			// Ignore
		}

		return null;
	}

	private Shape getObjectShapeFromTile(Tile tile, ExamineType type, int id)
	{
		Shape shape = null;
		if (type == ExamineType.ITEM_GROUND)
		{
			TileItemPile itemLayer = tile.getItemLayer();
			if (itemLayer != null)
			{
				Node current = itemLayer.getBottom();
				while (current instanceof TileItem)
				{
					if (((TileItem) current).getId() == id)
					{
						shape = itemLayer.getCanvasTilePoly();
						break;
					}
					current = current.getNext();
				}
			}
		}
		else
		{
			GameObject[] gameObjects = tile.getGameObjects();
			if (gameObjects != null)
			{
				for (GameObject object : gameObjects)
				{
					if (object != null)
					{
						int objId = object.getId();
						ObjectDefinition comp = client.getObjectDefinition(objId);
						if (comp != null)
						{
							try
							{
								ObjectDefinition impostor = comp.getImpostor();
								if (impostor != null)
								{
									objId = impostor.getId();
								}
							}
							catch (Exception e)
							{
								// Ignore
							}
						}
						if (objId == id)
						{
							shape = object.getConvexHull();
							if (shape != null)
							{
								break;
							}
						}
					}
				}
			}

			if (shape == null)
			{
				GroundObject object = tile.getGroundObject();
				if (object != null && object.getId() == id)
				{
					shape = object.getConvexHull();
				}
			}

			if (shape == null)
			{
				DecorativeObject object = tile.getDecorativeObject();
				if (object != null && object.getId() == id)
				{
					shape = object.getConvexHull();
				}
			}

			if (shape == null)
			{
				WallObject object = tile.getWallObject();
				if (object != null && object.getId() == id)
				{
					shape = object.getConvexHull();
				}
			}
		}

		return shape;
	}
}
