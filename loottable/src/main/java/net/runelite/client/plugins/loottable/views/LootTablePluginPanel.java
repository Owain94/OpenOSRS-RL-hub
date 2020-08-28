package net.runelite.client.plugins.loottable.views;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import net.runelite.client.plugins.loottable.views.components.Header;
import net.runelite.client.plugins.loottable.views.components.LootTablePanel;
import net.runelite.client.ui.PluginPanel;

public class LootTablePluginPanel extends PluginPanel
{
	private static final long serialVersionUID = 5758361368464139958L;

	private final ActionListener onSearchButtonPressed;
	private final Consumer<String> onSearchBarTextChanged;

	public LootTablePluginPanel(ActionListener onSearchButtonPressed, Consumer<String> onSearchBarTextChanged)
	{
		this.onSearchButtonPressed = onSearchButtonPressed;
		this.onSearchBarTextChanged = onSearchBarTextChanged;
		Header header = new Header("", onSearchButtonPressed, onSearchBarTextChanged);
		add(header);
	}

	public void rebuildPanel(String monsterName, Map<String, List<String[]>> allLootTable)
	{
		SwingUtilities.invokeLater(() -> {
			this.removeAll();
			Header header = new Header(monsterName, onSearchButtonPressed, onSearchBarTextChanged);
			LootTablePanel lootTablePanel = new LootTablePanel(allLootTable);
			add(header);
			add(lootTablePanel, BorderLayout.WEST);
		});
	}
}