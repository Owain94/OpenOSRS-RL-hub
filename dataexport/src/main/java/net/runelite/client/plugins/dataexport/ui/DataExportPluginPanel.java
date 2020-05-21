package net.runelite.client.plugins.dataexport.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.dataexport.DataExport;
import net.runelite.client.plugins.dataexport.DataExportConfig;
import net.runelite.client.plugins.dataexport.DataExportPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class DataExportPluginPanel extends PluginPanel
{
	final JPanel wrapperPanel = new JPanel();

	private final ItemManager itemManager;

	private final DataExportPlugin plugin;

	private final DataExportConfig config;

	private final DataExport dataExport;

	private JPanel containerContainer = new JPanel();

	private Map<Tab, DataExportTabPanel> containers = new LinkedHashMap<>();

	public DataExportPluginPanel(ItemManager itemManager, DataExportPlugin plugin, DataExportConfig config, DataExport dataExport)
	{
		super(true);

		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		this.dataExport = dataExport;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(7, 7, 7, 7));

		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
		wrapperPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		containerContainer.setLayout(new GridLayout(0, 1, 0, 8));
		containerContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
		containerContainer.setVisible(true);

		Arrays.asList(Tab.CONTAINER_TABS).forEach(t ->
		{
			DataExportTabPanel p = new DataExportTabPanel(plugin, this, config, dataExport, itemManager, t, t.getName(), t.getFilePrefix(), "Not ready");
			containers.put(t, p);
		});

		containers.forEach((tab, panel) ->
			containerContainer.add(panel));

		wrapperPanel.add(containerContainer);

		this.add(wrapperPanel);

		updateVisibility();
		rebuild();
	}

	public void updateVisibility()
	{
		containerContainer.removeAll();

		log.debug("Containers: {}", containers.values());

		containers.forEach((t, p) ->
		{
			if (p.isVisibility())
			{
				containerContainer.add(p);
			}
		});

		rebuild();
	}

	public void setVisibility(Tab tab, boolean visibility)
	{
		log.debug("Containers: {}", containers.values());

		Map<Tab, DataExportTabPanel> containersTemp = new LinkedHashMap<>();

		containers.forEach((t, p) ->
		{
			if (p.isVisibility() && t.getName().compareTo(p.getTitle()) != 0)
			{
				setVisibility(Tab.ALL_ITEMS, true);
			}
			if (tab.getName().equals(t.getName()))
			{
				DataExportTabPanel panel = containers.get(tab);
				panel.setVisibility(visibility);
				containersTemp.put(t, panel);
			}

			containersTemp.put(t, p);
		});

		containers = containersTemp;
	}

	public void updateTab(String container, String newStatus)
	{
		containers.forEach((tab, panel) ->
		{
			if (panel.getTitle().equals(container))
			{
				panel.updateStatus(newStatus);
			}
			containers.put(tab, panel);
		});

		containers.forEach((tab, panel) ->
			containerContainer.add(panel));

		rebuild();
	}

	public void rebuild()
	{
		revalidate();
		repaint();
	}
}