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
package net.runelite.client.plugins.runiterocks.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.plugins.runiterocks.Rock;
import net.runelite.client.plugins.runiterocks.RuniteRock;
import net.runelite.client.plugins.runiterocks.RuniteRocksPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

/**
 * Modified version of the WorldTableHeader from the WorldHopper plugin
 */
public class TableRow extends JPanel
{
	private static final int WORLD_COLUMN_WIDTH = RuniteRocksPanel.WORLD_COLUMN_WIDTH;
	private static final int LOCATION_COLUMN_WIDTH = RuniteRocksPanel.LOCATION_COLUMN_WIDTH;
	private static final int TIME_COLUMN_WIDTH = RuniteRocksPanel.TIME_COLUMN_WIDTH;

	private static final Color RUNITE_COLOR = new Color(113, 160, 167);
	private static final Color CURRENT_WORLD = new Color(66, 227, 17);
	private static final Color DANGEROUS_WORLD = new Color(251, 62, 62);
	private static final Color MEMBERS_WORLD = new Color(210, 193, 53);
	private static final Color FREE_WORLD = new Color(200, 200, 200);

	private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm:ss a");

	private JLabel worldLabel;
	private JLabel locationLabel;

	private final JLabel respawnLabel = new JLabel();
	private final JLabel lastVisitedLabel = new JLabel();

	@Getter
	private final World world;
	@Getter
	private final RuniteRock runiteRock;
	private final boolean respawnCounter;
	private final boolean visitCounter;

	@Getter(AccessLevel.PACKAGE)
	private int updatedPlayerCount;

	private Color lastBackground;
	private boolean current = false;

