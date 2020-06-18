package net.runelite.client.plugins.analoguetimestamps;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.MessageNode;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Analogue Timestamps",
	description = "Add analogue timestamps to chat messages",
	tags = {"chat", "emoji", "hub"},
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class AnalogueTimestampsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ClientThread clientThread;

	private SimpleDateFormat formatter;
	private int modIconsStart = -1;

	@Override
	protected void startUp() throws Exception
	{
		updateFormatter();
		clientThread.invoke(this::loadAnalogueTimestampIcons);
	}

	@Override
	protected void shutDown() throws Exception
	{
		formatter = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			loadAnalogueTimestampIcons();
		}
	}

	private void loadAnalogueTimestampIcons()
	{
		final IndexedSprite[] modIcons = client.getModIcons();
		if (modIconsStart != -1 || modIcons == null)
		{
			return;
		}
		final AnalogueTimestamp[] analogueTimestamps = AnalogueTimestamp.values();
		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + analogueTimestamps.length);
		modIconsStart = modIcons.length;

		for (int i = 0; i < analogueTimestamps.length; i++)
		{
			final AnalogueTimestamp analogueTimestamp = analogueTimestamps[i];

			try
			{
				final BufferedImage image = analogueTimestamp.loadImage();
				final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
				newModIcons[modIconsStart + i] = sprite;
			}
			catch (Exception ex)
			{
				log.warn("Failed to load the sprite for Timestamp Analogue " + analogueTimestamp, ex);
			}
		}
		client.setModIcons(newModIcons);
	}

	String getTimestamp(final MessageNode messageNode)
	{
		final String filename = generateRoundedTimestamp(messageNode.getTimestamp(), ZoneId.systemDefault());
		final AnalogueTimestamp analogueTimestamp = AnalogueTimestamp.getAnalogueTimestamp(filename);

		final int analogueTimestampId = modIconsStart + analogueTimestamp.ordinal();
		return "<img=" + analogueTimestampId + ">";
	}

	String generateRoundedTimestamp(int timestamp, ZoneId zoneId)
	{
		final ZonedDateTime time = ZonedDateTime.ofInstant(
			Instant.ofEpochSecond(timestamp), zoneId);

		String stringTimestamp = formatter.format(Date.from(time.toInstant()));
		return String.valueOf(5 * ((Integer.parseInt(stringTimestamp)) / 5));
	}

	private void updateFormatter()
	{
		formatter = new SimpleDateFormat("hhmm");

	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!event.getEventName().equals("addTimestamp"))
		{
			return;
		}

		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();

		String[] stringStack = client.getStringStack();
		int stringStackSize = client.getStringStackSize();

		int messageId = intStack[intStackSize - 1];

		MessageNode messageNode = client.getMessages().get(messageId);

		String timestamp = getTimestamp(messageNode);

		stringStack[stringStackSize - 1] += timestamp;
	}
}