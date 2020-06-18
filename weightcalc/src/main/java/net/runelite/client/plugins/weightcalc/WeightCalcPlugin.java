package net.runelite.client.plugins.weightcalc;

import com.google.inject.Provides;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Weight Calculator",
	description = "Helps calculate weights",
	tags = {"weight"},
	enabledByDefault = false,
	type = PluginType.UTILITY
)

@Slf4j
public class WeightCalcPlugin extends Plugin
{
	@Inject
	private WeightCalcWidgetItemOverlay widgetItemOverlay;

	@Inject
	private WeightCalcOverlayPanel overlayPanel;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;

	@Inject
	private WeightCalcConfig config;

	static final String CONFIG_GROUP_KEY = "weightcalc";

	// Constants for weighing items IDs and weights.
	public static int THOUSANDTH_KG_ID;
	public static int HUNDREDTH_KG_ID;
	public static int TENTH_KG_ID;
	public static int HALF_KG_ID;
	public static int COINS_ID = 995;

	public static final BigDecimal THOUSANDTH_KG_WEIGHT = new BigDecimal("0.001");
	public static final BigDecimal HUNDREDTH_KG_WEIGHT = new BigDecimal("0.010");
	public static final BigDecimal TENTH_KG_WEIGHT = new BigDecimal("0.100");
	public static final BigDecimal HALF_KG_WEIGHT = new BigDecimal("0.500");

	// Constants for determining the player state
	public static final int STATE_EQUIPPED = 0;
	public static final int STATE_EMPTY = 1;
	public static final int STATE_ITEM_UNKNOWN = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_TOO_MANY_ITEMS = 4;
	public static final int STATE_WEIGHING = 5;
	public static final int STATE_UNKNOWN = 6;

	// The client produced weight while the player is only holding the item to weigh.
	// Note that due to the way the client displays weight, this will always be an int.
	@Getter
	private static BigDecimal aloneWeightBD = new BigDecimal("0");

	// Minimum and maximum weights required in order to determine weight of the object
	@Getter
	private static BigDecimal minWeight = new BigDecimal("0");
	@Getter
	private static BigDecimal maxWeight = new BigDecimal("0");

	// The number of each weighing object in the player's inventory.
	@Getter
	private static final int[] itemCounts = new int[4];

	// Info for determining whether the player needs to withdraw/deposit a weighing item.
	@Getter
	private static WeightCalcMessage wm = null;

	// The item that the player is currently weighing. Needed to display certain messages.
	@Getter
	private static Item currentItem = null;

	// The current state. Needed by the overlays to display messages correctly.
	@Getter
	int state = STATE_EMPTY;

	// int version of aloneWeightBD for simple comparisons only in this class.
	private static int aloneWeight = 0;

