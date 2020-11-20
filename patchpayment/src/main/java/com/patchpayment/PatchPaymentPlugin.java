package com.patchpayment;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import java.util.Arrays;

import static net.runelite.api.ItemID.*;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
		name = "Patch Payment",
		description =  "Creates a menu item to check a seed's required payment.",
		tags = {"farming", "payment"},
		loadWhenOutdated = true,
		enabledByDefault = false
)
@Slf4j
public class PatchPaymentPlugin extends Plugin {

	private static final String CHECK_PAYMENT = "Check";

	PairInterface[] paymentPairList = {
			// ALLOTMENT PAIRS
			new PaymentPair("2 buckets of compost", new int[] {POTATO_SEED}),
			new PaymentPair("1 full sack of potatoes", new int[] {ONION_SEED}),
			new PaymentPair("1 full sack of onions", new int[] {CABBAGE_SEED}),
			new PaymentPair("1 full sack of cabbages", new int[] {TOMATO_SEED}),
			new PaymentPair("10 jute fibres", new int[] {SWEETCORN_SEED}),
			new PaymentPair("1 full basket of apples", new int[] {STRAWBERRY_SEED}),
			new PaymentPair("10 curry leaves", new int[] {WATERMELON_SEED}),
			new PaymentPair("5 jangerberries", new int[] {SNAPE_GRASS_SEED}),
			// FLOWER SEEDS
			new CustomPair(new int[] {MARIGOLD_SEED, ROSEMARY_SEED, NASTURTIUM_SEED, WOAD_SEED, LIMPWURT_SEED, WHITE_LILY_SEED}),
			// HERB PAIRS
			new CustomPair(new int[] {GUAM_SEED, MARRENTILL_SEED, TARROMIN_SEED, HARRALANDER_SEED, GOUT_TUBER, RANARR_SEED, TOADFLAX_SEED, IRIT_SEED, AVANTOE_SEED, KWUARM_SEED, SNAPDRAGON_SEED, CADANTINE_SEED, LANTADYME_SEED, DWARF_WEED_SEED, TORSTOL_SEED}),
			// HOPS PAIRS
			new PaymentPair("3 buckets of compost", new int[] {BARLEY_SEED}),
			new PaymentPair("1 marigold", new int[] {HAMMERSTONE_SEED}, "hammerstone hops"),
			new PaymentPair("1 full sack of onions", new int[] {ASGARNIAN_SEED}, "asgarnian hops"),
			new PaymentPair("6 barley malts", new int[] {JUTE_SEED}),
			new PaymentPair("1 full basket of tomatoes", new int[] {YANILLIAN_SEED}, "yanillian hops"),
			new PaymentPair("3 full sacks of cabbages", new int[] {KRANDORIAN_SEED}, "krandorian hops"),
			new PaymentPair("1 nasturtium", new int[] {WILDBLOOD_SEED}),
			// BUSH PAIRS
			new PaymentPair("4 full sacks of cabbages", new int[] {REDBERRY_SEED}),
			new PaymentPair("3 full baskets of tomatoes", new int[] {CADAVABERRY_SEED}),
			new PaymentPair("3 full baskets of strawberries", new int[] {DWELLBERRY_SEED}),
			new PaymentPair("6 watermelons", new int[] {JANGERBERRY_SEED}),
			new PaymentPair("8 bittercap mushrooms", new int[] {WHITEBERRY_SEED}),
			new CustomPair("as it will never get diseased", new int[] {POISON_IVY_SEED}),
			// TREE PAIRS
			new PaymentPair("1 full basket of tomatoes", new int[] {ACORN, OAK_SAPLING, OAK_SEEDLING, OAK_SEEDLING_W}, "oak tree"),
			new PaymentPair("1 full basket of apples", new int[] {WILLOW_SEED, WILLOW_SAPLING, WILLOW_SEEDLING, WILLOW_SEEDLING_W}, "willow tree"),
			new PaymentPair("1 full basket of oranges", new int[] {MAPLE_SEED, MAPLE_SAPLING, MAPLE_SEEDLING, MAPLE_SEEDLING_W}, "maple tree"),
			new PaymentPair("10 cactus spines", new int[] {YEW_SEED, YEW_SAPLING, YEW_SEEDLING, YEW_SEEDLING_W}, "yew tree"),
			new PaymentPair("25 coconuts", new int[] {MAGIC_SEED, MAGIC_SAPLING, MAGIC_SEEDLING, MAGIC_SEEDLING_W}, "magic tree"),
			// FRUIT TREE PAIRS
			new PaymentPair("9 sweetcorn", new int[] {APPLE_TREE_SEED, APPLE_SAPLING, APPLE_SEEDLING, APPLE_SEEDLING_W}, "apple tree"),
			new PaymentPair("4 full baskets of apples", new int[] {BANANA_TREE_SEED, BANANA_SAPLING, BANANA_SEEDLING, BANANA_SEEDLING_W}, "banana tree"),
			new PaymentPair("3 full baskets of strawberries", new int[] {ORANGE_TREE_SEED, ORANGE_SAPLING, ORANGE_SEEDLING, ORANGE_SEEDLING_W}, "orange tree"),
			new PaymentPair("5 full baskets of bananas", new int[] {CURRY_TREE_SEED, CURRY_SAPLING, CURRY_SEEDLING, CURRY_SEEDLING_W}, "curry tree"),
			new PaymentPair("10 watermelons", new int[] {PINEAPPLE_SEED, PINEAPPLE_SAPLING, PINEAPPLE_SEEDLING, PINEAPPLE_SEEDLING_W}),
			new PaymentPair("10 pineapples", new int[] {PAPAYA_TREE_SEED, PAPAYA_SAPLING, PAPAYA_SEEDLING, PAPAYA_SEEDLING_W}, "papaya tree"),
			new PaymentPair("15 papaya fruit", new int[] {PALM_TREE_SEED, PALM_SAPLING, PALM_SEEDLING, PALM_SEEDLING_W}, "palm tree"),
			new PaymentPair("15 coconuts", new int[] {DRAGONFRUIT_TREE_SEED, DRAGONFRUIT_SAPLING, DRAGONFRUIT_SEEDLING, DRAGONFRUIT_SEEDLING_W}, "dragonfruit tree"),
			// SPECIAL PAIRS
			new PaymentPair("200 numulite", new int[] {SEAWEED_SPORE}),
			new CustomPair("as it is protected for free. That doesn't make sense but I'm too lazy to change it", new int[] {GRAPE_SEED}),
			new CustomPair(new int[] {MUSHROOM_SPORE, BELLADONNA_SEED}),
			new CustomPair("as it is immune to disease", new int[] {HESPORI_SEED}),
			// ANIMA PAIRS
			new CustomPair(new int[] {KRONOS_SEED, IASOR_SEED, ATTAS_SEED}),
			// SPECIAL TREE PAIRS
			new PaymentPair("15 limpwurt roots", new int[] {TEAK_SEED, TEAK_SAPLING, TEAK_SEEDLING, TEAK_SEEDLING_W}, "teak tree"),
			new PaymentPair("25 yanillian hops", new int[] {MAHOGANY_SEED, MAHOGANY_SAPLING, MAHOGANY_SEEDLING, MAHOGANY_SEEDLING_W}, "mahogany tree"),
			new PaymentPair("8 poison ivy berries", new int[] {CALQUAT_TREE_SEED, CALQUAT_SAPLING, CALQUAT_SEEDLING, CALQUAT_SEEDLING_W}, "calquat tree"),
			new CustomPair("as it is immune to disease", new int[] {CRYSTAL_SEED, CRYSTAL_SAPLING, CRYSTAL_SEEDLING, CRYSTAL_SEEDLING_W}),
			new PaymentPair("5 monkey nuts, 1 monkey bar, and 1 ground tooth", new int[] {SPIRIT_SEED, SPIRIT_SAPLING, SPIRIT_SEEDLING, SPIRIT_SEEDLING_W}, "spirit tree"),
			new PaymentPair("8 potato cacti", new int[] {CELASTRUS_SEED, CELASTRUS_SAPLING, CELASTRUS_SEEDLING, CELASTRUS_SEEDLING_W}, "celastrus tree"),
			new PaymentPair("6 dragonfruit", new int[] {REDWOOD_TREE_SEED, REDWOOD_SAPLING, REDWOOD_SEEDLING, REDWOOD_SEEDLING_W}, "redwood tree"),
			// CACTI PAIRS
			new PaymentPair("6 cadava berries", new int[] {CACTUS_SEED}),
			new PaymentPair("8 snape grass", new int[] {POTATO_CACTUS_SEED})
	};

