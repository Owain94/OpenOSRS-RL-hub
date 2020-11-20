/*
 * Copyright (c) 2020, MMagicala <https://github.com/MMagicala>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.github.mmagicala.gnomeRestaurant;

import com.google.inject.Provides;
import io.github.mmagicala.gnomeRestaurant.itemOrder.BakedOrder;
import io.github.mmagicala.gnomeRestaurant.itemOrder.BakedToppedOrder;
import io.github.mmagicala.gnomeRestaurant.itemOrder.HeatedCocktailOrder;
import io.github.mmagicala.gnomeRestaurant.itemOrder.CocktailOrder;
import io.github.mmagicala.gnomeRestaurant.itemOrder.ItemOrder;
import java.security.InvalidParameterException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuOpcode;
import net.runelite.api.NPC;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Timer;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Gnome Restaurant",
	description = "Add quality-of-life features to the Gnome Restaurant minigame",
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class GnomeRestaurantPlugin extends Plugin
{
	private static final Pattern DELIVERY_START_PATTERN =
		Pattern.compile("([\\w .]+) wants (?:some|a) ([\\w ]+)");

	private static final String EASY_DELIVERY_DELAY_TEXT = "Fine, your loss. If you want another easy job one come back in five minutes and maybe I'll be able to find you one.";
	private static final String HARD_DELIVERY_DELAY_TEXT = "Fine, your loss. I may have an easier job for you, since you chickened out of that one, If you want another hard one come back in five minutes and maybe I'll be able to find you a something.";

	// NPC printed and actual names

	private static final HashMap<String, String> easyOrderNPCs = new HashMap<String, String>()
	{
		{
			put("Burkor", null);
			put("Brimstall", null);
			put("Captain Errdo", null);
			put("Coach", "Gnome Coach");
			put("Dalila", null);
			put("Damwin", null);
			put("Eebel", null);
			put("Ermin", null);
			put("Femi", null);
			put("Froono", null);
			put("Guard Vemmeldo", null);
			put("Gulluck", null);
			put("His Royal Highness King Narnode", "King Narnode Shareen");
			put("Meegle", null);
			put("Perrdur", null);
			put("Rometti", null);
			put("Sarble", null);
			put("Trainer Nacklepen", null);
			put("Wurbel", null);
			put("Heckel Funch", null);
		}
	};

	private static final HashMap<String, String> hardOrderNPCs = new HashMap<String, String>()
	{
		{
			put("Ambassador Ferrnook", null);
			put("Ambassador Gimblewap", null);
			put("Ambassador Spanfipple", null);
			put("Brambickle", null);
			put("Captain Bleemadge", null);
			put("Captain Daerkin", null);
			put("Captain Dalbur", null);
			put("Captain Klemfoodle", null);
			put("Captain Ninto", null);
			put("G.L.O Caranock", null);
			put("Garkor", null);
			put("Gnormadium Avlafrim", null);
			put("Hazelmere", null);
			put("King Bolren", null);
			put("Lieutenant Schepbur", null);
			put("Penwie", null);
			put("Professor Imblewyn", null);
			put("Professor Manglethorp", null);
			put("Professor Onglewip", null);
			put("Wingstone", null);
		}
	};

	@Inject
	private Client client;

	@Inject
	private GnomeRestaurantConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	// UI

	private Timer orderTimer, delayTimer;
	private Overlay overlay;

	// Order status

	private boolean isDeliveryForTesting = false;

	private boolean isTrackingDelivery = false;

	// Order data

	private static final Map<String, ItemOrder> itemOrders = Collections.unmodifiableMap(new Hashtable<String, ItemOrder>()
	{
		{
			// Gnomebowls

			put("worm hole",
				new BakedToppedOrder(
					ItemOrderType.GNOMEBOWL,
					ItemID.HALF_MADE_BOWL_9559,
					ItemID.UNFINISHED_BOWL_9560,
					ItemID.WORM_HOLE,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.KING_WORM, 4));
							add(new CookingItem(ItemID.ONION, 2));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
						}
					}
				)
			);
			put("vegetable ball", new BakedToppedOrder(
					ItemOrderType.GNOMEBOWL,
					ItemID.HALF_MADE_BOWL_9561,
					ItemID.UNFINISHED_BOWL_9562,
					ItemID.VEG_BALL,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.POTATO, 2));
							add(new CookingItem(ItemID.ONION, 2));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
						}
					}
				)
			);
			put("tangled toads legs", new BakedOrder(
					ItemOrderType.GNOMEBOWL,
					ItemID.HALF_MADE_BOWL,
					ItemID.TANGLED_TOADS_LEGS,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.TOADS_LEGS, 4));
							add(new CookingItem(ItemID.GNOME_SPICE, 2));
							add(new CookingItem(ItemID.CHEESE, 1));
							add(new CookingItem(ItemID.DWELLBERRIES, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1));
						}
					}
				)
			);
			put("chocolate bomb", new BakedToppedOrder(
					ItemOrderType.GNOMEBOWL,
					ItemID.HALF_MADE_BOWL_9563,
					ItemID.UNFINISHED_BOWL_9564,
					ItemID.CHOCOLATE_BOMB,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.CHOCOLATE_BAR, 4));
							add(new CookingItem(ItemID.EQUA_LEAVES, 2));
							add(new CookingItem(ItemID.CHOCOLATE_DUST, 1, true));
							add(new CookingItem(ItemID.POT_OF_CREAM, 2, true));
						}
					}
				)
			);

			// Battas

			put("fruit batta", new BakedToppedOrder(
					ItemOrderType.BATTA,
					ItemID.HALF_MADE_BATTA,
					ItemID.UNFINISHED_BATTA_9479,
					ItemID.FRUIT_BATTA,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.EQUA_LEAVES, 4));
							add(new CookingItem(ItemID.LIME_CHUNKS, 1));
							add(new CookingItem(ItemID.ORANGE_CHUNKS, 1));
							add(new CookingItem(ItemID.PINEAPPLE_CHUNKS, 1));
							add(new CookingItem(ItemID.GNOME_SPICE, 1, true));
						}
					}
				)
			);
			put("toad batta", new BakedOrder(
					ItemOrderType.BATTA,
					ItemID.HALF_MADE_BATTA_9482,
					ItemID.TOAD_BATTA,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.EQUA_LEAVES, 4));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.CHEESE, 1));
							add(new CookingItem(ItemID.TOADS_LEGS, 1));
						}
					}
				)
			);
			put("worm batta", new BakedToppedOrder(
					ItemOrderType.BATTA,
					ItemID.HALF_MADE_BATTA_9480,
					ItemID.UNFINISHED_BATTA_9481,
					ItemID.WORM_BATTA,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.KING_WORM, 1));
							add(new CookingItem(ItemID.CHEESE, 1));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
						}
					}
				)
			);
			put("vegetable batta", new BakedToppedOrder(
					ItemOrderType.BATTA,
					ItemID.HALF_MADE_BATTA_9485,
					ItemID.UNFINISHED_BATTA_9486,
					ItemID.VEGETABLE_BATTA,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.TOMATO, 2));
							add(new CookingItem(ItemID.DWELLBERRIES, 1));
							add(new CookingItem(ItemID.ONION, 1));
							add(new CookingItem(ItemID.CHEESE, 1));
							add(new CookingItem(ItemID.CABBAGE, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
						}
					}
				)
			);
			put("cheese and tomato batta", new BakedToppedOrder(
					ItemOrderType.BATTA,
					ItemID.HALF_MADE_BATTA_9483,
					ItemID.UNFINISHED_BATTA_9484,
					ItemID.CHEESETOM_BATTA,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.CHEESE, 1));
							add(new CookingItem(ItemID.TOMATO, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
						}
					}
				)
			);

			// Crunchies

			put("choc chip crunchies", new BakedToppedOrder(
					ItemOrderType.CRUNCHIES,
					ItemID.HALF_MADE_CRUNCHY,
					ItemID.UNFINISHED_CRUNCHY_9578,
					ItemID.CHOCCHIP_CRUNCHIES,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.CHOCOLATE_BAR, 2));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.CHOCOLATE_DUST, 1, true));
						}
					}
				)
			);
			put("spicy crunchies", new BakedToppedOrder(
					ItemOrderType.CRUNCHIES,
					ItemID.HALF_MADE_CRUNCHY_9579,
					ItemID.UNFINISHED_CRUNCHY_9580,
					ItemID.SPICY_CRUNCHIES,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.EQUA_LEAVES, 2));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.GNOME_SPICE, 1, true));
						}
					}
				)
			);
			put("toad crunchies", new BakedToppedOrder(
					ItemOrderType.CRUNCHIES,
					ItemID.HALF_MADE_CRUNCHY_9581,
					ItemID.UNFINISHED_CRUNCHY_9582,
					ItemID.TOAD_CRUNCHIES,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.TOADS_LEGS, 2));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
						}
					}
				)
			);
			put("worm crunchies", new BakedToppedOrder(
					ItemOrderType.CRUNCHIES,
					ItemID.HALF_MADE_CRUNCHY_9583,
					ItemID.UNFINISHED_CRUNCHY_9584,
					ItemID.WORM_CRUNCHIES,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.KING_WORM, 2));
							add(new CookingItem(ItemID.GNOME_SPICE, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1));
							add(new CookingItem(ItemID.GNOME_SPICE, 1, true));
						}
					}
				)
			);

			// Gnome cocktails

			put("fruit blast", new CocktailOrder(
					ItemID.MIXED_BLAST,
					ItemID.FRUIT_BLAST,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.PINEAPPLE, 1));
							add(new CookingItem(ItemID.LEMON, 1));
							add(new CookingItem(ItemID.ORANGE, 1));
							add(new CookingItem(ItemID.LEMON_SLICES, 1, true));
						}
					}
				)
			);
			put("pineapple punch", new CocktailOrder(
					ItemID.MIXED_PUNCH,
					ItemID.PINEAPPLE_PUNCH,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.PINEAPPLE, 2));
							add(new CookingItem(ItemID.LEMON, 1));
							add(new CookingItem(ItemID.ORANGE, 1));
							add(new CookingItem(ItemID.LIME_CHUNKS, 1, true));
							add(new CookingItem(ItemID.PINEAPPLE_CHUNKS, 1, true));
							add(new CookingItem(ItemID.ORANGE_SLICES, 1, true));
						}
					}
				)
			);
			put("wizard blizzard", new CocktailOrder(
				ItemID.MIXED_BLIZZARD,
				ItemID.WIZARD_BLIZZARD,
				new ArrayList<CookingItem>()
				{
					{
						add(new CookingItem(ItemID.VODKA, 2));
						add(new CookingItem(ItemID.GIN, 1));
						add(new CookingItem(ItemID.LIME, 1));
						add(new CookingItem(ItemID.LEMON, 1));
						add(new CookingItem(ItemID.ORANGE, 1));
						add(new CookingItem(ItemID.PINEAPPLE_CHUNKS, 1, true));
						add(new CookingItem(ItemID.LIME_SLICES, 1, true));
					}
				}));
			put("short green guy", new CocktailOrder(
					ItemID.MIXED_SGG,
					ItemID.SHORT_GREEN_GUY,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.VODKA, 1));
							add(new CookingItem(ItemID.LIME, 3));
							add(new CookingItem(ItemID.LIME_SLICES, 1, true));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
						}
					}
				)
			);
			put("drunk dragon", new HeatedCocktailOrder(
					HeatTiming.AFTER_ADDING_INGREDS,
					ItemID.MIXED_DRAGON,
					ItemID.MIXED_DRAGON_9575,
					ItemID.MIXED_DRAGON_9576,
					ItemID.DRUNK_DRAGON,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.VODKA, 1));
							add(new CookingItem(ItemID.GIN, 1));
							add(new CookingItem(ItemID.DWELLBERRIES, 1));
							add(new CookingItem(ItemID.PINEAPPLE_CHUNKS, 1, true));
							add(new CookingItem(ItemID.POT_OF_CREAM, 1, true));
						}
					}
				)
			);
			put("choc saturday", new HeatedCocktailOrder(
					HeatTiming.BEFORE_ADDING_INGREDS,
					ItemID.MIXED_SATURDAY,
					ItemID.MIXED_SATURDAY_9572,
					ItemID.MIXED_SATURDAY_9573,
					ItemID.CHOC_SATURDAY,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.WHISKY, 1));
							add(new CookingItem(ItemID.CHOCOLATE_BAR, 1));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1));
							add(new CookingItem(ItemID.BUCKET_OF_MILK, 1));
							add(new CookingItem(ItemID.CHOCOLATE_DUST, 1, true));
							add(new CookingItem(ItemID.POT_OF_CREAM, 1, true));
						}
					}
				)
			);
			put("blurberry special", new CocktailOrder(
					ItemID.MIXED_SPECIAL,
					ItemID.BLURBERRY_SPECIAL,
					new ArrayList<CookingItem>()
					{
						{
							add(new CookingItem(ItemID.VODKA, 1));
							add(new CookingItem(ItemID.BRANDY, 1));
							add(new CookingItem(ItemID.GIN, 1));
							add(new CookingItem(ItemID.LEMON, 2));
							add(new CookingItem(ItemID.ORANGE, 1));
							add(new CookingItem(ItemID.LEMON_CHUNKS, 1, true));
							add(new CookingItem(ItemID.ORANGE_CHUNKS, 1, true));
							add(new CookingItem(ItemID.EQUA_LEAVES, 1, true));
							add(new CookingItem(ItemID.LIME_SLICES, 1, true));
						}
					}
				)
			);
		}
	});

	// Order information

	private ItemOrder itemOrder;
	private String recipientRealName;

	// Stage nodes

	private final ArrayList<StageNode> stageNodes = new ArrayList<>();

	@Getter
	private int currentStageNodeIndex;

	public String getCurrentStageDirections()
	{
		return stageNodes.get(currentStageNodeIndex).getStage().directions;
	}

	// Overlay tables

	private final Hashtable<Integer, OverlayEntry> currentItemsOverlayTable = new Hashtable<>();
	private final Hashtable<Integer, OverlayEntry> futureItemsOverlayTable = new Hashtable<>();

	// Overlay strings

	public static final String OVERLAY_MENU_ENTRY_TEXT = "Reset Stage";

	@Override
	protected void shutDown() throws Exception
	{
		reset();
	}

	private void reset()
	{
		removeOrderTimer();
		removeDelayTimer();
		removeOverlay();
		client.clearHintArrow();

		isTrackingDelivery = false;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getWidget(WidgetInfo.DIALOG_NPC_NAME) != null
			&& client.getWidget(WidgetInfo.DIALOG_NPC_NAME).getText().equals("Gianne jnr.")
		)
		{
			String dialog = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT).getText();

			// Replace line breaks with spaces

			dialog = dialog.replace("<br>", " ");

			Matcher deliveryStartMatcher = DELIVERY_START_PATTERN.matcher(dialog);

			if (isDeliveryForTesting)
			{
				resetPluginAndTest("Starting real delivery");
			}

			if (deliveryStartMatcher.find() && !isTrackingDelivery)
			{
				startTrackingDelivery(deliveryStartMatcher.group(1), deliveryStartMatcher.group(2));
			}

			// Show delay timer if player refuses the order

			if (config.showDelayTimer() && delayTimer == null && (dialog.contains(EASY_DELIVERY_DELAY_TEXT) || dialog.contains(HARD_DELIVERY_DELAY_TEXT)))
			{
				delayTimer = new Timer(5, ChronoUnit.MINUTES, itemManager.getImage(ItemID.ALUFT_ALOFT_BOX), this);
				delayTimer.setTooltip("Cannot place an order at this time");
				infoBoxManager.addInfoBox(delayTimer);
			}
		}
	}

	private void startTrackingDelivery(String printedRecipientName, String orderName)
	{
		// Players can change their order upon earning a full reward token

		reset();

		itemOrder = itemOrders.get(orderName);

		if (itemOrder == null)
		{
			throw new InvalidParameterException("No order found with the name " + orderName);
		}

		boolean isHardOrder;

		if (easyOrderNPCs.containsKey(printedRecipientName))
		{
			recipientRealName = easyOrderNPCs.get(printedRecipientName) == null ? printedRecipientName : easyOrderNPCs.get(printedRecipientName);
			isHardOrder = false;
		}
		else if (hardOrderNPCs.containsKey(printedRecipientName))
		{
			recipientRealName = hardOrderNPCs.get(printedRecipientName) == null ? printedRecipientName : hardOrderNPCs.get(printedRecipientName);
			isHardOrder = true;
		}
		else
		{
			throw new InvalidParameterException("No recipient found with the name " + printedRecipientName);
		}

		isTrackingDelivery = true;

		// Delete the delay timer if it is active (we can choose hard orders during a delay)

		removeDelayTimer();

		if (config.showOverlay())
		{
			// Build stage list

			rebuildStageNodeList();
			currentStageNodeIndex = 0;

			// Determine initial stage, initialize overlay, and create overlay tables

			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			assert inventory != null;

			overlay = new GnomeRestaurantOverlay(this, currentItemsOverlayTable, futureItemsOverlayTable);
			overlayManager.add(overlay);

			updateStage(inventory, true);
		}

		if (config.showOrderTimer())
		{
			int numSecondsLeft;

			if (isHardOrder)
			{
				numSecondsLeft = 660;
			}
			else
			{
				numSecondsLeft = 360;
			}

			orderTimer = new Timer(numSecondsLeft, ChronoUnit.SECONDS, itemManager.getImage(itemOrder.getItemId()), this);

			String tooltipText = "Deliver " + orderName + " to " + recipientRealName;
			orderTimer.setTooltip(tooltipText);
			infoBoxManager.addInfoBox(orderTimer);
		}

		// Draw hint arrow if we can already identify the NPC

		if (config.showHintArrow())
		{
			markNPCFromCache();
		}
	}

	private void markNPCFromCache()
	{
		NPC[] npcs = client.getCachedNPCs();

		for (NPC npc : npcs)
		{
			if (toggleMarkRecipient(npc, true))
			{
				return;
			}
		}
	}

	private boolean toggleMarkRecipient(NPC npc, boolean mark)
	{
		if (npc == null || npc.getName() == null)
		{
			return false;
		}

		if (npc.getName().equals(recipientRealName))
		{
			if (mark)
			{
				client.setHintArrow(npc);
			}
			else
			{
				client.clearHintArrow();
			}
			return true;
		}
		return false;
	}

	@Subscribe
	public void onNpcSpawned(final NpcSpawned event)
	{
		if (isTrackingDelivery && config.showHintArrow())
		{
			toggleMarkRecipient(event.getNpc(), true);
		}
	}

	@Subscribe
	public void onNpcDespawned(final NpcDespawned event)
	{
		if (isTrackingDelivery && config.showHintArrow())
		{
			toggleMarkRecipient(event.getNpc(), false);
		}
	}

	// Build a linear graph that links stages together, and define the items required to move to the next stage

	private void rebuildStageNodeList()
	{
		stageNodes.clear();

		// Ingredients

		ArrayList<CookingItem> initialIngredients = itemOrder.getIngredients(false);
		ArrayList<CookingItem> laterIngredients = itemOrder.getIngredients(true);

		if (itemOrder.getItemOrderType() == ItemOrderType.COCKTAIL)
		{
			// Starting items

			ArrayList<CookingItem> startingItems = new ArrayList<>(initialIngredients);
			startingItems.add(new CookingItem(ItemID.COCKTAIL_SHAKER, 1));
			stageNodes.add(new StageNode(MinigameStage.COMBINE_INGREDIENTS, startingItems));

			ArrayList<CookingItem> requiredItemsToPour = new ArrayList<CookingItem>();


			requiredItemsToPour.add(new CookingItem(ItemID.COCKTAIL_GLASS, 1));


			if (itemOrder instanceof HeatedCocktailOrder)
			{
				stageNodes.add(new StageNode(MinigameStage.POUR, requiredItemsToPour, ((HeatedCocktailOrder) itemOrder).getShakerMixId()));

				if (((HeatedCocktailOrder) itemOrder).getHeatTiming() == HeatTiming.BEFORE_ADDING_INGREDS)
				{
					stageNodes.add(new StageNode(MinigameStage.HEAT_AGAIN, ((HeatedCocktailOrder) itemOrder).getPouredMixId()));
					stageNodes.add(new StageNode(MinigameStage.TOP_WITH_INGREDIENTS, laterIngredients, ((HeatedCocktailOrder) itemOrder).getSecondPouredMixId()));
				}
				else
				{
					stageNodes.add(new StageNode(MinigameStage.TOP_WITH_INGREDIENTS, laterIngredients, ((HeatedCocktailOrder) itemOrder).getPouredMixId()));
					stageNodes.add(new StageNode(MinigameStage.HEAT_AGAIN, ((HeatedCocktailOrder) itemOrder).getSecondPouredMixId()));
				}
			}
			else
			{
				requiredItemsToPour.addAll(laterIngredients);
				stageNodes.add(new StageNode(MinigameStage.POUR, requiredItemsToPour, ((CocktailOrder) itemOrder).getShakerMixId()));
			}
		}
		else
		{
			ArrayList<CookingItem> startingItems = new ArrayList<>();
			startingItems.add(new CookingItem(ItemID.GIANNE_DOUGH, 1));
			startingItems.add(new CookingItem(itemOrder.getItemOrderType().getToolId(), 1));

			stageNodes.add(new StageNode(MinigameStage.CREATE_MOULD, startingItems));
			stageNodes.add(new StageNode(MinigameStage.BAKE_MOULD, itemOrder.getItemOrderType().getMouldId()));
			stageNodes.add(new StageNode(MinigameStage.COMBINE_INGREDIENTS, initialIngredients, itemOrder.getItemOrderType().getHalfBakedId()));

			stageNodes.add(new StageNode(MinigameStage.HEAT_AGAIN, ((BakedOrder) itemOrder).getHalfMadeId()));

			if (itemOrder instanceof BakedToppedOrder)
			{
				stageNodes.add(new StageNode(MinigameStage.TOP_WITH_INGREDIENTS, laterIngredients, ((BakedToppedOrder) itemOrder).getUnfinishedId()));
			}
		}
		stageNodes.add(new StageNode(MinigameStage.DELIVER, new ArrayList<CookingItem>()
		{
			{
				add(new CookingItem(ItemID.ALUFT_ALOFT_BOX, 1));
			}
		}, itemOrder.getItemId()));
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// Ignore varbit changes while we are testing, since it will stay 0

		if (isTrackingDelivery && !isDeliveryForTesting && client.getVarbitValue(2478) == 0)
		{
			reset();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (overlay != null)
		{
			if (event.getContainerId() != InventoryID.INVENTORY.getId())
			{
				return;
			}

			updateStage(event.getItemContainer(), false);
		}
	}

	/**
	 * Update stage according to inventory and update / rebuild overlay tables
	 * @param forceRebuildOverlayTables Set this to true when we need to build an overlay table upon receiving a delivery
	 */
	private void updateStage(ItemContainer inventory, boolean forceRebuildOverlayTables)
	{
		int traversedStageNodeIndex = stageNodes.size() - 1;

		while (traversedStageNodeIndex > currentStageNodeIndex)
		{
			if (inventory.contains(stageNodes.get(traversedStageNodeIndex).getProducedItemId()))
			{
				currentStageNodeIndex = traversedStageNodeIndex;

				// Rebuild overlay tables after updating the stage

				rebuildOverlayTables(inventory);

				return;
			}
			traversedStageNodeIndex--;
		}

		ArrayList<Hashtable<Integer, OverlayEntry>> overlayTables = new ArrayList<Hashtable<Integer, OverlayEntry>>()
		{
			{
				add(currentItemsOverlayTable);
				add(futureItemsOverlayTable);
			}
		};

		if (forceRebuildOverlayTables)
		{
			rebuildOverlayTables(inventory);
		}
		else
		{
			// Simply update inventory counts for overlay tables if the stage has not changed

			for (Hashtable<Integer, OverlayEntry> overlayTable : overlayTables)
			{
				for (Map.Entry<Integer, OverlayEntry> entry : overlayTable.entrySet())
				{
					int realInventoryCount = inventory.count(entry.getKey());
					if (entry.getValue().getInventoryCount() != realInventoryCount)
					{
						entry.getValue().setInventoryCount(realInventoryCount);
					}
				}
			}
		}
	}

	// Overlay methods

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked event)
	{
		if (event.getEntry().getMenuOpcode() == MenuOpcode.RUNELITE_OVERLAY &&
			event.getEntry().getTarget().equals("Gnome Restaurant Overlay") &&
			event.getEntry().getOption().equals(OVERLAY_MENU_ENTRY_TEXT))
		{
			// Reset to beginning stage, then update it again

			currentStageNodeIndex = 0;

			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			assert inventory != null;
			updateStage(inventory, true);
		}
	}

	/**
	 * Add overlay entries to an overlay table
	 */
	private void addItemsToOverlayTable(Hashtable<Integer, OverlayEntry> overlayTable, ItemContainer inventory, ArrayList<CookingItem> itemStacks)
	{
		for (CookingItem itemStack : itemStacks)
		{
			String itemName = itemManager.getItemDefinition(itemStack.getItemId()).getName();
			overlayTable.put(itemStack.getItemId(), new OverlayEntry(itemName, inventory.count(itemStack.getItemId()), itemStack.getCount()));
		}
	}

	private void rebuildOverlayTables(ItemContainer inventory)
	{
		futureItemsOverlayTable.clear();
		currentItemsOverlayTable.clear();

		for (int i = stageNodes.size() - 1; i >= currentStageNodeIndex; i--)
		{
			Hashtable<Integer, OverlayEntry> overlayTable;
			ArrayList<CookingItem> requiredItems = new ArrayList<>(stageNodes.get(i).getOtherRequiredItems());
			if (i == currentStageNodeIndex)
			{
				overlayTable = currentItemsOverlayTable;
				if (i > 0)
				{
					requiredItems.add(new CookingItem(stageNodes.get(i).getProducedItemId(), 1));
				}
			}
			else
			{
				overlayTable = futureItemsOverlayTable;
			}
			addItemsToOverlayTable(overlayTable, inventory, requiredItems);
		}
	}

	// Config

	@Provides
	GnomeRestaurantConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GnomeRestaurantConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!config.showDelayTimer())
		{
			removeDelayTimer();
		}

		if (!config.showOrderTimer())
		{
			removeOrderTimer();
		}

		if (!config.showOverlay())
		{
			removeOverlay();
		}

		if (!config.showHintArrow())
		{
			client.clearHintArrow();
		}
		else if (isTrackingDelivery)
		{
			// Re-enable hint arrow

			markNPCFromCache();
		}
	}

	// UI cleaning

	private void removeOrderTimer()
	{
		infoBoxManager.removeInfoBox(orderTimer);
		orderTimer = null;
	}

	private void removeDelayTimer()
	{
		infoBoxManager.removeInfoBox(delayTimer);
		delayTimer = null;
	}

	private void removeOverlay()
	{
		overlayManager.remove(overlay);
		overlay = null;
	}

	/**
	 * Reset plugin and test, and print out the reason
	 */
	private void resetPluginAndTest(String errorMessage)
	{
		reset();
		isDeliveryForTesting = false;
		printTestMessage("Test cancelled. Reason: " + errorMessage);
	}

	private void printTestMessage(String message)
	{
		chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.GAMEMESSAGE).value(message).build());
	}
}
