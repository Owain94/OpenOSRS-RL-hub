/*
 * Copyright (c) 2018, TheStonedTurtle <https://github.com/TheStonedTurtle>
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
package net.runelite.client.plugins.lootlogger.ui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.lootlogger.ItemSortTypes;
import net.runelite.client.plugins.lootlogger.LootLoggerConfig;
import net.runelite.client.plugins.lootlogger.UniqueItemPlacement;
import net.runelite.client.plugins.lootlogger.data.LootLog;
import net.runelite.client.plugins.lootlogger.data.UniqueItem;
import net.runelite.client.plugins.lootlogger.localstorage.LTItemEntry;
import net.runelite.client.plugins.lootlogger.localstorage.LTRecord;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;

@Slf4j
class LootPanel extends JPanel
{
	private final LootLog lootLog;
	private final LootLoggerConfig config;
	private final ItemManager itemManager;

	private boolean playbackPlaying = false;
	private boolean cancelPlayback = false;

	LootPanel(
		final LootLog log,
		final LootLoggerConfig config,
		final ItemManager itemManager)
	{
		this.lootLog = log;
		this.config = config;
		this.itemManager = itemManager;

		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(0, 10, 0, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		createPanel(log);
	}

	private void createPanel(final LootLog lootLog)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;

		// Create necessary helpers for the unique toggles
		final Multimap<Integer, UniqueItem> positionMap = ArrayListMultimap.create();
		final Set<Integer> uniqueIds = new HashSet<>();

		// Attach Kill Count Panel(s)
		final int amount = lootLog.getRecords().size();
		String currentText;
		String loggedText;

		switch (lootLog.getName())
		{
			case "Wintertodt":
				currentText = "Current Killcount:";
				loggedText = "Crates Logged:";
				break;
			case "Herbiboar":
				currentText = "Herbiboars Looted:";
				loggedText = "Loots Logged:";
				break;
			case "Brimstone Chest":
			case "Crystal Chest":
			case "Larran's big chest":
			case "Larran's small chest":
			case "Elven Crystal Chest":
				currentText = "Chests Opened:";
				loggedText = "Chests Logged:";
				break;
			case "Clue Scroll (Beginner)":
			case "Clue Scroll (Easy)":
			case "Clue Scroll (Medium)":
			case "Clue Scroll (Hard)":
			case "Clue Scroll (Elite)":
			case "Clue Scroll (Master)":
				currentText = "Clues Completed:";
				loggedText = "Clues Logged:";
				break;
			default:
				currentText = "Current Loot Count:";
				loggedText = "Loots Logged:";
				break;
		}

		this.add(titleLabel("Statistics"), c);
		c.gridy++;

		if (amount > 0)
		{
			final LTRecord entry = lootLog.getRecords().get(amount - 1);
			if (entry.getKillCount() != -1)
			{
				final TextPanel p = new TextPanel(currentText, entry.getKillCount());
				this.add(p, c);
				c.gridy++;
			}
		}

		final TextPanel p2 = new TextPanel(loggedText, amount);
		this.add(p2, c);
		c.gridy++;

		// Only add the total value element if it has something useful to display
		final long totalValue = lootLog.getConsolidated().values().stream().mapToLong(e -> e.getPrice() * e.getQuantity()).sum();
		if (totalValue > 0)
		{
			final TextPanel totalPanel = new TextPanel("Total Value:", totalValue);
			this.add(totalPanel, c);
			c.gridy++;
		}

		if (amount > 0 && totalValue > 0)
		{
			final TextPanel meanPanel = new TextPanel("Mean Value:", totalValue / amount);
			this.add(meanPanel, c);
			c.gridy++;

			long[] values = lootLog.getConsolidated().values().stream()
				.mapToLong(e -> e.getPrice() * e.getQuantity())
				.sorted()
				.toArray();

			final TextPanel medianPanel = new TextPanel("Median Value:", (values.length % 2 != 0) ? values[values.length / 2] : (values[values.length / 2] + values[(values.length / 2) - 1]) / 2);
			this.add(medianPanel, c);
			c.gridy++;
		}

		if (!config.uniquesPlacement().equals(UniqueItemPlacement.ITEM_BREAKDOWN))
		{
			// Loop over all UniqueItems and check how many the player has received as a drop for each
			// Also add all Item IDs for uniques to a Set for easy hiding later on.
			for (final UniqueItem item : lootLog.getUniques())
			{
				final int id = item.getItemID();
				final int linkedId = item.getLinkedID();
				uniqueIds.add(id);
				uniqueIds.add(linkedId);

				final LTItemEntry entry = lootLog.getConsolidated().get(id);
				final LTItemEntry notedEntry = lootLog.getConsolidated().get(linkedId);
				final int qty = (entry == null ? 0 : entry.getQuantity()) + (notedEntry == null ? 0 : notedEntry.getQuantity());
				item.setQty(qty);
				positionMap.put(item.getPosition(), item);
			}

			if (!positionMap.isEmpty())
			{
				this.add(titleLabel("Uniques"), c);
				c.gridy++;
			}

			for (final int position : positionMap.keySet())
			{
				final Collection<UniqueItem> uniques = positionMap.get(position);

				final UniqueItemPanel p = new UniqueItemPanel(uniques, this.itemManager, this.config.itemMissingAlpha());
				this.add(p, c);
				c.gridy++;
			}
		}

		final boolean hideUniques = config.uniquesPlacement().equals(UniqueItemPlacement.UNIQUES_PANEL);
		final Comparator<LTItemEntry> sorter = createLTItemEntryComparator(config.itemSortType());
		final Collection<LTItemEntry> itemsToDisplay = lootLog.getConsolidated().values().stream()
			.filter(e -> !(hideUniques && uniqueIds.contains(e.getId())))
			.sorted(sorter)
			.collect(Collectors.toList());

		if (itemsToDisplay.size() > 0)
		{
			this.add(titleLabel("Drops"), c);
			c.gridy++;

			if (config.itemBreakdown())
			{
				for (final LTItemEntry e : itemsToDisplay)
				{
					final ItemPanel p = new ItemPanel(e, itemManager);
					this.add(p, c);
					c.gridy++;
				}
			}
			else
			{
				final LootGrid grid = new LootGrid(itemsToDisplay.toArray(new LTItemEntry[0]), itemManager);
				this.add(grid, c);
				c.gridy++;
			}
		}
	}

	void addedRecord(final LTRecord record)
	{
		lootLog.addRecord(record);

		// TODO: Smarter update system so it only repaints necessary Item and Text Panels
		this.removeAll();
		this.createPanel(lootLog);

		this.revalidate();
		this.repaint();
	}

	void playback()
	{
		if (playbackPlaying)
		{
			cancelPlayback = true;
			return;
		}

		playbackPlaying = true;

		if (lootLog.getRecords().size() > 0)
		{
			final Collection<LTRecord> recs = new ArrayList<>();
			for (final LTRecord r : lootLog.getRecords())
			{
				recs.add(r);

				SwingUtilities.invokeLater(() -> refreshPlayback(new LootLog(recs, lootLog.getName())));
				try
				{
					if (cancelPlayback)
					{
						playbackPlaying = false;
						cancelPlayback = false;
						SwingUtilities.invokeLater(() -> refreshPlayback(lootLog));
						break;
					}

					// TODO: Allow this rate to be configurable?
					Thread.sleep(250);
				}
				catch (InterruptedException e)
				{
					System.out.println(e.getMessage());
				}
			}
		}

		playbackPlaying = false;
	}

	private void refreshPlayback(final LootLog log)
	{
		this.removeAll();

		this.createPanel(log);

		this.revalidate();
		this.repaint();
	}

	/**
	 * Sorts the collection of LTItemEntry based on the selected {@link ItemSortTypes}
	 *
	 * @param sortType The {@link ItemSortTypes} describing how these entries should be sorted
	 * @return returns the sorted list
	 */
	private static Comparator<LTItemEntry> createLTItemEntryComparator(final ItemSortTypes sortType)
	{
		return (o1, o2) ->
		{
			switch (sortType)
			{
				case ITEM_ID:
					return o1.getId() - o2.getId();
				case PRICE:
					if (o1.getPrice() != o2.getPrice())
					{
						return o1.getPrice() > o2.getPrice() ? -1 : 1;
					}
					break;
				case VALUE:
					if (o1.getTotal() != o2.getTotal())
					{
						return o1.getTotal() > o2.getTotal() ? -1 : 1;
					}
					break;
				case ALPHABETICAL:
					// Handled below
					break;
				default:
					log.warn("Sort Type not being handled correctly, defaulting to alphabetical.");
					break;
			}

			// Default to alphabetical
			return o1.getName().compareTo(o2.getName());
		};
	}

	private static JLabel titleLabel(String text)
	{
		JLabel title = new JShadowedLabel();

		title.setFont(FontManager.getRunescapeSmallFont());
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setText("<html><body style = 'text-align:center'>" + text + "</body></html>");
		title.setBorder(new EmptyBorder(10, 0, 0, 0));

		return title;
	}
}