	@Inject
	@Nullable
	private Client client;

	@Inject
	private Provider<MenuManager> menuManager;

	@Inject
	private PatchPaymentConfig config;

	@Provides
	PatchPaymentConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(PatchPaymentConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
		super.startUp();
	}

	@Override
	protected void shutDown() throws Exception {
		super.shutDown();
	}


	@Subscribe
	public void onMenuOpened(MenuOpened event) {
		if (!config.checkWithExamine()) {
			MenuEntry firstEntry = event.getFirstEntry();

			if (firstEntry == null)
				return;

			int widgetId = firstEntry.getParam1();

			if (widgetId == WidgetInfo.INVENTORY.getId()) {

				int itemId = firstEntry.getIdentifier();
				if (itemId == -1)
					return;

				for (PairInterface pp : paymentPairList) {
					if (pp.checkForId(itemId)) {
						ItemDefinition itemComposition = client.getItemDefinition(itemId);

						MenuEntry[] entries = event.getMenuEntries();

						final MenuEntry checkPaymentEntry = new MenuEntry();
						checkPaymentEntry.setOption(CHECK_PAYMENT);
						checkPaymentEntry.setTarget("<col=ff9040>" + itemComposition.getName());
						checkPaymentEntry.setIdentifier(itemId);
						checkPaymentEntry.setParam1(widgetId);
						checkPaymentEntry.setOpcode(MenuOpcode.CC_OP.getId());

						MenuEntry[] newEntries = ObjectArrays.concat(entries, checkPaymentEntry);
						int menuEntryCount = newEntries.length;
						ArrayUtils.swap(newEntries, menuEntryCount - 1, menuEntryCount - 2);
						client.setMenuEntries(newEntries);
					}
				}
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getOption().equals(CHECK_PAYMENT) ||
				(event.getOption().equals("Examine") && config.checkWithExamine() )) {
			ItemDefinition composition;
			ChatMessageType messageType = ChatMessageType.ITEM_EXAMINE; // message type check added for compatibility with ExamineTooltip plugin
			if (event.getParam1() == WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId() && config.checkInBank()) {
				composition = client.getItemDefinition(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER).getChild(event.getParam0()).getItemId());
				messageType = ChatMessageType.GAMEMESSAGE;
			} else if (event.getParam1() == WidgetInfo.BANK_ITEM_CONTAINER.getId() && config.checkInBank()) {
				composition = client.getItemDefinition(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER).getChild(event.getParam0()).getItemId());
				messageType = ChatMessageType.GAMEMESSAGE;
			} else
				composition = client.getItemDefinition(event.getIdentifier());

			for (PairInterface pp : paymentPairList) {
				if (pp.checkForId(composition.getId())) {
					String text = composition.getName();
					if (pp instanceof PaymentPair) {
						PaymentPair pair = (PaymentPair) pp;
						if (pair.getPreferredName() != null)
							text = pair.getPreferredName();
						client.addChatMessage(messageType, "", String.format("A farmer will watch over %s %s patch for %s.", grammatify(text), stripAndShrink(text), pair.getPayment()), null);
					} else if(pp instanceof CustomPair) {
						CustomPair pair = (CustomPair) pp;
						if (pair.preferredName != null)
							text = pair.getPreferredName();
						client.addChatMessage(messageType, "", String.format("%s %s patch can NOT be protected by a farmer%s.", grammatify(text).replace('a', 'A'), stripAndShrink(text), pair.getMessage()), null);
					}
				}
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (!config.checkWithExamine() && config.checkInBank()) {
			MenuEntry[] entries = client.getMenuEntries();
			if (event.getOption().equals("Examine")) {
				Widget container = null;
				Widget item = null;
				if (event.getParam1() == WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId()) {
					container = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
					item = container.getChild(event.getParam0());
				} else if (event.getParam1() == WidgetInfo.BANK_ITEM_CONTAINER.getId()) {
					container = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
					item = container.getChild(event.getParam0());
				}

				if (container != null && item != null) {
					for (PairInterface pp : paymentPairList) {
						if (pp.checkForId(item.getItemId())) {
							MenuEntry checkPaymentEntry = new MenuEntry();
							checkPaymentEntry.setParam0(event.getParam0());
							checkPaymentEntry.setParam1(event.getParam1());
							checkPaymentEntry.setTarget(event.getTarget());
							checkPaymentEntry.setOption(CHECK_PAYMENT);
							checkPaymentEntry.setOpcode(MenuOpcode.RUNELITE.getId());
							checkPaymentEntry.setIdentifier(event.getIdentifier());
							entries = Arrays.copyOf(entries, entries.length + 1);
							entries[entries.length - 1] = checkPaymentEntry;
							client.setMenuEntries(entries);
						}
					}
				}
			}
		}
	}

	public interface PairInterface {
		int[] pairedIDs = null;

		boolean checkForId(int id);
	}

	class PaymentPair implements PairInterface {
		String payment;
		int[] pairedIDs;
		String preferredName = null;

		public PaymentPair(String payment, int[] pairedIDs) {
			this.payment = payment;
			this.pairedIDs = pairedIDs;
		}

		public PaymentPair(String payment, int[] pairedIDs, String preferredName) {
			this(payment, pairedIDs);
			this.preferredName = preferredName;
		}

		public boolean checkForId(int id) {
			for (int check : pairedIDs) {
				if (check == id)
					return true;
			}
			return false;
		}

		public String getPayment() {
			return this.payment;
		}

		public String getPreferredName() {
			return preferredName;
		}
	}

	private class CustomPair implements PairInterface {

		String message = "";
		int[] pairedIDs;
		String preferredName = null;

		public CustomPair(int[] pairedIDs) {
			this.pairedIDs = pairedIDs;
		}

		public CustomPair(String message, int[] pairedIDs) {
			this.message = " " + message;
			this.pairedIDs = pairedIDs;
		}

		public CustomPair(String message, int[] pairedIDs, String preferredName) {
			this(message, pairedIDs);
			this.preferredName = preferredName;
		}

		public boolean checkForId(int id) {
			for (int check : pairedIDs) {
				if (check == id)
					return true;
			}
			return false;
		}

		public String getMessage() { return message; }

		public String getPreferredName() { return preferredName; }
	}

	private String stripAndShrink(String text) {
		return text
				.replace("seed", "")
				.replace("sapling", "")
				.replace("spore", "")
				.toLowerCase().trim();
	}

	private String grammatify(String text) {
		char[] vowels = {'a', 'e', 'i', 'o', 'u'};
		for (char vowel : vowels)
			if (text.toLowerCase().charAt(0) == vowel)
				return "an";
		return "a";
	}
}
