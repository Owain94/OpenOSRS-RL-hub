package spudjb.bankvalue;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemDefinition;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Bank Value",
	description = "Shows the value of your bank in the sidebar",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
@Slf4j
public class BankValuePlugin extends Plugin
{
	@Inject
	Client client;

	@Inject
	ClientToolbar clientToolbar;

	@Inject
	ItemManager itemManager;

	private BankValuePanel panel;
	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception
	{
		panel = new BankValuePanel(this);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(BankValuePlugin.class, "panel_icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Bank Value")
			.priority(5)
			.panel(panel)
			.icon(icon)
			.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.BANK.getId())
		{
			return;
		}

		final List<CachedItem> cachedItems = new ArrayList<>(event.getItemContainer().getItems().length);
		for (Item item : event.getItemContainer().getItems())
		{
			if (itemManager.canonicalize(item.getId()) != item.getId() || item.getId() == -1)
			{
				continue;
			}
			int itemPrice = itemManager.getItemPrice(item.getId());
			ItemDefinition itemDefinition = client.getItemDefinition(item.getId());

			cachedItems.add(new CachedItem(item.getId(), item.getQuantity(), itemDefinition.getName(), itemPrice));
		}


		SwingUtilities.invokeLater(() -> panel.populate(cachedItems));
	}
}
