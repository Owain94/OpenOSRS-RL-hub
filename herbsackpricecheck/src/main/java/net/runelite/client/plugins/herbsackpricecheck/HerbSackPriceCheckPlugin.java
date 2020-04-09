package net.runelite.client.plugins.herbsackpricecheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import static net.runelite.api.ItemID.HERB_SACK;
import static net.runelite.api.ItemID.OPEN_HERB_SACK;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.http.api.item.ItemPrice;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Herb Sack Price Check",
	description = "Price checks the herbs in herb sack",
	tags = {"herbs", "prices"},
	type = PluginType.MISCELLANEOUS
)
public class HerbSackPriceCheckPlugin extends Plugin
{
	private boolean gettingHerbs = false;
	private List<ChatMessage> herbsInChatMessage = new ArrayList<>();

	@Inject
	private ItemManager itemManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!event.getOption().equals("Check") || (event.getIdentifier() != OPEN_HERB_SACK && event.getIdentifier() != HERB_SACK))
		{
			return;
		}
		gettingHerbs = true;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (!gettingHerbs || chatMessage.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		String messageString = chatMessage.getMessage();
		if (messageString.contains("x Grimy"))
		{
			herbsInChatMessage.add(chatMessage);
		}

		if (messageString.equals("The herb sack is empty."))
		{
			herbsInChatMessage.clear();
			gettingHerbs = false;
		}
	}

	private HashMap<String, Integer> reformatHerbChatMessages()
	{
		HashMap<String, Integer> herbsWithQuantity = new HashMap<>();

		for (ChatMessage message : herbsInChatMessage)
		{
			String[] fullHerbName = message.getMessage().split(" x ");
			if (fullHerbName.length == 2)
			{
				herbsWithQuantity.put(fullHerbName[1].trim(), Integer.parseInt(fullHerbName[0].trim(), 10));
			}
		}

		return herbsWithQuantity;
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (gettingHerbs && !herbsInChatMessage.isEmpty())
		{
			HashMap<String, Integer> herbsWithQuantity = reformatHerbChatMessages();
			int totalValue = herbPriceLookup(herbsWithQuantity);

			buildValueMessage(totalValue);

			gettingHerbs = false;
			herbsInChatMessage = new ArrayList<>();
		}
	}

	private int herbPriceLookup(HashMap<String, Integer> herbsWithQuantity)
	{
		int totalValue = 0;
		for (HashMap.Entry<String, Integer> herbQuant : herbsWithQuantity.entrySet())
		{
			List<ItemPrice> results = itemManager.search(herbQuant.getKey());

			if (results != null && !results.isEmpty())
			{
				for (ItemPrice result : results)
				{
					totalValue += result.getPrice() * herbQuant.getValue();
				}
			}
		}
		return totalValue;
	}

	private void buildValueMessage(int totalValue)
	{
		final ChatMessageBuilder output = new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append("Total value of herbs in sack: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(QuantityFormatter.formatNumber(totalValue));

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.ITEM_EXAMINE)
			.runeLiteFormattedMessage(output.build())
			.build());
	}
}