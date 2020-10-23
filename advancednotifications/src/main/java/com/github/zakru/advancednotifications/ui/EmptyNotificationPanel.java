package com.github.zakru.advancednotifications.ui;

import com.github.zakru.advancednotifications.DraggableContainer;
import com.github.zakru.advancednotifications.EmptyNotification;
import com.github.zakru.advancednotifications.InventoryComparator;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class EmptyNotificationPanel extends NotificationPanel<EmptyNotification>
{
	private final SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
	private final JSpinner countSpinner = new JSpinner(spinnerModel);

	public EmptyNotificationPanel(EmptyNotification notification, DraggableContainer container)
	{
		super(notification, container);
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		DefaultTypePanel typePanel = new DefaultTypePanel(this, "Empty Space");
		typePanel.addDefaultVisualListener();

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		contentPanel.setOpaque(false);

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

		contentPanel.add(paramsPanel, BorderLayout.SOUTH);

		add(typePanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}
}
