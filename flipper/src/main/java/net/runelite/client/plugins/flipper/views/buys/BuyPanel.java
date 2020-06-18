package net.runelite.client.plugins.flipper.views.buys;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.flipper.helpers.UiUtilities;
import net.runelite.client.plugins.flipper.models.Transaction;
import net.runelite.client.plugins.flipper.views.components.AmountProgressBar;
import net.runelite.client.plugins.flipper.views.components.ItemHeader;
import net.runelite.client.ui.ColorScheme;

/**
 * Construct of two main components
 * Item Header (item image and name)
 * Item Information (buy info)
 */
public class BuyPanel extends JPanel
{
	private static final long serialVersionUID = -6576417948629423220L;

	public BuyPanel(Transaction buy, ItemManager itemManager)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		container.setBackground(ColorScheme.DARK_GRAY_COLOR);
		container.add(new ItemHeader(buy, itemManager, true), BorderLayout.NORTH);
		container.add(new AmountProgressBar(buy), BorderLayout.SOUTH);
		container.setBorder(UiUtilities.ITEM_INFO_BORDER);

		this.add(container);
		this.setBorder(new EmptyBorder(0, 0, 3, 0));
	}
}