	@Provides
	WeightCalcConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WeightCalcConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(widgetItemOverlay);
		overlayManager.add(overlayPanel);
		THOUSANDTH_KG_ID = config.thousandthKgConfig().getId();
		HUNDREDTH_KG_ID = config.hundredthKgConfig().getId();
		TENTH_KG_ID = config.tenthKgConfig().getId();
		HALF_KG_ID = config.halfKgConfig().getId();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(widgetItemOverlay);
		overlayManager.remove(overlayPanel);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		switch (event.getKey())
		{
			case WeightCalcConfig.HALF_KG_KEYNAME:
				HALF_KG_ID = config.halfKgConfig().getId();
				break;
			case WeightCalcConfig.TENTH_KG_KEYNAME:
				TENTH_KG_ID = config.tenthKgConfig().getId();
				break;
			case WeightCalcConfig.HUNDREDTH_KG_KEYNAME:
				HUNDREDTH_KG_ID = config.hundredthKgConfig().getId();
				break;
			case WeightCalcConfig.THOUSANDTH_KG_KEYNAME:
				THOUSANDTH_KG_ID = config.thousandthKgConfig().getId();
			default:
				break;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (InventoryID.INVENTORY.getId() != event.getContainerId())
		{
			return;
		}

		final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		final ItemContainer equipmentContainer = client.getItemContainer(InventoryID.EQUIPMENT);

		if (itemContainer == null)
		{
			return;
		}

		final Item[] items = itemContainer.getItems();
		state = determineState(itemContainer, equipmentContainer);

		if (state == STATE_EQUIPPED)
		{
			log.debug("STATE_EQUIPPED");
			currentItem = null;
			wm = null;
		}

		else if (state == STATE_EMPTY)
		{
			log.debug("STATE_EMPTY");
			currentItem = null;
			wm = null;
		}

		else if (state == STATE_ITEM_UNKNOWN)
		{
			log.debug("STATE_ITEM_UNKNOWN");
			currentItem = null;
			wm = null;
		}

		else if (state == STATE_READY)
		{
			log.debug("STATE_READY");
			currentItem = getItemToWeigh(items);

			aloneWeight = client.getWeight();
			aloneWeightBD = new BigDecimal(aloneWeight);
			minWeight = new BigDecimal("0");
			maxWeight = minWeight.add(BigDecimal.ONE);
			wm = solve();
		}

		else if (state == STATE_TOO_MANY_ITEMS)
		{
			log.debug("STATE_TOO_MANY_ITEMS");
			wm = null;
		}

		else if (state == STATE_WEIGHING)
		{
			log.debug("STATE_WEIGHING");

			int currentWeight = client.getWeight();
			BigDecimal extraWeight = getExtraWeight();
			log.debug("Extra weight: " + extraWeight);
			// If the current weight shown is still below the alone weight + 1,
			// we know that the added weight needs to be increased.
			if (currentWeight < aloneWeight + 1)
			{
				if (minWeight.compareTo(extraWeight) <= 0)
				{
					minWeight = extraWeight.add(THOUSANDTH_KG_WEIGHT);
				}
			}
			else if (currentWeight >= aloneWeight + 1)
			{
				if (maxWeight.compareTo(extraWeight) > 0)
				{
					maxWeight = extraWeight;
				}
			}

			if (maxWeight.compareTo(minWeight) == 0)
			{
				wm = null;
				log.debug("Correct weight is: " + (new BigDecimal(aloneWeight + 1).subtract(minWeight)));
			}
			else
			{
				log.debug("Solving...");
				wm = solve();
			}
		}

		else if (state == STATE_UNKNOWN)
		{
			log.debug("STATE UNKNOWN");
			currentItem = null;
			wm = null;
		}
	}

	private BigDecimal getExtraWeight()
	{
		BigDecimal extraWeight = BigDecimal.ZERO;
		extraWeight = extraWeight.add(THOUSANDTH_KG_WEIGHT.multiply(new BigDecimal(itemCounts[0])));
		extraWeight = extraWeight.add(HUNDREDTH_KG_WEIGHT.multiply(new BigDecimal(itemCounts[1])));
		extraWeight = extraWeight.add(TENTH_KG_WEIGHT.multiply(new BigDecimal(itemCounts[2])));
		extraWeight = extraWeight.add(HALF_KG_WEIGHT.multiply(new BigDecimal(itemCounts[3])));

		return extraWeight;
	}

	private int determineState(ItemContainer itemContainer, ItemContainer equipmentContainer)
	{
		Arrays.fill(itemCounts, 0);

		// If the player has anything equipped, we are in the equipped state.
		if (equipmentContainer != null)
		{
			final Item[] equipment = equipmentContainer.getItems();
			if (realSize(equipment) != 0)
			{
				return STATE_EQUIPPED;
			}
		}

		// If the inventory container exists, we have six possible states that we could be in.
		// STATE_EMPTY - The player's inventory is empty.
		// STATE_READY - The player's inventory contains a single item, but this is not the item that player was
		// 				previously weighing.
		// STATE_ITEM_UNKNOWN - The player's inventory no longer contains an item to weigh.
		// STATE_TOO_MANY_ITEMS - The player's inventory contains more than one non-weighing item.
		// STATE_WEIGHING - The player's inventory contains only the item in the process of being weighed and
		// 				weighing items.
		if (itemContainer != null)
		{
			Item[] items = itemContainer.getItems();
			if (realSize(items) == 0)
			{
				return STATE_EMPTY;
			}
			else if (realSize(items) == 1 && (currentItem == null || getItemToWeigh(items).getId() != currentItem.getId()))
			{
				int id = getItemToWeigh(items).getId();
				if (!(id == HALF_KG_ID || id == TENTH_KG_ID || id == HUNDREDTH_KG_ID || id == THOUSANDTH_KG_ID))
				{
					return STATE_READY;
				}
			}

			if (realSize(items) >= 1)
			{
				// We could either be in the weighing state or the too many items state depending on the inventory.
				int nonWeighingItemCount = 0;
				boolean itemWeighedInInventory = false;
				for (Item item : items)
				{
					// Determine how many of each weighing object the player has in their inventory.
					// Note that the -1 case is to ignore items removed from the inventory.
					int id = item.getId();
					if (id == THOUSANDTH_KG_ID)
					{
						itemCounts[0] += 1;
					}
					else if (id == HUNDREDTH_KG_ID)
					{
						itemCounts[1] += 1;
					}
					else if (id == TENTH_KG_ID)
					{
						itemCounts[2] += 1;
					}
					else if (id == HALF_KG_ID)
					{
						itemCounts[3] += 1;
					}
					else if (id == -1)
					{
						continue;
					}
					else if (item.getId() != COINS_ID)
					{
						nonWeighingItemCount++;
					}

					if (currentItem != null && item.getId() == currentItem.getId())
					{
						itemWeighedInInventory = true;
					}
				}
				if (nonWeighingItemCount > 1)
				{
					return STATE_TOO_MANY_ITEMS;
				}
				else if (!itemWeighedInInventory)
				{
					return STATE_ITEM_UNKNOWN;
				}
				else
				{
					return STATE_WEIGHING;
				}
			}
		}
		// In case the item container is null, return a special state that will display an error.
		return STATE_UNKNOWN;
	}

