package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.InventoryComparator;
import com.github.zakru.advancednotifications.ItemNotification;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ItemNotificationPanel extends NotificationPanel<ItemNotification>
{
	private final SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
	private final JSpinner countSpinner = new JSpinner(spinnerModel);

	public ItemNotificationPanel(ItemNotification notification, DraggableContainer container)
	{
		super(notification, container);
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		DefaultTypePanel typePanel = new DefaultTypePanel(this, "Inventory");
		typePanel.addDefaultVisualListener();

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		contentPanel.setOpaque(false);

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
		paramsPanel.setOpaque(false);

		JLabel countLabel = new JLabel("Count ");
		countLabel.setForeground(Color.WHITE);

		JComboBox<InventoryComparator> comparatorBox = new JComboBox<>(InventoryComparator.COMPARATORS);
		comparatorBox.setSelectedItem(notification.getComparator().object);
		comparatorBox.setPreferredSize(new Dimension(50, 20));
		comparatorBox.setMaximumRowCount(9);
		comparatorBox.addItemListener(e -> {
			notification.getComparator().object = (InventoryComparator)comparatorBox.getSelectedItem();
			notification.getPlugin().updateConfig();
			countSpinner.setVisible(notification.getComparator().object.takesParam());
		});

		countSpinner.setValue(notification.getComparatorParam());
		countSpinner.setPreferredSize(new Dimension(64, 20));
		countSpinner.setVisible(notification.getComparator().object.takesParam());
		countSpinner.addChangeListener(e -> {
			notification.setComparatorParam((Integer)countSpinner.getValue());
			notification.getPlugin().updateConfig();
		});

		paramsPanel.add(countLabel);
		paramsPanel.add(comparatorBox);
		paramsPanel.add(countSpinner);

		contentPanel.add(nameField, BorderLayout.NORTH);
		contentPanel.add(paramsPanel, BorderLayout.SOUTH);

		add(typePanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	private void updateItem(JTextField field)
	{
		notification.setItem(field.getText());
		notification.getPlugin().updateConfig();
	}
}
