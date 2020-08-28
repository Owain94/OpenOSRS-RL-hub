package net.runelite.client.plugins.loottable.controllers;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.plugins.loottable.LootTablePlugin;
import net.runelite.client.plugins.loottable.helpers.ScrapeWiki;
import net.runelite.client.plugins.loottable.helpers.UiUtilities;
import net.runelite.client.plugins.loottable.views.LootTablePluginPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;

public class LootTableController
{
	private ClientToolbar clientToolbar;
	private LootTablePluginPanel lootTablePluginPanel;
	private NavigationButton navButton;
	private String monsterName;

	final private String LOOT_TABLE_MENU_OPTION = "Loot Table";

	public LootTableController(ClientToolbar clientToolbar)
	{
		this.clientToolbar = clientToolbar;
		Consumer<String> onSearchBarTextChangedListener = text -> onSearchBarTextChanged(text);
		lootTablePluginPanel = new LootTablePluginPanel(
			(ActionEvent event) -> onSearchButtonPressed(event),
			onSearchBarTextChangedListener
		);
		setUpNavigationButton();
		this.monsterName = null;
	}

	/**
	 * Adds "Loot Table" option if "Attack" option is present
	 *
	 * @todo issue with players when "Attack" option is available
	 */
	public void onMenuOpened(MenuOpened event, Client client)
	{
		MenuEntry[] menuEntries = event.getMenuEntries();
		// Look for Attack option
		for (MenuEntry menuEntry : menuEntries)
		{
			if (menuEntry.getOption().equals("Attack"))
			{
				client.insertMenuItem(LOOT_TABLE_MENU_OPTION, monsterName, MenuOpcode.RUNELITE.getId(), menuEntry.getIdentifier(), menuEntry.getParam0(), menuEntry.getParam1(), false);
			}
		}
	}

	/**
	 * menuOptionTarget structured like <col=ffff00>Monk<col=ff00>  (level-2)
	 * We just want Monk to be returned
	 *
	 * @param menuOptionTarget
	 * @return
	 */
	public String parseMenuTarget(String menuOptionTarget)
	{
		return StringUtils.substringBetween(menuOptionTarget, ">", "<");
	}

	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getOption().equals(LOOT_TABLE_MENU_OPTION))
		{
			this.monsterName = parseMenuTarget(event.getTarget());
			Map<String, List<String[]>> allLootTables = ScrapeWiki.scrapeWiki(this.monsterName);
			lootTablePluginPanel.rebuildPanel(this.monsterName, allLootTables);
		}
	}

	public void onSearchButtonPressed(ActionEvent event)
	{
		Map<String, List<String[]>> allLootTables = ScrapeWiki.scrapeWiki(this.monsterName);
		lootTablePluginPanel.rebuildPanel(this.monsterName, allLootTables);
	}

	public void onSearchBarTextChanged(String text)
	{
		this.monsterName = text;
	}

	private void setUpNavigationButton()
	{
		navButton = NavigationButton
			.builder()
			.tooltip("Loot Table")
			.icon(
				ImageUtil.getResourceStreamFromClass(
					LootTablePlugin.class,
					UiUtilities.lootTableNavIcon
				)
			)
			.priority(5)
			.panel(lootTablePluginPanel)
			.build();
		clientToolbar.addNavigation(navButton);
	}
}