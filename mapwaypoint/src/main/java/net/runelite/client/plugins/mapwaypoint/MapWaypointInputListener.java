package net.runelite.client.plugins.mapwaypoint;

import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.client.input.MouseListener;

public class MapWaypointInputListener implements MouseListener
{

	private final MapWaypointPlugin plugin;
	private final MapWaypointConfig config;

	@Inject
	private MapWaypointInputListener(MapWaypointPlugin plugin, MapWaypointConfig config)
	{
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent)
	{
		if (mouseEvent.getButton() == 1 && (mouseEvent.getClickCount() == 2 || (config.shiftClick() && mouseEvent.isShiftDown())))
		{
			plugin.mouseClicked();
		}

		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}
}