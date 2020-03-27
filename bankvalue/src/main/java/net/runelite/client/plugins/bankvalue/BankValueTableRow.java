/*
 * Copyright (c) 2018, Psikoi <https://github.com/Psikoi>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.bankvalue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.QuantityFormatter;

class BankValueTableRow extends JPanel
{
	static final int ITEM_COUNT_COLUMN_WIDTH = 45;
	static final int ITEM_VALUE_COLUMN_WIDTH = 45;

	@Getter
	private final CachedItem item;

	private Color lastBackground;

	BankValueTableRow(CachedItem item)
	{
		this.item = item;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(2, 0, 2, 0));

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{

				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setBackground(getBackground().brighter());
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() == 2)
				{
					setBackground(getBackground().darker());
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				BankValueTableRow.this.lastBackground = getBackground();
				setBackground(getBackground().brighter());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setBackground(lastBackground);
			}
		});

//		final JPopupMenu popupMenu = new JPopupMenu();
//		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
//		popupMenu.add(favoriteMenuOption);
//
//		setComponentPopupMenu(popupMenu);

		JPanel leftSide = new JPanel(new BorderLayout());
		JPanel rightSide = new JPanel(new BorderLayout());
		leftSide.setOpaque(false);
		rightSide.setOpaque(false);

		JPanel itemNameField = buildItemNameField();
//		itemNameField.setPreferredSize(new Dimension(ITEM_NAME_COLUMN_WIDTH, 0));
		itemNameField.setOpaque(false);

		JPanel itemCountField = buildItemCountField();
		itemCountField.setPreferredSize(new Dimension(ITEM_COUNT_COLUMN_WIDTH, 0));
		itemCountField.setOpaque(false);

		JPanel valueField = buildValueField();
		valueField.setPreferredSize(new Dimension(ITEM_VALUE_COLUMN_WIDTH, 0));
		valueField.setOpaque(false);

		recolour();

		leftSide.add(itemNameField, BorderLayout.CENTER);
		leftSide.add(itemCountField, BorderLayout.EAST);
		rightSide.add(valueField, BorderLayout.CENTER);

		add(leftSide, BorderLayout.CENTER);
		add(rightSide, BorderLayout.EAST);
	}

	public void recolour()
	{
	}

	/**
	 * Builds the players list field (containing the amount of players logged in that world).
	 */
	private JPanel buildItemCountField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		JLabel itemCount = new JLabel(QuantityFormatter.quantityToStackSize(getItemCount()));
		itemCount.setFont(FontManager.getRunescapeSmallFont());

		column.add(itemCount, BorderLayout.WEST);

		return column;
	}

	int getItemCount()
	{
		return item.getQuantity();
	}

	private JPanel buildValueField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		JLabel value = new JLabel(QuantityFormatter.quantityToStackSize(getPrice()));
		value.setFont(FontManager.getRunescapeSmallFont());

		column.add(value, BorderLayout.EAST);

		return column;
	}

	int getPrice()
	{
		return item.getValue() * item.getQuantity();
	}

	/**
	 * Builds the activity list field (containing that world's activity/theme).
	 */
	private JPanel buildItemNameField()
	{
		JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		JLabel itemName = new JLabel(getItemName());
		itemName.setFont(FontManager.getRunescapeSmallFont());

		column.add(itemName, BorderLayout.WEST);

		return column;
	}

	String getItemName()
	{
		return item.getName();
	}

}