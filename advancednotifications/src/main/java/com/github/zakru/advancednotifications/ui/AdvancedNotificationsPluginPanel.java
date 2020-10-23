package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.*;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class AdvancedNotificationsPluginPanel extends PluginPanel
{
	private static final ImageIcon ADD_ICON;
	private static final ImageIcon ADD_HOVER_ICON;

	private final AdvancedNotificationsPlugin plugin;

	private final JPanel notificationView;

	static
	{
		final BufferedImage addIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "add_icon.png");
		ADD_ICON = new ImageIcon(addIcon);
		ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
	}

	public AdvancedNotificationsPluginPanel(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		JLabel title = new JLabel("Notifications");
		title.setForeground(Color.WHITE);

		JPopupMenu addPopup = new JPopupMenu();
		addPopup.add(new JMenuItem(new AbstractAction("Inventory")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getNotifications().add(new ItemNotification(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));
		addPopup.add(new JMenuItem(new AbstractAction("Empty Space")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getNotifications().add(new EmptyNotification(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));
		addPopup.add(new JMenuItem(new AbstractAction("Group")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plugin.getNotifications().add(new NotificationGroup(plugin));
				plugin.updateConfig();
				rebuild();
			}
		}));

		JLabel addNotification = new JLabel(ADD_ICON);
		addNotification.setToolTipText("Add a notification");
		addNotification.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				addPopup.show(addNotification, e.getX(), e.getY());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				addNotification.setIcon(ADD_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				addNotification.setIcon(ADD_ICON);
			}
		});

		northPanel.add(title, BorderLayout.WEST);
		northPanel.add(addNotification, BorderLayout.EAST);

		notificationView = new JPanel();
		notificationView.setLayout(new BoxLayout(notificationView, BoxLayout.Y_AXIS));

		add(northPanel, BorderLayout.NORTH);
		add(notificationView, BorderLayout.CENTER);
	}

	public void rebuild()
	{
		notificationView.removeAll();

		int index = 0;
		notificationView.add(new DropSpace(plugin, plugin, index++));
		for (final Notification notif : plugin.getNotifications())
		{
			NotificationPanel<?> panel = NotificationPanel.buildPanel(plugin, notif);
			if (panel != null)
			{
				notificationView.add(panel);
				notificationView.add(new DropSpace(plugin, plugin, index++));
			}
		}

		repaint();
		revalidate();

		for (Component n : notificationView.getComponents())
		{
			if (n instanceof NotificationGroupPanel) ((NotificationGroupPanel)n).resetScroll();
		}
	}
}
