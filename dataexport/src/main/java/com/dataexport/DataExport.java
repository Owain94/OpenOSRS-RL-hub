package com.dataexport;

import com.dataexport.ui.DataExportPluginPanel;
import com.google.gson.Gson;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

@Slf4j
public class DataExport
{
	private final Client client;

	private final DataExportConfig config;

	private final ItemManager itemManager;

	int hashAllItems;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapBank;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapSeedVault;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapInventory;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapEquipment;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapSkills;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapItems;

	@Getter
	@Setter
	private ArrayList<Integer> arrayListItems;

	private DataExportPluginPanel panel;

	private DataExportPlugin plugin;

	public DataExport(Client client, DataExportConfig config, ItemManager itemManager, DataExportPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.itemManager = itemManager;
		this.plugin = plugin;

		hashAllItems = -1;

		mapBank = new LinkedHashMap<>();
		mapSeedVault = new LinkedHashMap<>();
		mapInventory = new LinkedHashMap<>();
		mapEquipment = new LinkedHashMap<>();
		mapItems = new LinkedHashMap<>();
		arrayListItems = new ArrayList<>();
	}

	public void exportContainer(String container)
	{
		Map<Integer, DataExportItem> map;

		if (container.equals("container_all_items"))
		{
			map = mapItems;
		}
		else if (container.equals("container_bank"))
		{
			map = mapBank;
		}
		else if (container.equals("container_seed_vault"))
		{
			map = mapSeedVault;
		}
		else if (container.equals("container_inventory"))
		{
			map = mapInventory;
		}
		else if (container.equals("container_equipment"))
		{
			map = mapEquipment;
		}
		else if (container.equals("container_all_items"))
		{
			map = mapItems;
		}
		else
		{
			map = new LinkedHashMap<>();
		}

		if (map == null)
		{
			return;
		}

		SwingUtilities.invokeLater(() ->
		{
			final Gson gson = new Gson();
			final String json = gson.toJson(map.values());
			final StringSelection contents = new StringSelection(json);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
			JOptionPane.showMessageDialog(panel,
				"Container data was copied to clipboard.",
				"Export Setup Succeeded",
				JOptionPane.PLAIN_MESSAGE);
		});
	}

	public void rebuildSkillArrayList()
	{

	}

	private int getTotalQuantityForItem(int id)
	{
		int total = 0;

		if (mapBank.containsKey(id) && mapBank.size() > 1)
		{
			total += mapBank.get(id).getQuantity();
		}
		if (mapSeedVault.containsKey(id) && mapSeedVault.size() > 1)
		{
			total += mapSeedVault.get(id).getQuantity();
		}
		if (mapInventory.containsKey(id) && mapInventory.size() > 1)
		{
			total += mapInventory.get(id).getQuantity();
		}
		if (mapEquipment.containsKey(id) && mapEquipment.size() > 1)
		{
			total += mapEquipment.get(id).getQuantity();
		}

		return total;
	}

	public void addItemAll(int id, DataExportItem item)
	{
		DataExportItem item2 = mapItems.get(id);

		if (item2 != null)
		{
			return;
		}

		log.info("Adding to list: " + item.getName());
		mapItems.put(id, item);
	}
}
