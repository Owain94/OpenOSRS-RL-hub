package net.runelite.client.plugins.advancednotifications.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import net.runelite.client.plugins.advancednotifications.DraggableContainer;
import net.runelite.client.plugins.advancednotifications.EmptyNotification;
import net.runelite.client.plugins.advancednotifications.InventoryComparator;
import net.runelite.client.ui.ColorScheme;

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
			notification.getComparator().object = (InventoryComparator) comparatorBox.getSelectedItem();
			notification.getPlugin().updateConfig();
			countSpinner.setVisible(notification.getComparator().object.takesParam());
		});

		countSpinner.setValue(notification.getComparatorParam());
		countSpinner.setPreferredSize(new Dimension(64, 20));
		countSpinner.setVisible(notification.getComparator().object.takesParam());
		countSpinner.addChangeListener(e -> {
			notification.setComparatorParam((Integer) countSpinner.getValue());
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