/*
 * Copyright (c) 2020, Hannah Ryan <HannahRyanster@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.emojiscape;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import com.google.inject.Provides;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.config.ConfigManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "EmojiScape",
	description = "Adds Runescape icons to chat",
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class EmojiScapePlugin extends Plugin
{
	private static final Pattern TAG_REGEXP = Pattern.compile("<[^>]*>");
	private static final Pattern WHITESPACE_REGEXP = Pattern.compile("[\\s\\u00A0]");
	private static final Pattern SLASH_REGEXP = Pattern.compile("[\\/]");
	private static final Pattern PUNCTUATION_REGEXP = Pattern.compile("[\\W\\_\\d]");

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private EmojiScapeConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	private int modIconsStart = -1;

	@Override
	protected void startUp()
	{
		clientThread.invokeLater(this::loadRSEmojiIcons);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			loadRSEmojiIcons();
		}
	}

	private void loadRSEmojiIcons()
	{
		final IndexedSprite[] modIcons = client.getModIcons();
		if (modIconsStart != -1 || modIcons == null)
		{
			return;
		}

		final RSEmoji[] RSEmojis = RSEmoji.values();
		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + RSEmojis.length);
		modIconsStart = modIcons.length;

		for (int i = 0; i < RSEmojis.length; i++)
		{
			final RSEmoji RSEmoji = RSEmojis[i];

			try
			{
				final BufferedImage image = RSEmoji.loadImage();
				final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
				newModIcons[modIconsStart + i] = sprite;
			}
			catch (Exception ex)
			{
				log.warn("Failed to load the sprite for RSEmoji " + RSEmoji, ex);
			}
		}

		log.debug("Adding RSEmoji icons");
		client.setModIcons(newModIcons);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (client.getGameState() != GameState.LOGGED_IN || modIconsStart == -1)
		{
			return;
		}

		switch (chatMessage.getType())
		{
			case PUBLICCHAT:
			case MODCHAT:
			case FRIENDSCHAT:
			case PRIVATECHAT:
			case PRIVATECHATOUT:
			case MODPRIVATECHAT:
				break;
			default:
				return;
		}

		final MessageNode messageNode = chatMessage.getMessageNode();
		final String message = messageNode.getValue();
		final String updatedMessage = updateMessage(message);

		if (updatedMessage == null)
		{
			return;
		}

		messageNode.setRuneLiteFormatMessage(updatedMessage);
		chatMessageManager.update(messageNode);
		client.refreshChat();
	}

	@Subscribe
	public void onOverheadTextChanged(final OverheadTextChanged event)
	{
		if (!(event.getActor() instanceof Player))
		{
			return;
		}

		final String message = event.getOverheadText();
		final String updatedMessage = updateMessage(message);

		if (updatedMessage == null)
		{
			return;
		}

		event.getActor().setOverheadText(updatedMessage);
	}

	@Nullable
	String updateMessage(final String message)
	{
		final String[] slashWords = SLASH_REGEXP.split(message);
		boolean editedMessage = false;
		for (int s = 0; s < slashWords.length; s++)
		{
			final String[] messageWords = WHITESPACE_REGEXP.split(slashWords[s]);

			for (int i = 0; i < messageWords.length; i++)
			{
				boolean longTriggerUsed = false;
				//Remove tags except for <lt> and <gt>
				final String pretrigger = removeTags(messageWords[i]);
				final Matcher matcherTrigger = PUNCTUATION_REGEXP.matcher(pretrigger);
				final String trigger = matcherTrigger.replaceAll("");
				final String shortTrigger = trigger;
				final RSEmoji rsEmoji = RSEmoji.getRSEmoji(trigger.toLowerCase());
				final RSEmoji rsShortEmoji = RSEmoji.getShortRSEmoji(shortTrigger.toLowerCase());

				if (rsEmoji == null && rsShortEmoji == null)
				{
					continue;
				}

				boolean skillLong = false;
				boolean skillShort = false;
				boolean miscLong = false;
				boolean miscShort = false;

				switch (config.skillIcons())
				{
					case LONG:
						skillLong = true;
						break;
					case SHORT:
						skillShort = true;
						break;
					case BOTH:
						skillLong = true;
						skillShort = true;
						break;
				}

				switch (config.miscIcons())
				{
					case LONG:
						miscLong = true;
						break;
					case SHORT:
						miscShort = true;
						break;
					case BOTH:
						miscLong = true;
						miscShort = true;
						break;
				}

				if (rsEmoji != null)
				{
					final int rsEmojiId = modIconsStart + rsEmoji.ordinal();
					if (skillLong && rsEmoji.ordinal() <= 24)
					{
						if (config.swapIconMode() == IconMode.REPLACE)
						{
							messageWords[i] = messageWords[i].replace(trigger, "<img=" + rsEmojiId + ">");
						}
						else if (config.swapIconMode() == IconMode.APPEND)
						{
							messageWords[i] = messageWords[i].replace(trigger, trigger + "(<img=" + rsEmojiId + ">)");
						}
					}
					if (config.prayerIcons() && 25 <= rsEmoji.ordinal() && rsEmoji.ordinal() <= 32)
					{
						if (config.swapIconMode() == IconMode.REPLACE)
						{
							messageWords[i] = messageWords[i].replace(trigger, "<img=" + rsEmojiId + ">");
						}
						else if (config.swapIconMode() == IconMode.APPEND)
						{
							messageWords[i] = messageWords[i].replace(trigger, trigger + "(<img=" + rsEmojiId + ">)");
						}
					}
					if (miscLong && 33 <= rsEmoji.ordinal() && rsEmoji.ordinal() <= 51)
					{
						if (config.swapIconMode() == IconMode.REPLACE)
						{
							messageWords[i] = messageWords[i].replace(trigger, "<img=" + rsEmojiId + ">");
						}
						else if (config.swapIconMode() == IconMode.APPEND)
						{
							messageWords[i] = messageWords[i].replace(trigger, trigger + "(<img=" + rsEmojiId + ">)");
						}
					}
					longTriggerUsed = true;
				}

				if (rsShortEmoji != null && !longTriggerUsed)
				{
					final int rsShortEmojiId = modIconsStart + rsShortEmoji.ordinal();
					if (skillShort && rsShortEmoji.ordinal() <= 24)
					{
						if (config.swapIconMode() == IconMode.REPLACE)
						{
							messageWords[i] = messageWords[i].replace(shortTrigger, "<img=" + rsShortEmojiId + ">");
						}
						else if (config.swapIconMode() == IconMode.APPEND)
						{
							messageWords[i] = messageWords[i].replace(shortTrigger, shortTrigger + "(<img=" + rsShortEmojiId + ">)");
						}
					}
					if (config.prayerIcons() && 25 <= rsShortEmoji.ordinal() && rsShortEmoji.ordinal() <= 32)
					{
						if (config.swapIconMode() == IconMode.REPLACE)
						{
							messageWords[i] = messageWords[i].replace(trigger, "<img=" + rsShortEmojiId + ">");
						}
						else if (config.swapIconMode() == IconMode.APPEND)
						{
							messageWords[i] = messageWords[i].replace(trigger, trigger + "(<img=" + rsShortEmojiId + ">)");
						}
					}
					if (miscShort && 33 <= rsShortEmoji.ordinal() && rsShortEmoji.ordinal() <= 51)
					{
						if (config.swapIconMode() == IconMode.REPLACE)
						{
							messageWords[i] = messageWords[i].replace(trigger, "<img=" + rsShortEmojiId + ">");
						}
						else if (config.swapIconMode() == IconMode.APPEND)
						{
							messageWords[i] = messageWords[i].replace(trigger, trigger + "(<img=" + rsShortEmojiId + ">)");
						}
					}
				}
				editedMessage = true;
			}
			slashWords[s] = Strings.join(messageWords, " ");
		}

		if (!editedMessage)
		{
			return null;
		}

		return Strings.join(slashWords, "/");
	}

	/**
	 * Remove tags, except for &lt;lt&gt; and &lt;gt&gt;
	 *
	 * @return
	 */
	private static String removeTags(String str)
	{
		StringBuilder stringBuilder = new StringBuilder();
		Matcher matcher = TAG_REGEXP.matcher(str);
		while (matcher.find())
		{
			matcher.appendReplacement(stringBuilder, "");
			String match = matcher.group(0);
			switch (match)
			{
				case "<lt>":
				case "<gt>":
					stringBuilder.append(match);
					break;
			}
		}
		matcher.appendTail(stringBuilder);
		return stringBuilder.toString();
	}

	@Provides
	EmojiScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EmojiScapeConfig.class);
	}
}