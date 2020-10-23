/*
 * Copyright (c) 2018, Psikoi <https://github.com/Psikoi>
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
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
package thestonedturtle.runiterocks;

import com.google.common.collect.Ordering;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lombok.Getter;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.http.api.worlds.World;
import thestonedturtle.runiterocks.ui.TableHeader;
import thestonedturtle.runiterocks.ui.TableRow;

/**
 * Modified version of the WorldSwitcherPanel
 */
public class RuniteRocksPanel extends PluginPanel
{
	public static final int WORLD_COLUMN_WIDTH = 55;
	public static final int LOCATION_COLUMN_WIDTH = 45;
	public static final int TIME_COLUMN_WIDTH = 70;
	private static final int HEADER_HEIGHT = 20;

	@Getter
	private final List<TableRow> rows = new ArrayList<>();
	private final JPanel listContainer = new JPanel();
	private final RuniteRocksPlugin plugin;

	private TableHeader worldHeader;
	private TableHeader locationHeader;
	private TableHeader respawnHeader;
	private TableHeader lastVisitHeader;

	private ListOrdering sortOrder = ListOrdering.WORLD;
	private boolean ascendingOrder = false;

	RuniteRocksPanel(RuniteRocksPlugin plugin)
	{
		this.plugin = plugin;

		setBorder(null);
		setLayout(new DynamicGridLayout(0, 1));

		final JPanel headerContainer = buildHeader();

		listContainer.setLayout(new GridLayout(0, 1));

		add(headerContainer);
		add(listContainer);
	}

	void switchCurrentHighlight(int newWorld, int lastWorld)
	{
		for (TableRow row : rows)
		{
			final int rowWorld = row.getWorld().getId();
			if (rowWorld == newWorld)
			{
				row.setCurrent(true);
			}
			else if (rowWorld == lastWorld)
			{
				row.setCurrent(false);
			}
		}

		listContainer.revalidate();
		listContainer.repaint();
	}

	public void updateRuniteRocks(final Collection<RuniteRock> runeRocks)
	{
		for (final RuniteRock runiteRock : runeRocks)
		{
			updateRuniteRock(runiteRock);
		}

		updateList();
	}

	public void updateRuniteRock(@Nullable final RuniteRock runeRock)
	{
		if (runeRock == null)
		{
			return;
		}

		final boolean currentWorld = runeRock.getWorld() == plugin.getTracker().getWorld();
		for (TableRow row : rows)
		{
			if (runeRock.matches(row.getRuniteRock()))
			{
				rows.remove(row);
				break;
			}
		}

		rows.add(buildRow(runeRock.getWorld(), currentWorld, runeRock));
	}

	public void populate()
	{
		rows.clear();

		for (final WorldTracker tracker : plugin.getWorldMap().values())
		{
			final World world = tracker.getWorld();
			final boolean currentWorld = world == plugin.getTracker().getWorld();

			for (final RuniteRock rock : tracker.getRuniteRocks())
			{
				rows.add(buildRow(world, currentWorld, rock));
			}
		}

		updateList();
	}

	public void updateList()
	{
		Ordering<TableRow> ordering = new Ordering<TableRow>()
		{
			@Override
			public int compare(@Nullable TableRow r1, @Nullable TableRow r2)
			{
				// ordering.nullsLast() handles these
				if (r1 == null || r2 == null)
				{
					return 0;
				}

				switch (sortOrder)
				{
					case WORLD:
						return Integer.compare(r1.getRuniteRock().getWorld().getId(), r2.getRuniteRock().getWorld().getId());
					case LOCATION:
						return r1.getRuniteRock().getRock().compareTo(r2.getRuniteRock().getRock());
					case RESPAWN_TIME:
						// Accurate timers should be prioritized, if both times are accurate use normal comparison.
						if (plugin.config.accurateRespawnPriority())
						{
							final boolean r1Accurate = r1.getRuniteRock().hasWitnessedDepletion() || r1.getRuniteRock().isAvailable();
							final boolean r2Accurate = r2.getRuniteRock().hasWitnessedDepletion() || r2.getRuniteRock().isAvailable();
							if (r1Accurate && !r2Accurate)
							{
								return -1;
							}
							else if (!r1Accurate && r2Accurate)
							{
								return 1;
							}
						}

						return r1.getRuniteRock().getRespawnTime().compareTo(r2.getRuniteRock().getRespawnTime());
					case LAST_VISITED:
						return r1.getRuniteRock().getLastSeenAt().compareTo(r2.getRuniteRock().getLastSeenAt());
					default:
						return 0;
				}
			}
		};

		if (!ascendingOrder)
		{
			ordering = ordering.reverse();
		}
		ordering = ordering.nullsLast();

		rows.sort(ordering);
		listContainer.removeAll();

		for (TableRow row : rows)
		{
			if (plugin.config.ignoreInaccurate() && !(row.getRuniteRock().hasWitnessedDepletion() || row.getRuniteRock().isAvailable()))
			{
				continue;
			}
			listContainer.add(row);
			row.refresh();
		}

		listContainer.revalidate();
		listContainer.repaint();
	}

