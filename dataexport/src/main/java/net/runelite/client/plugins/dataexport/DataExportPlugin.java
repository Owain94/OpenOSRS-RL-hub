package net.runelite.client.plugins.dataexport;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemDefinition;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.dataexport.localstorage.DataWriter;
import net.runelite.client.plugins.dataexport.ui.DataExportPluginPanel;
import net.runelite.client.plugins.dataexport.ui.Tab;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Data Export",
	description = "Exports your bank to the clipboard, so you can paste all items as csv (useful for spreadsheets)",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class DataExportPlugin extends Plugin
{
	private static final Set<Integer> CONTAINERS = Set.of(InventoryID.BANK.getId(), InventoryID.SEED_VAULT.getId(), InventoryID.INVENTORY.getId(), InventoryID.EQUIPMENT.getId());

	public DataWriter dataWriter;

	public DataExport dataExport;

	public Map<Tab, Boolean> visibilityMap = new LinkedHashMap<>();

	int hashAllItems = -1;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SkillIconManager skillIconManager;

	@Inject
	private DataExportConfig config;

	@Inject
	private KeyManager keyManager;

	private DataExportPluginPanel panel;

	private NavigationButton navButton;

	private int hashBank = -1;

	private int hashSeedVault = -1;

	private int hashInventory = -1;

	private int hashEquipment = -1;

	//private static final Logger logger = LoggerFactory.getLogger(DataExportPlugin.class);

	private int hashSkills = -1;

	private int lastTick = -1;

	@Provides
	DataExportConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DataExportConfig.class);
	}

	@Override
	protected void startUp()
	{
		dataExport = new DataExport(client, config, itemManager, this);
		dataWriter = new DataWriter(config);

		Arrays.asList(Tab.CONTAINER_TABS).forEach(t ->
		{
			visibilityMap.put(t, true);
		});

		this.panel = new DataExportPluginPanel(itemManager, this, config, dataExport);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "data_export_icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Data Exporter")
			.icon(icon)
			.priority(6)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		clientThread.invokeLater(() ->
		{
			switch (client.getGameState())
			{
				case STARTING:
				case UNKNOWN:
					return false;
			}

			SwingUtilities.invokeLater(() ->
			{
				panel.rebuild();
			});

			return true;
		});
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);

	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("dataexport"))
		{
			return;
		}

		Map<Integer, DataExportItem> mapBlank = new HashMap<>();
		if (!config.includeBank())
		{
			dataExport.setMapBank(mapBlank);
			visibilityMap.put(Tab.BANK, false);
		}
		else
		{
			visibilityMap.put(Tab.BANK, true);
		}

		if (!config.includeSeedVault())
		{
			dataExport.setMapSeedVault(mapBlank);
			visibilityMap.put(Tab.SEED_VAULT, false);
		}
		else
		{
			visibilityMap.put(Tab.SEED_VAULT, true);
		}

		if (!config.includeInventory())
		{
			dataExport.setMapInventory(mapBlank);
			visibilityMap.put(Tab.INVENTORY, false);
		}
		else
		{
			visibilityMap.put(Tab.INVENTORY, true);
		}

		if (!config.includeEquipment())
		{
			dataExport.setMapEquipment(mapBlank);
			visibilityMap.put(Tab.EQUIPMENT, false);
		}
		else
		{
			visibilityMap.put(Tab.EQUIPMENT, true);
		}

		visibilityMap.forEach((t, v) ->
		{
			panel.setVisibility(t, v);
		});

		panel.updateVisibility();
		panel.rebuild();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int tick = client.getTickCount();
		if (tick == lastTick)
		{
			return;
		}
		lastTick = tick;

		ItemContainer itemContainer = event.getItemContainer();
		int itemContainerId = event.getContainerId();

		if (itemContainer == null)
		{
			return;
		}

		if (!CONTAINERS.contains(itemContainerId))
		{
			return;
		}

		final Item[] widgetItems = itemContainer.getItems();
		if (widgetItems == null || widgetItems.length == 0)
		{
			return;
		}

		int hash = hashItems(widgetItems);
		//log.info("New hash: " + hash);

		Map<Integer, DataExportItem> mapContainer = new LinkedHashMap<>();

		for (Item widgetItem : widgetItems)
		{
			ItemDefinition itemComposition = itemManager.getItemDefinition(widgetItem.getId());

			String name = itemComposition.getName();
			int quantity = widgetItem.getQuantity();
			int id = widgetItem.getId();

			if (itemComposition.getPlaceholderTemplateId() != -1)
			{
				quantity = 0;
			}

			if (name != null && quantity > 0 && id != -1)
			{
				DataExportItem item = new DataExportItem(name, quantity, id);
				mapContainer.putIfAbsent(id, item);
				dataExport.addItemAll(id, item);
			}
		}

		if (mapContainer.size() < 2)
		{
			return;
		}

		if (itemContainerId == InventoryID.BANK.getId() && config.includeBank() && hash != hashBank)
		{
			log.debug("Bank hash: " + hashBank + "   ->   " + hash);
			hashBank = hash;
			updateBankData(mapContainer);
		}
		else if (itemContainerId == InventoryID.SEED_VAULT.getId() && config.includeSeedVault() && hash != hashSeedVault)
		{
			log.debug("Seed vault hash: " + hashSeedVault + "   ->   " + hash);
			hashSeedVault = hash;
			updateSeedVaultData(mapContainer);
		}
		else if (itemContainerId == InventoryID.INVENTORY.getId() && config.includeInventory() && hash != hashInventory)
		{
			log.debug("Inventory hash: " + hashInventory + "   ->   " + hash);
			hashInventory = hash;
			updateInventoryData(mapContainer);
		}
		else if (itemContainerId == InventoryID.EQUIPMENT.getId() && config.includeEquipment() && hash != hashEquipment)
		{
			log.debug("Equipment hash: " + hashEquipment + "   ->   " + hash);
			hashEquipment = hash;
			updateEquipmentData(mapContainer);
		}
	}

	private void updateBankData(Map<Integer, DataExportItem> map)
	{
		dataExport.setMapBank(map);
		dataWriter.writeJSON("container_bank", map);
		log.debug("Bank Container Map: {}", map);

		if (map.size() > 1)
		{
			panel.updateTab("Bank", "Ready");
		}
		else
		{
			panel.updateTab("Bank", "Visit a bank!");
		}
	}

	private void updateSeedVaultData(Map<Integer, DataExportItem> map)
	{
		dataExport.setMapSeedVault(map);
		dataWriter.writeJSON("container_seed_vault", map);
		log.debug("Seed Vault Container Map: {}", map);

		if (map.size() > 1)
		{
			panel.updateTab("Seed Vault", "Ready");
		}
		else
		{
			panel.updateTab("Seed Vault", "Inventory empty");
		}
	}

	private void updateInventoryData(Map<Integer, DataExportItem> map)
	{
		dataExport.setMapInventory(map);
		dataWriter.writeJSON("container_inventory", map);
		log.debug("Inventory Container Map: {}", map);

		if (map.size() > 1)
		{
			panel.updateTab("Inventory", "Ready");
		}
		else
		{
			panel.updateTab("Inventory", "No items equipped");
		}
	}

	private void updateEquipmentData(Map<Integer, DataExportItem> map)
	{
		dataExport.setMapEquipment(map);
		dataWriter.writeJSON("container_equipment", map);
		log.debug("Equipment Container Map: {}", map);

		if (map.size() > 1)
		{
			panel.updateTab("Equipment", "Ready");
		}
		else
		{
			panel.updateTab("Equipment", "No items equipped");
		}
	}

	public void exportContainer(String container)
	{
		if (container.equals("container_all_items"))
		{
			dataExport.exportContainer(container);
		}
		else if (container.equals("container_bank"))
		{
			dataExport.exportContainer(container);
		}
		else if (container.equals("container_seed_vault"))
		{
			dataExport.exportContainer(container);
		}
		else if (container.equals("container_inventory"))
		{
			dataExport.exportContainer(container);
		}
		else if (container.equals("container_equipment"))
		{
			dataExport.exportContainer(container);
		}
	}

	public void downloadContainer(String container)
	{
		if (container.equals("container_all_items"))
		{
			dataWriter.writeFile(container, dataExport.getMapItems());
		}
		else if (container.equals("container_bank"))
		{
			dataWriter.writeFile(container, dataExport.getMapBank());
		}
		else if (container.equals("container_seed_vault"))
		{
			dataWriter.writeFile(container, dataExport.getMapSeedVault());
		}
		else if (container.equals("container_inventory"))
		{
			dataWriter.writeFile(container, dataExport.getMapInventory());
		}
		else if (container.equals("container_equipment"))
		{
			dataWriter.writeFile(container, dataExport.getMapEquipment());
		}
	}

	private int hashItems(final Item[] items)
	{
		final Map<Integer, Integer> mapCheck = new HashMap<>(items.length);
		for (Item item : items)
		{
			mapCheck.put(item.getId(), item.getQuantity());
		}

		return mapCheck.hashCode();
	}
}