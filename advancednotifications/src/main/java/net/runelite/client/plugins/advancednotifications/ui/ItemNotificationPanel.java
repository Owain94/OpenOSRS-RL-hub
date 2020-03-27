package net.runelite.client.plugins.advancednotifications.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.plugins.advancednotifications.AdvancedNotificationsPlugin;
import net.runelite.client.plugins.advancednotifications.InventoryComparator;
import net.runelite.client.plugins.advancednotifications.ItemNotification;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

public class ItemNotificationPanel extends NotificationPanel
{
	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;

	private static final Border TYPE_BORDER = new CompoundBorder(
		new MatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
		new EmptyBorder(8, 8, 8, 8));

	private ItemNotification notification;

	private final JSpinner countSpinner = new JSpinner();

	static
	{
		final BufferedImage addIcon
			= ImageUtil.getResourceStreamFromClass(AdvancedNotificationsPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(addIcon);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
	}

	public ItemNotificationPanel(ItemNotification notification)
	{
		this.notification = notification;
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel typeWrapper = new JPanel(new BorderLayout());
		typeWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		typeWrapper.setBorder(TYPE_BORDER);

		JLabel typeLabel = new JLabel("Inventory");
		typeLabel.setForeground(Color.WHITE);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		actions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel deleteButton = new JLabel(DELETE_ICON);
		deleteButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				notification.getPlugin().getNotifications().remove(notification);
				notification.getPlugin().updateConfig();
				notification.getPlugin().rebuildPluginPanel();
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

		actions.add(new EnabledButton(notification.getPlugin(), notification));
		actions.add(deleteButton);

		typeWrapper.add(typeLabel, BorderLayout.WEST);
		typeWrapper.add(actions, BorderLayout.EAST);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		contentPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JTextField nameField = new JTextField(notification.getItem());
		nameField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				updateItem(nameField);
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				updateItem(nameField);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				updateItem(nameField);
			}
		});

		JPanel paramsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		paramsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel countLabel = new JLabel("Count ");
		countLabel.setForeground(Color.WHITE);

		JComboBox<InventoryComparator> comparatorBox = new JComboBox<>(InventoryComparator.COMPARATORS);
		comparatorBox.setSelectedItem(notification.getComparator());
		comparatorBox.setPreferredSize(new Dimension(50, 20));
		comparatorBox.setMaximumRowCount(9);
		comparatorBox.addItemListener(e -> {
			notification.setComparator((InventoryComparator) comparatorBox.getSelectedItem());
			notification.getPlugin().updateConfig();
			countSpinner.setVisible(notification.getComparator().takesParam());
		});

		countSpinner.setValue(notification.getComparatorParam());
		countSpinner.setPreferredSize(new Dimension(64, 20));
		countSpinner.setVisible(notification.getComparator().takesParam());
		countSpinner.addChangeListener(e -> {
			notification.setComparatorParam((Integer) countSpinner.getValue());
			notification.getPlugin().updateConfig();
		});

		paramsPanel.add(countLabel);
		paramsPanel.add(comparatorBox);
		paramsPanel.add(countSpinner);

		contentPanel.add(nameField, BorderLayout.NORTH);
		contentPanel.add(paramsPanel, BorderLayout.SOUTH);

		add(typeWrapper, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	private void updateItem(JTextField field)
	{
		notification.setItem(field.getText());
		notification.getPlugin().updateConfig();
	}
}