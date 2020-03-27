package net.runelite.client.plugins.advancednotifications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import joptsimple.internal.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.advancednotifications.ui.AdvancedNotificationsPluginPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Advanced Notifications",
	tags = {"notifications", "inventory", "item"},
	description = "An advanced notifications system",
	type = PluginType.UTILITY
)
@Slf4j
public class AdvancedNotificationsPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "advancednotifications";
	private static final String CONFIG_KEY = "notifications";
	private static final String ICON_FILE = "panel_icon.png";
	private static final String PLUGIN_NAME = "Advanced Notifications";

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private Client client;

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private ItemManager itemManager;

	@Inject
	@Getter(AccessLevel.PACKAGE)
	private Notifier notifier;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	private AdvancedNotificationsPluginPanel pluginPanel;
	private NavigationButton navigationButton;
	private Item[] previousItems;

	@Getter
	private List<Notification> notifications;

	@Override
	protected void startUp()
	{
		notifications = new ArrayList<>();
		previousItems = null;

		loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY));

		pluginPanel = new AdvancedNotificationsPluginPanel(this);
		pluginPanel.rebuild();

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), ICON_FILE);

		navigationButton = NavigationButton.builder()
			.tooltip(PLUGIN_NAME)
			.icon(icon)
			.priority(5)
			.panel(pluginPanel)
			.build();

		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navigationButton);

		notifications = null;
		pluginPanel = null;
		navigationButton = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (notifications.isEmpty() && event.getGroup().equals(CONFIG_GROUP) && event.getKey().equals(CONFIG_KEY))
		{
			loadConfig(event.getNewValue());
		}
	}

	private void loadConfig(String json)
	{
		if (Strings.isNullOrEmpty(json))
		{
			notifications = new ArrayList<>();
			return;
		}

		Gson gson = new GsonBuilder()
			.registerTypeHierarchyAdapter(Notification.class, new NotificationAdapter(this))
			.create();

		notifications = gson.fromJson(json, new TypeToken<ArrayList<Notification>>()
		{
		}.getType());
	}

	private void notify(Object event)
	{
		for (Notification n : notifications)
		{
			n.tryNotify(event);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
		{
			Item[] items = event.getItemContainer().getItems();
			if (previousItems == null)
			{
				previousItems = items;
				return;
			}

			Set<Integer> uniqueItems = new HashSet<>();
			addUniqueItems(uniqueItems, items);
			addUniqueItems(uniqueItems, previousItems);

			for (int id : uniqueItems)
			{
				notify(new InventoryEvent(id, countItems(items, id), countItems(previousItems, id)));
			}

			previousItems = items;
		}
	}

	private void addUniqueItems(Set<Integer> set, Item[] items)
	{
		for (Item i : items)
		{
			set.add(i.getId());
		}
	}

	private int countItems(Item[] items, int id)
	{
		int c = 0;
		for (Item i : items)
		{
			if (i.getId() == id)
			{
				c += Math.max(i.getQuantity(), 1);
			}
		}
		return c;
	}

	public void updateConfig()
	{
		if (notifications.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
			return;
		}

		final Gson gson = new GsonBuilder()
			.registerTypeHierarchyAdapter(Notification.class, new NotificationAdapter(this))
			.create();
		final String json = gson.toJson(notifications);
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
	}

	public void rebuildPluginPanel()
	{
		pluginPanel.rebuild();
	}
}