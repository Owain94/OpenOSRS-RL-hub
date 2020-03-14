package net.runelite.client.plugins.advancednotifications.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.advancednotifications.AdvancedNotificationsPlugin;
import net.runelite.client.plugins.advancednotifications.EmptyNotification;
import net.runelite.client.plugins.advancednotifications.ItemNotification;
import net.runelite.client.plugins.advancednotifications.Notification;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

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
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

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

		for (final Notification notif : plugin.getNotifications())
		{
			NotificationPanel panel = buildPanel(notif);
			if (panel != null)
			{
				notificationView.add(panel);
				notificationView.add(Box.createRigidArea(new Dimension(0, 10)));
			}
		}

		repaint();
		revalidate();
	}

	private NotificationPanel buildPanel(Notification notif)
	{
		if (notif instanceof ItemNotification)
		{
			return new ItemNotificationPanel((ItemNotification) notif);
		}
		if (notif instanceof EmptyNotification)
		{
			return new EmptyNotificationPanel((EmptyNotification) notif);
		}

		return null;
	}
}