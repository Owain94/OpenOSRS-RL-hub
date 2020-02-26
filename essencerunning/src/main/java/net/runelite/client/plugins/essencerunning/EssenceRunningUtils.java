package net.runelite.client.plugins.essencerunning;

import com.google.common.collect.ArrayListMultimap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuShouldLeftClick;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;

public class EssenceRunningUtils
{
	private static final String TRADING_WITH = "Trading with:<br>";

	public static void swapPrevious(final Client client,
									final ArrayListMultimap<String, Integer> optionIndexes,
									final String optionA, // the desired option
									final String target,
									final int index)
	{ // the index of examine

		if (index > 0)
		{
			// examine is always the last option for an item and the one before it is the default displayed option
			final MenuEntry previousEntry = client.getMenuEntries()[index - 1];
			final String previousOption = Text.removeTags(previousEntry.getOption()).toLowerCase();
			final String previousTarget = Text.removeTags(previousEntry.getTarget()).toLowerCase();

			if (target.equals(previousTarget) && !optionA.equals(previousOption))
			{
				swap(client, optionIndexes, optionA, previousOption, target, index - 1, true);
			}
		}
	}

	public static void swap(final Client client,
							final ArrayListMultimap<String, Integer> optionIndexes,
							final String optionA,
							final String optionB,
							final String target,
							final int index,
							final boolean strict)
	{

		final MenuEntry[] menuEntries = client.getMenuEntries();
		final int thisIndex = findIndex(optionIndexes, menuEntries, index, optionB, target, strict);
		final int optionIdx = findIndex(optionIndexes, menuEntries, thisIndex, optionA, target, strict);

		if (thisIndex >= 0 && optionIdx >= 0)
		{
			swap(client, optionIndexes, menuEntries, optionIdx, thisIndex);
		}
	}

	private static int findIndex(final ArrayListMultimap<String, Integer> optionIndexes,
								final MenuEntry[] entries,
								final int limit,
								final String option,
								final String target,
								final boolean strict)
	{

		if (strict)
		{
			List<Integer> indexes = optionIndexes.get(option);

			// We want the last index which matches the target, as that is what is top-most on the menu
			for (int i = indexes.size() - 1; i >= 0; --i)
			{
				final int idx = indexes.get(i);
				MenuEntry entry = entries[idx];
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				// Limit to the last index which is prior to the current entry
				if (idx <= limit && entryTarget.equals(target))
				{
					return idx;
				}
			}
		}
		else
		{
			// Without strict matching we have to iterate all entries up to the current limit...
			for (int i = limit; i >= 0; i--)
			{
				final MenuEntry entry = entries[i];
				final String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
				final String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
				{
					return i;
				}
			}

		}

		return -1;
	}

	private static void swap(final Client client,
							final ArrayListMultimap<String, Integer> optionIndexes,
							final MenuEntry[] entries,
							final int index1,
							final int index2)
	{

		final MenuEntry entry = entries[index1];
		entries[index1] = entries[index2];
		entries[index2] = entry;

		client.setMenuEntries(entries);

		// Rebuild option indexes
		optionIndexes.clear();
		int idx = 0;
		for (MenuEntry menuEntry : entries)
		{
			final String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}
	}

	public static void swapBankWithdrawOp(final Client client, final MenuEntryAdded menuEntryAdded)
	{

		final String target = Text.removeTags(menuEntryAdded.getTarget()).toLowerCase();
		final EssenceRunningItem item = EssenceRunningItem.of(target);

		// Withdraw- op 1 is the current withdraw amount 1/5/10/x
		if (item != null && menuEntryAdded.getOpcode() == MenuOpcode.CC_OP.getId() && menuEntryAdded.getIdentifier() == 1
			&& menuEntryAdded.getOption().startsWith("Withdraw-"))
		{

			final MenuEntry[] menuEntries = client.getMenuEntries();
			final String withdrawQuantity = "Withdraw-" + item.getWithdrawQuantity();

			// Find the custom withdraw quantity option
			for (int i = menuEntries.length - 1; i >= 0; --i)
			{
				final MenuEntry entry = menuEntries[i];

				if (entry.getOption().equals(withdrawQuantity))
				{
					menuEntries[i] = menuEntries[menuEntries.length - 1];
					menuEntries[menuEntries.length - 1] = entry;

					client.setMenuEntries(menuEntries);
					break;
				}
			}
		}
	}

	public static void swapBankOp(final Client client, final MenuEntryAdded menuEntryAdded)
	{

		// Deposit- op 2 is the current deposit amount 1/5/10/x
		if (menuEntryAdded.getOpcode() == MenuOpcode.CC_OP.getId() && menuEntryAdded.getIdentifier() == 2
			&& menuEntryAdded.getOption().startsWith("Deposit-"))
		{

			final MenuEntry[] menuEntries = client.getMenuEntries();

			// Find the extra menu option; they don't have fixed names, so check based on the menu identifier
			for (int i = menuEntries.length - 1; i >= 0; --i)
			{
				final MenuEntry entry = menuEntries[i];

				// The extra options are always option 9
				if (entry.getOpcode() == MenuOpcode.CC_OP_LOW_PRIORITY.getId() && entry.getIdentifier() == 9
					&& !entry.getOption().equals("Empty"))
				{ // exclude Runecraft pouch's "Empty" option

					// we must also raise the priority of the op so it doesn't get sorted later
					entry.setOpcode(MenuOpcode.CC_OP.getId());

					menuEntries[i] = menuEntries[menuEntries.length - 1];
					menuEntries[menuEntries.length - 1] = entry;

					client.setMenuEntries(menuEntries);
					break;
				}
			}
		}
	}

	public static boolean itemEquipped(final Client client, final EquipmentInventorySlot slot)
	{
		final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment != null)
		{
			final Item[] item = equipment.getItems();
			return item.length > slot.getSlotIdx()
				&& item[slot.getSlotIdx()] != null
				&& item[slot.getSlotIdx()].getId() > -1;
		}
		return false;
	}

	public static void computeItemsTraded(final Client client, final EssenceRunningSession session)
	{
		final Widget tradingWith = client.getWidget(334, 30);
		final String tradingPartnerRsn = tradingWith.getText().replace(TRADING_WITH, "");
		final Widget partnerTrades = client.getWidget(334, 29);

		int pureEssenceTraded = 0;
		int bindingNecklaceTraded = 0;
		for (Widget widget : partnerTrades.getChildren())
		{
			if (widget.getText().equals("Pure essence"))
			{
				pureEssenceTraded++;
			}
			else if (widget.getText().equals("Binding necklace"))
			{
				bindingNecklaceTraded++;
			}
		}
		session.updateRunnerStatistic(tradingPartnerRsn, pureEssenceTraded, bindingNecklaceTraded);
	}

	public static void forceRightClick(final Client client, final MenuShouldLeftClick menuShouldLeftClick, final int objectId)
	{
		final MenuEntry[] menuEntries = client.getMenuEntries();
		final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
		if (menuEntry.getIdentifier() == objectId && menuEntry.getOpcode() == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId())
		{
			menuShouldLeftClick.setForceRightClick(true);
		}
	}

	public static Map<Integer, String> getClanMessagesMap(final int size)
	{
		return new LinkedHashMap<>(size)
		{
			@Override
			public boolean removeEldestEntry(Map.Entry<Integer, String> eldest)
			{
				return this.size() > size;
			}
		};
	}
}