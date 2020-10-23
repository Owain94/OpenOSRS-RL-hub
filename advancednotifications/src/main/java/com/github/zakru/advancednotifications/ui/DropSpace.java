package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.AdvancedNotificationsPlugin;
import com.github.zakru.advancednotifications.DraggableContainer;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DropSpace extends JPanel
{
	private static final MouseAdapter LISTENER = new MouseAdapter()
	{
		@Override
		public void mouseEntered(MouseEvent e)
		{
			DropSpace space = (DropSpace)e.getComponent();
			if (space.plugin.getDragging() != null)
			{
				space.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
				space.plugin.setDragHovering(space);
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			DropSpace space = (DropSpace)e.getComponent();
			if (space.plugin.getDragging() != null)
			{
				space.setBackground(ColorScheme.DARK_GRAY_COLOR);
				space.plugin.setDragHovering(null);
			}
		}
	};

	private final AdvancedNotificationsPlugin plugin;
	@Getter
	private final DraggableContainer container;
	@Getter
	private final int index;

	public DropSpace(AdvancedNotificationsPlugin plugin, DraggableContainer container, int index)
	{
		this.plugin = plugin;
		this.container = container;
		this.index = index;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setPreferredSize(new Dimension(0,10));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		addMouseListener(LISTENER);
	}
}