	// Get the actual number of items in the player's inventory.
	// The length of the array is not accurate since removed items show up with item id -1.
	private int realSize(Item[] items)
	{
		int size = 0;
		for (Item item : items)
		{
			if (item.getId() != -1 && item.getId() != COINS_ID)
			{
				size += 1;
			}
		}
		return size;
	}

	// Expects an array of Item objects such that realSize(items) == 1
	// Returns the first item object with an id other than -1 or COINS_ID in the array of items.
	// If items has no objects with an id other than -1 or COINS_ID, the first object in items is returned.
	private Item getItemToWeigh(Item[] items)
	{
		for (Item item : items)
		{
			if (item.getId() != -1 && item.getId() != COINS_ID)
			{
				return item;
			}
		}
		return items[0];
	}

	// Creates a new WeightCalcMessage object for the overlay to use.
	// The WeightCalcMessage describes what action to do (withdraw/deposit) and which item to do said action with.
	private WeightCalcMessage solve()
	{
		int totalRocks = 10;
		int totalKeys = 9;
		int totalBones = 4;
		int totalBalls = 1;

		int remainingRocks = totalRocks - itemCounts[0];
		int remainingKeys = totalKeys - itemCounts[1];
		int remainingGroundBones = totalBones - itemCounts[2];
		int remainingGnomeballs = totalBalls - itemCounts[3];

		BigDecimal currentWeight = getExtraWeight();
		BigDecimal maxWeight = getMaxWeight();
		BigDecimal minWeight = getMinWeight();

		BigDecimal range = maxWeight.subtract(currentWeight);
		// Leaving this very explicit for readability purposes.
		// This could almost certainly be condensed using a new class and array.
		if (currentWeight.compareTo(minWeight) <= 0)
		{
			if (range.compareTo(HALF_KG_WEIGHT) > 0 && remainingGnomeballs > 0)
			{
				return new WeightCalcMessage(HALF_KG_ID, true);
			}
			else if (range.compareTo(TENTH_KG_WEIGHT) > 0 && remainingGroundBones > 0)
			{
				return new WeightCalcMessage(TENTH_KG_ID, true);
			}
			else if (range.compareTo(HUNDREDTH_KG_WEIGHT) > 0 && remainingKeys > 0)
			{
				return new WeightCalcMessage(HUNDREDTH_KG_ID, true);
			}
			else if (range.compareTo(THOUSANDTH_KG_WEIGHT) >= 0 && remainingRocks > 0)
			{
				return new WeightCalcMessage(THOUSANDTH_KG_ID, true);
			}
		}
		else
		{
			if (remainingRocks < totalRocks)
			{
				return new WeightCalcMessage(THOUSANDTH_KG_ID, false);
			}
			else if (remainingKeys < totalKeys)
			{
				return new WeightCalcMessage(HUNDREDTH_KG_ID, false);
			}
			else if (remainingGroundBones < totalBones)
			{
				return new WeightCalcMessage(TENTH_KG_ID, false);
			}
			else if (remainingGnomeballs < totalBalls)
			{
				return new WeightCalcMessage(HALF_KG_ID, false);
			}
		}
		return null;
	}

}