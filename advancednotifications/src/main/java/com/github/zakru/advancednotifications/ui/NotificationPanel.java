package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class NotificationPanel<N extends Notification> extends JPanel
{
	protected static class DragStarter extends MouseAdapter
	{
		private final NotificationPanel<?> panel;

		public DragStarter(NotificationPanel<?> panel)
		{
			this.panel = panel;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			panel.plugin.setDragging(panel.notification, panel.container);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (panel.plugin.getDragHovering() == null)
			{
				panel.plugin.setDragging(null, null);
			}
			else
			{
				AdvancedNotificationsPlugin plugin = panel.plugin;
				DropSpace space = plugin.getDragHovering();
				Notification notif = panel.notification;

				// Check if this is a container and the target is inside it
				if (!(notif instanceof DraggableContainer
					&& space.getContainer() instanceof Notification
					&& (notif == space.getContainer()
						|| containerContains((DraggableContainer)notif, (Notification)space.getContainer())))
				)
				{
					if (plugin.getDraggingFrom() != space.getContainer())
					{
						plugin.getDraggingFrom().getNotifications().remove(plugin.getDragging());
						space.getContainer().getNotifications().add(space.getIndex(), plugin.getDragging());
					}
					else
					{
						List<Notification> notifications = panel.container.getNotifications();
						int originalIndex = notifications.indexOf(panel.notification);
						notifications.remove(panel.notification);
						int index = space.getIndex();
						if (index > originalIndex) index = index - 1;

						notifications.add(index, panel.notification);
					}
					plugin.updateConfig();
					plugin.rebuildPluginPanel();
				}

				space.setBackground(ColorScheme.DARK_GRAY_COLOR);
				plugin.setDragging(null, null);
			}
		}

		private static boolean containerContains(DraggableContainer parent, Notification child) {
			if (parent.getNotifications().contains(child)) return true;

			for (Notification n : parent.getNotifications())
				if (n instanceof DraggableContainer && containerContains((DraggableContainer)n, child)) return true;

			return false;
		}
	}

	protected static class DefaultTypePanel extends JPanel
	{
		private static final ImageIcon DELETE_ICON;
		private static final ImageIcon DELETE_HOVER_ICON;

		private static final Border TYPE_BORDER = BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createEmptyBorder(8, 8, 8, 8));

		static
		{
			final BufferedImage deleteIcon
				= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "delete_icon.png");
			DELETE_ICON = new ImageIcon(deleteIcon);
			DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteIcon, 0.53f));
		}

		public DefaultTypePanel(NotificationPanel<?> panel, String typeName)
		{
			super(new BorderLayout());
			setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			setOpaque(false);
			setBorder(TYPE_BORDER);
			addMouseListener(new DragStarter(panel));

			JLabel typeLabel = new JLabel(typeName);
			typeLabel.setForeground(Color.WHITE);

			JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
			actions.setOpaque(false);
			actions.setBorder(BorderFactory.createEmptyBorder(0, -4, 0, -4));

			JLabel deleteButton = new JLabel(DELETE_ICON);
			deleteButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					panel.container.getNotifications().remove(panel.notification);
					panel.notification.getPlugin().updateConfig();
					panel.notification.getPlugin().rebuildPluginPanel();
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					deleteButton.setIcon(DELETE_HOVER_ICON);
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					deleteButton.setIcon(DELETE_ICON);
				}
			});

			actions.add(new EnabledButton(panel.notification.getPlugin(), panel.notification));
			actions.add(deleteButton);

			add(typeLabel, BorderLayout.WEST);
			add(actions, BorderLayout.EAST);
		}

		public void addDefaultVisualListener()
		{
			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					((DefaultTypePanel)e.getComponent()).setOpaque(true);
					e.getComponent().repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e)
				{
					((DefaultTypePanel)e.getComponent()).setOpaque(false);
					e.getComponent().repaint();
				}
			});
		}
	}

	protected final N notification;
	protected final DraggableContainer container;
	protected final AdvancedNotificationsPlugin plugin;

	public NotificationPanel(N notification, DraggableContainer container)
	{
		this.notification = notification;
		this.container = container;
		plugin = notification.getPlugin();
	}

	public static NotificationPanel<?> buildPanel(DraggableContainer container, Notification notif)
	{
		if (notif instanceof ItemNotification) return new ItemNotificationPanel((ItemNotification)notif, container);
		if (notif instanceof EmptyNotification) return new EmptyNotificationPanel((EmptyNotification)notif, container);
		if (notif instanceof NotificationGroup) return new NotificationGroupPanel((NotificationGroup)notif, container);

		return null;
	}
}
