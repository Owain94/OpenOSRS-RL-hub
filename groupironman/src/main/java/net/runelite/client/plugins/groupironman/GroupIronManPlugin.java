package net.runelite.client.plugins.groupironman;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Group Iron Man",
	description = "Adds the capability to play Group Ironman"
)
public class GroupIronManPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private GroupIronManConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Group Ironman started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Group Ironman stopped!");
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		List<String> players = Arrays.stream(config.groupMembers().split("\\n")).map(Text::standardize).collect(Collectors.toList());
		String target = Text.removeTags(event.getTarget());

		if (entryMatches(event, "Trade with") && !players.contains(cleanName(event.getTarget())))
		{
			// Scold the player for attempting to trade with a non-group member
			event.consume();
			sendChatMessage("You are a Group Iron Man. You stand alone...together.");
			return;
		}

		if (entryMatches(event, "Challenge") && !players.contains(cleanName(event.getTarget())))
		{
			event.consume();
			sendChatMessage("As a Group Iron Man, you cannot duel players outside your group.");
			return;
		}

		if (entryMatches(event, "Exchange", "Grand Exchange Clerk")
			|| entryMatches(event, "Exchange", "Grand Exchange booth")
			|| entryMatches(event, "Collect", "Banker")
			|| entryMatches(event, "Collect", "Grand Exchange booth"))
		{
			// Scold the player for attempting to use the grand exchange
			event.consume();
			sendChatMessage("As a Group Iron Man, you cannot use the Grand Exchange.");
			return;
		}

		if (entryMatches(event, "Burst", "Party Balloon"))
		{
			event.consume();
			sendChatMessage("As a Group Iron Man, you cannot receive items from the Falador Party Room");
			return;
		}

		if (isNMZStoreOpen() && event.getOption().startsWith("Buy-"))
		{
			if (!target.equals("Scroll of redirection") && !target.endsWith("(1)"))
			{
				event.consume();
				sendChatMessage("As a Group Iron Man, you cannot purchase this item.");
				return;
			}
		}

		if (config.ultimate())
		{
			if (entryMatches(event, "Bank")
				|| entryMatches(event, "Use", "Bank chest")
				|| entryMatches(event, "Deposit", "Bank deposit box"))
			{
				event.consume();
				sendChatMessage("As an Ultimate Group Iron Man, you cannot use the bank.");
			}
			if (entryMatches(event, "Sets", "Grand Exchange Clerk"))
			{
				event.consume();
				sendChatMessage("As an Ultimate Group Iron Man, you cannot create item sets.");
			}
			if (entryMatches(event, "Sand", "Bert"))
			{
				event.consume();
				sendChatMessage("As an Ultimate Group Iron Man, you cannot receive sand from Bert.");
			}
			if (entryMatches(event, "Collect", "Advisor Ghrim"))
			{
				event.consume();
				sendChatMessage("As an Ultimate Group Iron Man, you cannot receive resources from Managing Miscellania.");
			}
		}
	}

	@Subscribe
	private void onMenuOpened(MenuOpened event)
	{
		MenuEntry[] menuEntries = event.getMenuEntries();
		List<MenuEntry> newEntries = new ArrayList<>(menuEntries.length);

		for (MenuEntry entry : event.getMenuEntries())
		{
			String option = entry.getOption();
			String target = Text.removeTags(entry.getTarget());
			if (option.equals("Talk-to") && (target.equals("Grand Exchange Clerk") || target.equals("Banker")))
			{
				continue;
			}
			if (entryMatches(entry, "Open", "Seed vault"))
			{
				continue;
			}
			if (config.ultimate())
			{
				if (entryMatches(entry, "Talk-to", "Bert") && Quest.THE_HAND_IN_THE_SAND.getState(client) == QuestState.FINISHED)
				{
					continue;
				}
				if (entryMatches(entry, "Talk-to", "Advisor Ghrim") && Quest.THRONE_OF_MISCELLANIA.getState(client) == QuestState.FINISHED)
				{
					continue;
				}
			}

			newEntries.add(entry);
		}

		client.setMenuEntries(newEntries.toArray(new MenuEntry[0]));
	}

	private boolean isNMZStoreOpen()
	{
		Widget w = client.getWidget(206, 1);
		if (w == null || w.isHidden())
		{
			return false;
		}
		Widget child = w.getChild(1);
		if (child == null || child.isHidden())
		{
			return false;
		}
		return child.getText().equals("Dom Onion's Reward Shop");
	}

	private String cleanName(String playerName)
	{
		playerName = Text.standardize(playerName);
		int index = playerName.indexOf('(');
		if (index == -1)
		{
			return playerName.trim();
		}
		return playerName.substring(0, index).trim();
	}

	private boolean entryMatches(MenuEntry entry, String option)
	{
		return entry.getOption().equals(option);
	}

	private boolean entryMatches(MenuOptionClicked event, String option)
	{
		return event.getOption().equals(option);
	}

	private boolean entryMatches(MenuEntry entry, String option, String target)
	{
		return entryMatches(entry, option) && Text.removeTags(entry.getTarget()).equals(target);
	}

	private boolean entryMatches(MenuOptionClicked event, String option, String target)
	{
		return entryMatches(event, option) && Text.removeTags(event.getTarget()).equals(target);
	}

	private void sendChatMessage(String message)
	{
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.CONSOLE)
			.runeLiteFormattedMessage(message)
			.build());
	}

	@Provides
	GroupIronManConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GroupIronManConfig.class);
	}
}