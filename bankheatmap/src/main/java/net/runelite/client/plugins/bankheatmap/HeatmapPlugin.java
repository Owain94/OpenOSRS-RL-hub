package net.runelite.client.plugins.bankheatmap;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Varbits;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Bank Heatmap",
	description = "Shows various heatmaps in your bank",
	type = PluginType.UTILITY,
	enabledByDefault = false
)
@Slf4j
public class HeatmapPlugin extends Plugin
{
	enum HEATMAP_MODE
	{
		NULL,
		HA,
		GE
	}

	private static final List<Varbits> TAB_VARBITS = List.of(
		Varbits.BANK_TAB_ONE_COUNT,
		Varbits.BANK_TAB_TWO_COUNT,
		Varbits.BANK_TAB_THREE_COUNT,
		Varbits.BANK_TAB_FOUR_COUNT,
		Varbits.BANK_TAB_FIVE_COUNT,
		Varbits.BANK_TAB_SIX_COUNT,
		Varbits.BANK_TAB_SEVEN_COUNT,
		Varbits.BANK_TAB_EIGHT_COUNT,
		Varbits.BANK_TAB_NINE_COUNT
	);

	private static final int BANK_MENU_WIDGET_ID = (WidgetID.BANK_GROUP_ID << 16) + 106;

	@Inject
	private Client client;

	@Inject
	private HeatmapCalculation heatmapCalculation;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private HeatmapItemOverlay heatmapItemOverlay;

	@Getter
	private HEATMAP_MODE heatmapMode = HEATMAP_MODE.NULL;

	@Override
	protected void startUp()
	{
		overlayManager.add(heatmapItemOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(heatmapItemOverlay);
		heatmapMode = HEATMAP_MODE.NULL;
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if ("setBankTitle".equals(event.getEventName()))
		{
			Item[] items = getBankTabItems();
			heatmapItemOverlay.getHeatmapImages().invalidateAll();
			if (items != null)
			{
				heatmapCalculation.calculate(items);
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getOpcode() != MenuOpcode.CC_OP.getId() || !event.getOption().equals("Show menu")
			|| event.getParam1() != BANK_MENU_WIDGET_ID)
		{
			return;
		}

		MenuEntry[] entries = client.getMenuEntries();
		entries = Arrays.copyOf(entries, entries.length + 2);

		MenuEntry geHeatmap = new MenuEntry();
		geHeatmap.setOption("Toggle GE Heatmap");
		geHeatmap.setTarget("");
		geHeatmap.setOpcode(MenuOpcode.WIDGET_FOURTH_OPTION.getId() + 2000);
		geHeatmap.setIdentifier(event.getIdentifier());
		geHeatmap.setParam0(event.getParam0());
		geHeatmap.setParam1(event.getParam1());

		MenuEntry haHeatmap = new MenuEntry();
		haHeatmap.setOption("Toggle HA Heatmap");
		haHeatmap.setTarget("");
		haHeatmap.setOpcode(MenuOpcode.WIDGET_FIFTH_OPTION.getId() + 2000);
		haHeatmap.setIdentifier(event.getIdentifier());
		haHeatmap.setParam0(event.getParam0());
		haHeatmap.setParam1(event.getParam1());

		entries[entries.length - 2] = haHeatmap;
		entries[entries.length - 1] = geHeatmap;

		client.setMenuEntries(entries);
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if ((event.getMenuOpcode() != MenuOpcode.WIDGET_FOURTH_OPTION && event.getMenuOpcode() != MenuOpcode.WIDGET_FIFTH_OPTION)
			|| !event.getOption().startsWith("Toggle"))
		{
			return;
		}

		HEATMAP_MODE mode = event.getOption().equals("Toggle GE Heatmap") ? HEATMAP_MODE.GE : HEATMAP_MODE.HA;
		if (mode == heatmapMode)
		{
			heatmapMode = HEATMAP_MODE.NULL;
		}
		else
		{
			heatmapItemOverlay.getHeatmapImages().invalidateAll();
			heatmapMode = mode;
		}
	}

	private Item[] getBankTabItems()
	{
		final ItemContainer container = client.getItemContainer(InventoryID.BANK);
		if (container == null)
		{
			return null;
		}

		final Item[] items = container.getItems();
		int currentTab = client.getVar(Varbits.CURRENT_BANK_TAB);

		if (currentTab > 0)
		{
			int startIndex = 0;

			for (int i = currentTab - 1; i > 0; i--)
			{
				startIndex += client.getVar(TAB_VARBITS.get(i - 1));
			}

			int itemCount = client.getVar(TAB_VARBITS.get(currentTab - 1));
			return Arrays.copyOfRange(items, startIndex, startIndex + itemCount);
		}

		return items;
	}

	HeatmapItem getHeatmapItem(int id)
	{
		return heatmapCalculation.getHeatmapItems().get(id);
	}
}