	public TableRow(World world, RuniteRock rock, Consumer<World> hopToWorld, BiConsumer<Integer, Rock> removeRock, boolean respawnCounter, boolean visitCounter)
	{
		this.world = world;
		this.runiteRock = rock;
		this.updatedPlayerCount = world.getPlayers();
		this.respawnCounter = respawnCounter;
		this.visitCounter = visitCounter;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(2, 0, 2, 0));
		setForeground(getWorldColor());

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				lastBackground = getBackground();
				setBackground(getBackground().brighter());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setBackground(lastBackground);
			}
		});

		JPanel leftSide = new JPanel(new BorderLayout());
		JPanel rightSide = new JPanel(new BorderLayout());
		leftSide.setOpaque(false);
		rightSide.setOpaque(false);

		JPanel worldField = buildWorldField();
		worldField.setPreferredSize(new Dimension(WORLD_COLUMN_WIDTH, 0));
		worldField.setOpaque(false);

		JPanel locationField = buildLocationField();
		locationField.setPreferredSize(new Dimension(LOCATION_COLUMN_WIDTH, 0));
		locationField.setOpaque(false);

		JPanel respawnField = buildRespawnField();
		respawnField.setPreferredSize(new Dimension(TIME_COLUMN_WIDTH, 0));
		respawnField.setOpaque(false);

		JPanel lastVisitedField = buildLastVisitedField();
		lastVisitedField.setBorder(new EmptyBorder(5, 5, 5, 5));
		lastVisitedField.setOpaque(false);

		leftSide.add(worldField, BorderLayout.WEST);
		leftSide.add(locationField, BorderLayout.CENTER);

		rightSide.add(respawnField, BorderLayout.WEST);
		rightSide.add(lastVisitedField, BorderLayout.CENTER);

		add(leftSide, BorderLayout.WEST);
		add(rightSide, BorderLayout.CENTER);

		final JMenuItem hopTo = new JMenuItem("Hop-to world");
		hopTo.addActionListener(e -> hopToWorld.accept(world));

		final JMenuItem remove = new JMenuItem("Remove entry");
		remove.addActionListener(e -> removeRock.accept(world.getId(), runiteRock.getRock()));

		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
		popupMenu.add(hopTo);
		popupMenu.add(remove);

		setComponentPopupMenu(popupMenu);
	}

	/**
	 * Builds the world list field (containing the the world index)
	 */
	private JPanel buildWorldField()
	{
		final JPanel column = new JPanel(new BorderLayout(7, 0));
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		worldLabel = new JLabel(String.valueOf(world.getId()));
		column.add(worldLabel, BorderLayout.CENTER);

		return column;
	}

	/**
	 * Builds the location list field (containing the location of the rock in-game)
	 */
	private JPanel buildLocationField()
	{
		final JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		locationLabel = new JLabel(runiteRock.getRock().getName());
		locationLabel.setFont(FontManager.getRunescapeSmallFont());
		locationLabel.setToolTipText(runiteRock.getRock().getLocation());

		column.add(locationLabel, BorderLayout.WEST);

		return column;
	}

	/**
	 * Builds the respawn list field (shows the time at which the rock would or should have respawned)
	 */
	private JPanel buildRespawnField()
	{
		final JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		respawnLabel.setFont(FontManager.getRunescapeSmallFont());
		updateRespawnLabel();

		column.add(respawnLabel, BorderLayout.WEST);

		return column;
	}

	private void updateRespawnLabel()
	{
		if (runiteRock.isAvailable())
		{
			respawnLabel.setText("Available");
			respawnLabel.setForeground(RUNITE_COLOR);
			return;
		}

		final Instant respawn = runiteRock.getRespawnTime();
		if (respawnCounter)
		{
			final Duration seconds = Duration.between(respawn, Instant.now());
			if (!seconds.isNegative())
			{
				respawnLabel.setText("Available");
				respawnLabel.setForeground(ColorScheme.BRAND_BLUE);
				return;
			}

			final String timer = "-" + getReadableTimeElapsed(seconds).trim();
			respawnLabel.setText(timer);
			respawnLabel.setForeground(Color.LIGHT_GRAY);
		}
		else
		{
			respawnLabel.setText(TIME_FORMATTER.format(Date.from(respawn)));
			respawnLabel.setForeground(ColorScheme.DARK_GRAY_COLOR);
		}

		if (!runiteRock.hasWitnessedDepletion())
		{
			respawnLabel.setForeground(ColorScheme.BRAND_BLUE);
		}
	}

	/**
	 * Builds the last visited list field (shows the time at which the rock was last updated).
	 */
	private JPanel buildLastVisitedField()
	{
		final JPanel column = new JPanel(new BorderLayout());
		column.setBorder(new EmptyBorder(0, 5, 0, 5));

		lastVisitedLabel.setFont(FontManager.getRunescapeSmallFont());
		updateLastVisitedLabel();

		column.add(lastVisitedLabel, BorderLayout.WEST);

		return column;
	}

	private void updateLastVisitedLabel()
	{
		final Instant time = runiteRock.getLastSeenAt();
		String text;
		if (time == null)
		{
			text = "Unknown";
		}
		else if (visitCounter)
		{
			final Duration seconds = Duration.between(time, Instant.now());
			text = seconds.isNegative() ? "-" : "";
			text += getReadableTimeElapsed(seconds).trim();
		}
		else
		{
			text = TIME_FORMATTER.format(Date.from(time));
		}

		lastVisitedLabel.setText(text);
	}

	public void setCurrent(final boolean current)
	{
		this.current = current;
		final Color foreground = getWorldColor();
		worldLabel.setForeground(foreground);
		locationLabel.setForeground(foreground);
	}

	public void refresh()
	{
		updateRespawnLabel();
		updateLastVisitedLabel();

		revalidate();
		repaint();
	}

	private Color getWorldColor()
	{
		return current ? CURRENT_WORLD : getWorldColor(world);
	}

	private static Color getWorldColor(final World world)
	{
		final EnumSet<WorldType> types = world.getTypes();
		if (types.contains(WorldType.PVP) || types.contains(WorldType.HIGH_RISK) || types.contains(WorldType.DEADMAN))
		{
			return DANGEROUS_WORLD;
		}

		return types.contains(WorldType.MEMBERS) ? MEMBERS_WORLD : FREE_WORLD;
	}

	private static String getReadableTimeElapsed(final Duration duration)
	{
		final double seconds = Math.abs(duration.getSeconds());
		if (seconds <= 60)
		{
			return String.format("%2.0f", seconds) + "s";
		}

		final double s = seconds % 3600 % 60;
		final double m = Math.floor(seconds % 3600 / 60);
		final double h = Math.floor(seconds / 3600);

		return h < 1 ? String.format("%2.0f:%02.0f", m, s) : String.format("%2.0f:%02.0f:%02.0f", h, m, s);
	}
}