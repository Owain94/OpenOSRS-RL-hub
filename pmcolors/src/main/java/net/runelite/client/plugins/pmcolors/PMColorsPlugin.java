/*
 * Copyright (c) 2020, PresNL
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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

package net.runelite.client.plugins.pmcolors;

import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.PlayerMenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.pmcolors.ui.PMColorsPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "PM Colors",
	description = "Allows you to highlight certain users private messages in specified colors",
	tags = {"private", "message", "chat", "highlight"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class PMColorsPlugin extends Plugin
{
	private static final String PLUGIN_NAME = "PM Colors";

	private static final String CONFIG_GROUP = "pmcolors";
	private static final String CONFIG_KEY = "highlightedplayers";

	private static final Pattern LOGGED_PATTERN = Pattern.compile(
		"(.*?) has logged (out|in)");

	@Inject
	private Client client;

	@Inject
	private PMColorsConfig config;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	@Getter
	@Inject
	private ColorPickerManager colorPickerManager;

	private static final List<String> AFTER_OPTIONS = List.of("Message", "Lookup");

	private static final String HIGHLIGHT = "Highlight";
	private static final String REMOVE_HIGHLIGHT = "Remove highlight";

	//private static final ImmutableSet<Integer> HIGHLIGHT_SCRIPT IDS = new Immutabl

	private String selectedPlayer = null;

	private PMColorsPanel pluginPanel;

	private NavigationButton navigationButton;

	@Getter
	private final List<PlayerHighlight> highlightedPlayers = new ArrayList<>();

	@Override
	protected void startUp() throws Exception
	{
		selectedPlayer = null;

		loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY)).forEach(highlightedPlayers::add);

		pluginPanel = new PMColorsPanel(this);
		pluginPanel.rebuild();

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "icon_marker.png");

		navigationButton = NavigationButton.builder()
			.tooltip(PLUGIN_NAME)
			.icon(icon)
			.priority(5)
			.panel(pluginPanel)
			.build();

		clientToolbar.addNavigation(navigationButton);

	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navigationButton);

		highlightedPlayers.clear();
		pluginPanel = null;

		navigationButton = null;

		selectedPlayer = null;
	}

	@Provides
	PMColorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PMColorsConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (highlightedPlayers.isEmpty() && event.getGroup().equals(CONFIG_GROUP) && event.getKey().equals(CONFIG_KEY))
		{
			loadConfig(event.getNewValue()).forEach(highlightedPlayers::add);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		ChatMessageType messageType = chatMessage.getType();
		if (messageType == ChatMessageType.PRIVATECHAT || messageType == ChatMessageType.PRIVATECHATOUT)
		{
			String name = Text.removeTags(Text.toJagexName(chatMessage.getName()));
			PlayerHighlight player = highlightedPlayers.stream()
				.filter(p -> name.equalsIgnoreCase(p.getName()))
				.findAny()
				.orElse(null);

			if (player != null)
			{
				if (player.isHighlightMessage())
				{
					MessageNode messageNode = chatMessage.getMessageNode();
					messageNode.setValue(ColorUtil.wrapWithColorTag(
						messageNode.getValue().replace(ColorUtil.CLOSING_COLOR_TAG, ColorUtil.colorTag(player.getColor())),
						player.getColor()));
				}
			}
		}
		else if (messageType == ChatMessageType.LOGINLOGOUTNOTIFICATION)
		{
			Matcher loggedMatcher = LOGGED_PATTERN.matcher(chatMessage.getMessage());
			if (loggedMatcher.find())
			{
				String name = Text.removeTags(Text.toJagexName(loggedMatcher.group(1)));
				PlayerHighlight player = highlightedPlayers.stream()
					.filter(p -> name.equalsIgnoreCase(p.getName()))
					.findAny()
					.orElse(null);

				if (player != null)
				{
					if (player.isHighlightLoggedInOut())
					{
						MessageNode messageNode = chatMessage.getMessageNode();
						messageNode.setValue(ColorUtil.wrapWithColorTag(
							messageNode.getValue().replace(ColorUtil.CLOSING_COLOR_TAG, ColorUtil.colorTag(player.getColor())),
							player.getColor()));
					}
				}
			}
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
	{
		final String eventName = scriptCallbackEvent.getEventName();

		switch (eventName)
		{
			case "privateChatFrom":
			case "privateChatTo":
			case "privateChatSplitFrom":
			case "privateChatSplitTo":
				break;
			default:
				return;
		}

		//boolean isChatboxTransparent = client.isResized() && client.getVar(Varbits.TRANSPARENT_CHATBOX) == 1;
		//Color usernameColor = isChatboxTransparent ? chatColorConfig.transparentPrivateUsernames() : chatColorConfig.opaquePrivateUsernames();
		//if (usernameColor == null)
		//{
		//	return;
		//}

		final String[] stringStack = client.getStringStack();
		final int stringStackSize = client.getStringStackSize();


		// Stack is: To/From playername :
		String name = Text.removeTags(Text.toJagexName(stringStack[2]));
		PlayerHighlight player = highlightedPlayers.stream()
			.filter(p -> name.equalsIgnoreCase(p.getName()))
			.findAny()
			.orElse(null);

		if (player != null)
		{
			if (player.isHighlightUsername())
			{
				String toFrom = stringStack[stringStackSize - 3];
				stringStack[stringStackSize - 3] = ColorUtil.prependColorTag(toFrom, player.getColor());
			}
		}
	}

	// adapted from the hiscore plugin
	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final int groupId = WidgetInfo.TO_GROUP(event.getParam1());

		// Look for "Message" on friends list
		if (groupId == WidgetInfo.PRIVATE_CHAT_MESSAGE.getGroupId())
		{
			//// Friends have color tagsString option = event.getOption();
			selectedPlayer = Text.toJagexName(Text.removeTags(event.getTarget()));
			if (!AFTER_OPTIONS.contains(event.getOption()))
			{
				return;
			}

			// Build "Add Note" or "Edit Note" menu entry
			final MenuEntry highlight = new MenuEntry();
			PlayerHighlight player = highlightedPlayers.stream()
				.filter(p -> selectedPlayer.equalsIgnoreCase(p.getName()))
				.findAny()
				.orElse(null);

			if (player == null)
			{
				highlight.setOption(HIGHLIGHT);
			}
			else
			{
				highlight.setOption(REMOVE_HIGHLIGHT);
			}
			highlight.setOpcode(MenuOpcode.RUNELITE.getId());
			highlight.setTarget(event.getTarget()); //Preserve color codes here
			highlight.setParam0(event.getParam0());
			highlight.setParam1(event.getParam1());
			highlight.setIdentifier(event.getIdentifier());
			// Add menu entry
			insertMenuEntry(highlight, client.getMenuEntries());
		}
		else
		{
			selectedPlayer = null;
		}
	}

	@Subscribe
	public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event)
	{
		if (event.getMenuOption().equals(HIGHLIGHT))
		{
			finishCreation(false, selectedPlayer, config.defaultHighlightColor());
		}
		else if (event.getMenuOption().equals(REMOVE_HIGHLIGHT))
		{
			PlayerHighlight player = highlightedPlayers.stream()
				.filter(p -> selectedPlayer.equalsIgnoreCase(p.getName()))
				.findAny()
				.orElse(null);
			if (player != null)
			{
				deleteHighlight(player);
			}
		}
		selectedPlayer = null;
	}

	// adapted from the hiscore plugin
	private void insertMenuEntry(MenuEntry newEntry, MenuEntry[] entries)
	{
		MenuEntry[] newMenu = ObjectArrays.concat(entries, newEntry);
		int menuEntryCount = newMenu.length;
		ArrayUtils.swap(newMenu, menuEntryCount - 1, menuEntryCount - 2);
		client.setMenuEntries(newMenu);
	}

	public Color getDefaultColor()
	{
		return config.defaultHighlightColor();
	}

	public void finishCreation(boolean aborted, String name, Color color)
	{
		if (!aborted && name != null && color != null)
		{
			PlayerHighlight highlight = new PlayerHighlight();
			highlight.setName(Text.toJagexName(name));
			highlight.setColor(color);
			highlight.setHighlightUsername(config.highlightUsernameDefault());
			highlight.setHighlightMessage(config.highlightMessageDefault());
			highlight.setHighlightLoggedInOut(config.highlightLoggedInOutDefault());
			highlightedPlayers.add(highlight);

			SwingUtilities.invokeLater(() -> pluginPanel.rebuild());
			updateConfig();
		}

		pluginPanel.setCreation(false);
	}

	public void deleteHighlight(final PlayerHighlight highlight)
	{
		highlightedPlayers.remove(highlight);
		updateConfig();
		SwingUtilities.invokeLater(() -> pluginPanel.rebuild());

	}

	public void updateConfig()
	{
		if (highlightedPlayers.isEmpty())
		{
			configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
			return;
		}

		final Gson gson = new Gson();
		final String json = gson
			.toJson(highlightedPlayers.stream().collect(Collectors.toList()));
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
	}

	private Stream<PlayerHighlight> loadConfig(String json)
	{
		if (Strings.isNullOrEmpty(json))
		{
			return Stream.empty();
		}

		final Gson gson = new Gson();
		final List<PlayerHighlight> playerHiglightData = gson.fromJson(json, new TypeToken<ArrayList<PlayerHighlight>>()
		{
		}.getType());

		return playerHiglightData.stream().filter(Objects::nonNull);
	}
}