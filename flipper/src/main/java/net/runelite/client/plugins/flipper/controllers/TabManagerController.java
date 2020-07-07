package net.runelite.client.plugins.flipper.controllers;

import net.runelite.client.plugins.flipper.FlipperPlugin;
import net.runelite.client.plugins.flipper.helpers.UiUtilities;
import net.runelite.client.plugins.flipper.views.TabManager;
import net.runelite.client.plugins.flipper.views.buys.BuysPanel;
import net.runelite.client.plugins.flipper.views.flips.FlipsPanel;
import net.runelite.client.plugins.flipper.views.margins.MarginsPanel;
import net.runelite.client.plugins.flipper.views.sells.SellsPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

public class TabManagerController
{
	private TabManager tabManager;
	private NavigationButton navButton;
	private ClientToolbar clientToolbar;

	public TabManagerController(
		ClientToolbar clientToolbar,
		BuysPanel buysPanel,
		SellsPanel sellsPanel,
		FlipsPanel flipsPanel,
		MarginsPanel marginsPanel
	)
	{
		this.clientToolbar = clientToolbar;
		tabManager = new TabManager(buysPanel, sellsPanel, flipsPanel, marginsPanel);
		setUpNavigationButton();
	}

	private void setUpNavigationButton()
	{
		navButton = NavigationButton
			.builder()
			.tooltip("Flipper")
			.icon(
				ImageUtil.getResourceStreamFromClass(
					FlipperPlugin.class,
					UiUtilities.flipperNavIcon
				)
			)
			.priority(4)
			.panel(tabManager)
			.build();
		clientToolbar.addNavigation(navButton);
	}
}