	private void orderBy(final ListOrdering order)
	{
		worldHeader.highlight(order == ListOrdering.WORLD, ascendingOrder);
		locationHeader.highlight(order == ListOrdering.LOCATION, ascendingOrder);
		respawnHeader.highlight(order == ListOrdering.RESPAWN_TIME, ascendingOrder);
		lastVisitHeader.highlight(order == ListOrdering.LAST_VISITED, ascendingOrder);

		this.sortOrder = order;
		updateList();
	}

	/**
	 * Builds the entire table header.
	 */
	private JPanel buildHeader()
	{
		JPanel header = new JPanel(new DynamicGridLayout(0, 2));
		JPanel leftSide = new JPanel(new BorderLayout());
		JPanel rightSide = new JPanel(new BorderLayout());

		worldHeader = new TableHeader("World", sortOrder == ListOrdering.WORLD, ascendingOrder, this::populate, plugin::clearRocks);
		worldHeader.setPreferredSize(new Dimension(WORLD_COLUMN_WIDTH, HEADER_HEIGHT));
		worldHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = sortOrder != ListOrdering.WORLD || !ascendingOrder;
				orderBy(ListOrdering.WORLD);
			}
		});

		locationHeader = new TableHeader("Loc", sortOrder == ListOrdering.LOCATION, ascendingOrder, this::populate, plugin::clearRocks);
		locationHeader.setPreferredSize(new Dimension(LOCATION_COLUMN_WIDTH, HEADER_HEIGHT));
		locationHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = sortOrder != ListOrdering.LOCATION || !ascendingOrder;
				orderBy(ListOrdering.LOCATION);
			}
		});

		respawnHeader = new TableHeader("Respawn", sortOrder == ListOrdering.RESPAWN_TIME, ascendingOrder, this::populate, plugin::clearRocks);
		respawnHeader.setPreferredSize(new Dimension(TIME_COLUMN_WIDTH, HEADER_HEIGHT));
		respawnHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = sortOrder != ListOrdering.RESPAWN_TIME || !ascendingOrder;
				orderBy(ListOrdering.RESPAWN_TIME);
			}
		});

		lastVisitHeader = new TableHeader("Last Visit", sortOrder == ListOrdering.LAST_VISITED, ascendingOrder, this::populate, plugin::clearRocks);
		lastVisitHeader.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isRightMouseButton(mouseEvent))
				{
					return;
				}
				ascendingOrder = sortOrder != ListOrdering.LAST_VISITED || !ascendingOrder;
				orderBy(ListOrdering.LAST_VISITED);
			}
		});

		leftSide.add(worldHeader, BorderLayout.WEST);
		leftSide.add(locationHeader, BorderLayout.CENTER);

		rightSide.add(respawnHeader, BorderLayout.WEST);
		rightSide.add(lastVisitHeader, BorderLayout.CENTER);

		header.add(leftSide);
		header.add(rightSide);

		return header;
	}

	/**
	 * Builds a table row, that displays the world's information.
	 */
	private TableRow buildRow(World world, boolean current, RuniteRock rock)
	{
		TableRow row = new TableRow(world, rock, plugin::hopToWorld, plugin::removeRock, plugin.config.respawnCounter(), plugin.config.visitCounter());
		row.setCurrent(current);

		return row;
	}

	private enum ListOrdering
	{
		WORLD,
		LOCATION,
		RESPAWN_TIME,
		LAST_VISITED
	}
}
