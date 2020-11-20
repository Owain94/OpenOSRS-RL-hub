package com.thatgamerblue.runelite.plugins.fakeiron;

import com.google.common.base.Splitter;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IconID;
import net.runelite.api.IndexedSprite;
import net.runelite.api.ScriptID;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Fake Ironman Icon",
	description = "Shows a fake ironman icon next to your name. Stripped down version of what Skiddler, Ron, Purpp and EVScape have",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class FakeIronPlugin extends Plugin
{
	@Inject
	@Getter
	private Client client;
	@Inject
	private ClientThread clientThread;
	private HashMap<FakeIronIcons, Integer> iconIds = new HashMap<>();
	@Getter
	private static List<String> players = new ArrayList<>();
	private FakeIronIcons selectedIcon = null;
	@Inject
	@Getter
	private FakeIronConfig pluginConfig;
	private static final Splitter NEWLINE_SPLITTER = Splitter
		.on("\n")
		.omitEmptyStrings()
		.trimResults();

	private void updateSelectedIcon()
	{
		if (selectedIcon != pluginConfig.icon())
		{
			selectedIcon = pluginConfig.icon();
		}
	}

	@Provides
	FakeIronConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FakeIronConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("fakeiron"))
		{
			if (pluginConfig.icon().isHeader())
			{
				pluginConfig.icon(FakeIronIcons.valueOf(event.getOldValue()));
				return;
			}

			clientThread.invoke(() -> client.runScript(ScriptID.CHAT_PROMPT_INIT));
			players = NEWLINE_SPLITTER.splitToList(pluginConfig.otherPlayers().toLowerCase());
			updateSelectedIcon();
		}
	}

	@Override
	public void startUp()
	{
		updateSelectedIcon();

		if (client.getModIcons() == null)
		{
			iconIds.clear();
			return;
		}

		loadSprites();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		updateSelectedIcon();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		updateSelectedIcon();

		if (event.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if ((Stream.of(FakeIronIcons.values())
				 .noneMatch((icon) -> (!icon.getImagePath().equals("")) && iconIds.getOrDefault(icon, -1) == -1)))
		{
			return;
		}

		loadSprites();
	}

	private void loadSprites()
	{
		clientThread.invoke(() -> {
			IndexedSprite[] modIcons = client.getModIcons();
			IndexedSprite[] newAry = Arrays.copyOf(modIcons, Math.toIntExact(
				modIcons.length +
					Stream.of(FakeIronIcons.values()).filter(icon -> !icon.getImagePath().equals("")).count()));
			int modIconsStart = modIcons.length - 1;

			iconIds.put(FakeIronIcons.IRONMAN, IconID.IRONMAN.getIndex());
			iconIds.put(FakeIronIcons.ULTIMATE, IconID.ULTIMATE_IRONMAN.getIndex());
			iconIds.put(FakeIronIcons.HCIM, IconID.HARDCORE_IRONMAN.getIndex());

			for (FakeIronIcons icon : FakeIronIcons.values())
			{
				if (icon.getImagePath().equals(""))
				{
					continue;
				}

				final IndexedSprite sprite = getIndexedSprite(icon.getImagePath());
				newAry[++modIconsStart] = sprite;
				iconIds.put(icon, modIconsStart);
			}

			client.setModIcons(newAry);
		});
	}

	@Override
	public void shutDown()
	{
		iconIds.clear();

		clientThread.invoke(() -> client.runScript(ScriptID.CHAT_PROMPT_INIT));
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getName() == null || client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null)
		{
			return;
		}

		boolean isLocalPlayer =
			Text.standardize(event.getName()).equalsIgnoreCase(Text.standardize(client.getLocalPlayer().getName()));

		if (isLocalPlayer || players.contains(Text.standardize(event.getName().toLowerCase())))
		{
			event.getMessageNode().setName(
				getImgTag(iconIds.getOrDefault(selectedIcon, IconID.NO_ENTRY.getIndex())) +
					Text.removeTags(event.getName()));
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!event.getEventName().equals("setChatboxInput"))
		{
			return;
		}

		updateChatbox();
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		updateChatbox(); // this stops flickering when typing
	}

	private void updateChatbox()
	{
		Widget chatboxTypedText = client.getWidget(WidgetInfo.CHATBOX_INPUT);

		if (getIconIdx() == -1)
		{
			return;
		}

		if (chatboxTypedText == null || chatboxTypedText.isHidden())
		{
			return;
		}

		String[] chatbox = chatboxTypedText.getText().split(":", 2);
		String rsn = Objects.requireNonNull(client.getLocalPlayer()).getName();

		chatboxTypedText.setText(getImgTag(getIconIdx()) + Text.removeTags(rsn) + ":" + chatbox[1]);
	}

	private IndexedSprite getIndexedSprite(String file)
	{
		try
		{
			log.debug("Loading: {}", file);
			BufferedImage image = ImageUtil.getResourceStreamFromClass(this.getClass(), file);
			return ImageUtil.getImageIndexedSprite(image, client);
		}
		catch (RuntimeException ex)
		{
			log.debug("Unable to load image: ", ex);
		}

		return null;
	}

	private String getImgTag(int i)
	{
		return "<img=" + i + ">";
	}

	private int getIconIdx()
	{
		if (selectedIcon == null)
		{
			updateSelectedIcon();
		}

		return iconIds.getOrDefault(selectedIcon, -1);
	}
}
