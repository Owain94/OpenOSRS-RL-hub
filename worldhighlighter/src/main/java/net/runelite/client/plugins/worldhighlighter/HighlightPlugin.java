package net.runelite.client.plugins.worldhighlighter;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Point;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.PlayerMenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "World Highlighter",
	description = "Highlights the world a friend or clanmember is on",
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class HighlightPlugin extends Plugin
{
	private static final List<String> AFTER_OPTIONS = List.of("Message", "Add ignore", "Remove friend", "Kick");
	private final net.runelite.client.ui.overlay.components.TextComponent textComponent = new TextComponent();
	private static final Color HIGHLIGHT_BORDER_COLOR;
	private static final Color HIGHLIGHT_HOVER_BORDER_COLOR;
	private static final Color HIGHLIGHT_FILL_COLOR;
	private int world;
	private String player;
	private boolean clan;
	@Inject
	private Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private HighlightOverlay HighlightOverlay;
	@Inject
	private HighlightConfig config;

	@Override
	protected void startUp()
	{
		this.overlayManager.add(this.HighlightOverlay);
		this.resetWorld();
	}

	@Override
	protected void shutDown()
	{
		this.overlayManager.remove(this.HighlightOverlay);
		this.resetWorld();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		//If you or someone you love is able to figure out how to only have this enabled for clan and private chat, hit a Turtle up.
		if (true)
		{
			int groupId = WidgetInfo.TO_GROUP(event.getParam1());
			String option = event.getOption();
			if (groupId == WidgetInfo.CHATBOX.getGroupId() && !"Kick".equals(option) || groupId == WidgetInfo.PRIVATE_CHAT_MESSAGE.getGroupId())
			{
				if (!AFTER_OPTIONS.contains(option))
				{
					return;
				}
				MenuEntry high = new MenuEntry();
				high.setOption("Highlight World");
				high.setTarget(event.getTarget());
				high.setOpcode(MenuOpcode.RUNELITE.getId());
				high.setParam0(event.getParam0());
				high.setParam1(event.getParam1());
				high.setIdentifier(event.getIdentifier());
				this.insertMenuEntry(high, this.client.getMenuEntries());
			}
		}
	}

	private void insertMenuEntry(MenuEntry newEntry, MenuEntry[] entries)
	{
		MenuEntry[] newMenu = (MenuEntry[]) ObjectArrays.concat(entries, newEntry);
		int menuEntryCount = newMenu.length;
		ArrayUtils.swap(newMenu, menuEntryCount - 1, menuEntryCount - 2);
		this.client.setMenuEntries(newMenu);
	}

	private void highlight(String playerName)
	{
		clan = false;
		if (this.client.getFriendsChatManager() != null)
		{
			for (int c = 0; c != this.client.getFriendsChatManager().getMembers().length; c++)
			{
				if (this.client.getFriendsChatManager().getMembers()[c].getName().equals(playerName))
				{
					clan = true;
					player = toTrueName(this.client.getFriendsChatManager().getMembers()[c].getName());
					world = this.client.getFriendsChatManager().getMembers()[c].getWorld();
					if (world == this.client.getWorld())
					{
						sendNotification(2);
						this.resetWorld();
					}
					else
					{
						if (config.clanFirst())
						{
							sendNotification(4);
						}
						else
						{
							sendNotification(1);
						}
					}
					break;
				}
			}
		}
		if (!clan)
		{
			for (int f = 0; f != this.client.getFriendContainer().getCount(); f++)
			{
				if (this.client.getFriendContainer().getMembers()[f].getName().equals(playerName))
				{
					player = toTrueName(this.client.getFriendContainer().getMembers()[f].getName());
					world = this.client.getFriendContainer().getMembers()[f].getWorld();
					if (world == this.client.getWorld())
					{
						sendNotification(2);
						this.resetWorld();
					}
					else
					{
						sendNotification(1);
					}
				}
			}
		}
		if (world == 0)
		{
			sendNotification(3);
		}
	}

	@Subscribe
	public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event)
	{
		if (event.getMenuOption().equals("Highlight World"))
		{
			this.highlight(Text.removeTags(event.getMenuTarget()));
		}
	}

	void highlightWidget(Graphics2D graphics, Widget toHighlight, Widget container, Rectangle padding, String text)
	{
		padding = Objects.requireNonNullElse(padding, new Rectangle());
		Point canvasLocation = toHighlight.getCanvasLocation();
		if (canvasLocation != null && container != null)
		{
			Point windowLocation = container.getCanvasLocation();
			if (windowLocation.getY() <= canvasLocation.getY() + toHighlight.getHeight() && windowLocation.getY() + container.getHeight() >= canvasLocation.getY())
			{
				Area widgetArea = new Area(new Rectangle(canvasLocation.getX() - padding.x, Math.max(canvasLocation.getY(), windowLocation.getY()) - padding.y, toHighlight.getWidth() + padding.x + padding.width, Math.min(Math.min(windowLocation.getY() + container.getHeight() - canvasLocation.getY(), toHighlight.getHeight()), Math.min(canvasLocation.getY() + toHighlight.getHeight() - windowLocation.getY(), toHighlight.getHeight())) + padding.y + padding.height));
				OverlayUtil.renderHoverableArea(graphics, widgetArea, this.client.getMouseCanvasPosition(), HIGHLIGHT_FILL_COLOR, HIGHLIGHT_BORDER_COLOR, HIGHLIGHT_HOVER_BORDER_COLOR);
				if (text != null)
				{
					FontMetrics fontMetrics = graphics.getFontMetrics();
					this.textComponent.setPosition(new java.awt.Point(canvasLocation.getX() + toHighlight.getWidth() / 2 - fontMetrics.stringWidth(text) / 2, canvasLocation.getY() + fontMetrics.getHeight()));
					this.textComponent.setText(text);
					this.textComponent.render(graphics);
				}
			}
		}
	}

	void scrollToWidget(Widget list, Widget scrollbar, Widget... toHighlight)
	{
		Widget parent = list;
		int averageCentralY = 0;
		int nonnullCount = 0;
		Widget[] var7 = toHighlight;
		int var8 = toHighlight.length;
		for (int var9 = 0; var9 < var8; ++var9)
		{
			Widget widget = var7[var9];
			if (widget != null)
			{
				averageCentralY += widget.getRelativeY() + widget.getHeight() / 2;
				++nonnullCount;
			}
		}
		if (nonnullCount != 0)
		{
			averageCentralY /= nonnullCount;
			int newScroll = Math.max(0, Math.min(parent.getScrollHeight(), averageCentralY - parent.getHeight() / 4));
			this.client.runScript(new Object[]{72, scrollbar.getId(), parent.getId(), newScroll});
		}
	}

	private void sendNotification(int type)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if (this.config.message() == false)
		{
			return;
		}
		if (type == 1)
		{
			stringBuilder.append("Highlighting W").append(world).append(".");
			String notification = stringBuilder.toString();
			this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", notification, "");
		}
		else if (type == 2)
		{
			stringBuilder.append("Player is on your world.");
			String notification = stringBuilder.toString();
			this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", notification, "");
		}
		else if (type == 3)
		{
			stringBuilder.append("Unable to find world, player is neither in friends chat nor on friends list.");
			String notification = stringBuilder.toString();
			this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", notification, "");
		}
		else
		{
			stringBuilder.append("Highlighting " + player + " in friends chat.");
			String notification = stringBuilder.toString();
			this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", notification, "");
		}
	}

	@Provides
	HighlightConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HighlightConfig.class);
	}

	public int getWorld()
	{
		return this.world;
	}

	public void resetWorld()
	{
		this.world = 0;
	}

	public String getPlayer()
	{
		return this.player;
	}

	public void resetPlayer()
	{
		this.player = "";
	}

	public boolean getClan()
	{
		return this.clan;
	}

	static
	{
		HIGHLIGHT_BORDER_COLOR = Color.ORANGE;
		HIGHLIGHT_HOVER_BORDER_COLOR = HIGHLIGHT_BORDER_COLOR.darker();
		HIGHLIGHT_FILL_COLOR = new Color(0, 255, 0, 20);
	}

	private String toTrueName(String str)
	{
		return CharMatcher.ascii().retainFrom(str.replace('\u00A0', ' ')).trim();
	}
}