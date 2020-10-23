package com.thenorsepantheon.groupironman;

import com.google.common.base.Splitter;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.MenuEntry;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Group Iron Man",
	description = "Adds the capability to play Group Ironman",
	enabledByDefault = false,
	type = PluginType.GAMEMODE
)
public class GroupIronManPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private GroupIronManConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	private static final List<String> groupMembers = new ArrayList<>();
	private static final Map<Boolean, Integer> iconMap = new HashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		log.info("Group Ironman started!");

		populateGroupMembers();

		if (client.getModIcons() == null)
		{
			iconMap.clear();
			return;
		}

		loadSprites();
	}

	@Override
	protected void shutDown() throws Exception
	{
		iconMap.clear();

		clientThread.invoke(() -> client.runScript(ScriptID.CHAT_PROMPT_INIT));

		log.info("Group Ironman stopped!");
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		loadSprites();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		String target = Text.removeTags(event.getTarget());

		if ((entryMatches(event, "Trade with") || entryMatches(event, "Accept trade")) && !isGroupMember(event.getTarget()))
		{
			// Scold the player for attempting to trade with a non-group member
			event.consume();
			sendChatMessage("You are a Group Iron Man. You stand alone...together.");
			return;
		}

		if (entryMatches(event, "Challenge") && !isGroupMember(event.getTarget()))
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

			if (isGroupMember(entry.getTarget()))
			{
				entry.setTarget(getImgTag() + entry.getTarget());
			}

			newEntries.add(entry);
		}

		client.setMenuEntries(newEntries.toArray(new MenuEntry[0]));
	}

	@Subscribe
	private void onBeforeRender(BeforeRender event)
	{
		updateChatbox();
	}

	@Subscribe
	private void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!event.getEventName().equals("setChatboxInput"))
		{
			return;
		}

		updateChatbox();
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		if (event.getName() == null || client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null)
		{
			return;
		}

		boolean isLocalPlayer = Text.standardize(event.getName()).equalsIgnoreCase(Text.standardize(client.getLocalPlayer().getName()));

		if (isLocalPlayer || isGroupMember(event.getName().toLowerCase()))
		{
			event.getMessageNode().setName(getImgTag() + Text.removeTags(event.getName()));
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("groupironman"))
		{
			if (event.getKey().equals("groupMembers"))
			{
				populateGroupMembers();
			}
			else if (event.getKey().equals("iconHueOffset"))
			{
				loadSprites();
			}
		}
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
		int index = playerName.indexOf('(');
		if (index == -1)
		{
			return standardizeToJagexName(playerName);
		}
		return standardizeToJagexName(playerName.substring(0, index));
	}

	private String standardizeToJagexName(String name)
	{
		return Text.standardize(Text.toJagexName(name));
	}

	private boolean isGroupMember(String name)
	{
		return groupMembers.contains(cleanName(name));
	}

	private void populateGroupMembers()
	{
		Splitter NEWLINE_SPLITTER = Splitter.on("\n").omitEmptyStrings().trimResults();

		groupMembers.clear();
		groupMembers.addAll(
			NEWLINE_SPLITTER.splitToList(config.groupMembers())
				.stream().map(this::standardizeToJagexName).collect(Collectors.toList()));
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

	private void loadSprites()
	{
		iconMap.clear();

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(GroupIronManPlugin.class, "icon.png");

		float hueOffset = (float) config.iconHueOffset() / 100;

		IndexedSprite GROUP_IRON_MAN_ICON = ImageUtil.getImageIndexedSprite(adjustHue(icon, hueOffset), client);
		IndexedSprite ULTIMATE_GROUP_IRON_MAN_ICON = ImageUtil.getImageIndexedSprite(adjustHue(icon, hueOffset + .11f), client);


		IndexedSprite[] modIcons = client.getModIcons();
		IndexedSprite[] newArray = Arrays.copyOf(modIcons, modIcons.length + 2);
		int modIconsStart = modIcons.length - 1;

		newArray[++modIconsStart] = GROUP_IRON_MAN_ICON;
		iconMap.put(false, modIconsStart);
		newArray[++modIconsStart] = ULTIMATE_GROUP_IRON_MAN_ICON;
		iconMap.put(true, modIconsStart);

		client.setModIcons(newArray);
	}

	private void updateChatbox()
	{
		Widget chatboxTypedText = client.getWidget(WidgetInfo.CHATBOX_MESSAGE_LINES);

		if (getIconId() == -1)
		{
			return;
		}

		if (chatboxTypedText == null || chatboxTypedText.isHidden())
		{
			return;
		}

		String[] chatbox = chatboxTypedText.getText().split(":", 2);
		String rsn = Objects.requireNonNull(client.getLocalPlayer()).getName();

		chatboxTypedText.setText(getImgTag() + Text.removeTags(rsn) + ":" + chatbox[1]);
	}

	private BufferedImage adjustHue(final BufferedImage image, float hue)
	{
		final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < newImage.getWidth(); x++)
		{
			for (int y = 0; y < newImage.getHeight(); y++)
			{
				final Color pixelColour = new Color(image.getRGB(x, y), true);
				if (pixelColour.getAlpha() == 0)
				{
					newImage.setRGB(x, y, pixelColour.getRGB());
					continue;
				}
				float[] hsbVals = new float[3];
				Color.RGBtoHSB(pixelColour.getRed(), pixelColour.getBlue(), pixelColour.getGreen(), hsbVals);
				Color newColour = new Color(Color.HSBtoRGB((hue + hsbVals[0]) % 1, hsbVals[1], hsbVals[2]));

				newImage.setRGB(x, y, newColour.getRGB());
			}
		}

		return newImage;
	}

	private String getImgTag()
	{
		return "<img=" + getIconId() + "> ";
	}

	private int getIconId()
	{
		return iconMap.getOrDefault(config.ultimate(), -1);
	}

	@Provides
	GroupIronManConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GroupIronManConfig.class);
	}
}
