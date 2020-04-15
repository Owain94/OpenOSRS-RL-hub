package net.runelite.client.plugins.fixedhidechat;

import java.awt.event.KeyEvent;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import static net.runelite.client.plugins.fixedhidechat.FixedHideChatConstants.AUTO_EXPAND_WIDGETS;
import static net.runelite.client.plugins.fixedhidechat.FixedHideChatConstants.DEFAULT_VIEW_HEIGHT;
import static net.runelite.client.plugins.fixedhidechat.FixedHideChatConstants.EXPANDED_VIEW_HEIGHT;
import static net.runelite.client.plugins.fixedhidechat.FixedHideChatConstants.FIXED_MAIN;
import static net.runelite.client.plugins.fixedhidechat.FixedHideChatConstants.TO_CONTRACT_WIDGETS;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Fixed Mode Hide Chat",
	description = "Hides chatbox in fixed mode and expands the view",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class FixedHideChatPlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	private int lastMenu = 0;
	private boolean hideChat = true;

	@Override
	protected void startUp()
	{
		// Register listener
		keyManager.registerKeyListener(this);
	}

	@Override
	protected void shutDown()
	{
		// Unregister listener
		keyManager.unregisterKeyListener(this);

		// Reset menu state
		hideChat = true;
		lastMenu = 0;

		// Reset widgets
		resetWidgets();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (!client.isResized() && e.getKeyCode() == KeyEvent.VK_ESCAPE && !hideChat)
		{
			hideChat = true;
			e.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Subscribe
	public void onBeforeRender(final BeforeRender event)
	{
		if (client.isResized())
		{
			return;
		}

		if (hideChat)
		{
			// Expand the view height
			setViewSizeTo(DEFAULT_VIEW_HEIGHT, EXPANDED_VIEW_HEIGHT);
		}

		final Widget chatboxMessages = client.getWidget(WidgetInfo.CHATBOX);

		if (chatboxMessages != null)
		{
			boolean found = !hideChat;

			// Check if any auto-expand interface is open
			if (!found)
			{
				for (final Map.Entry<Integer, Integer> widgetInfo : AUTO_EXPAND_WIDGETS)
				{
					final Widget widget = client.getWidget(widgetInfo.getKey(), widgetInfo.getValue());

					if (widget != null && !widget.isSelfHidden())
					{
						found = true;
						break;
					}
				}
			}

			// Resize some widgets that might interfere with having expanded chat
			setWidgetsSizeTo(
				found ? EXPANDED_VIEW_HEIGHT : DEFAULT_VIEW_HEIGHT,
				found ? DEFAULT_VIEW_HEIGHT : EXPANDED_VIEW_HEIGHT);

			// Hide/show chat messages
			chatboxMessages.setHidden(!found);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
	{
		if (!"Switch tab".equals(event.getOption()))
		{
			return;
		}

		final Widget chatboxMessages = client.getWidget(WidgetInfo.CHATBOX);
		final int newMenu = event.getParam1();
		hideChat = true;

		if (newMenu != lastMenu || (chatboxMessages != null && chatboxMessages.isHidden()))
		{
			hideChat = false;
			lastMenu = newMenu;
		}
	}

	private static void changeWidgetHeight(int originalHeight, int newHeight, Widget widget)
	{
		if (widget.getHeight() == originalHeight)
		{
			widget.setHeight(newHeight);

			final Widget[] nestedChildren = widget.getNestedChildren();

			if (nestedChildren != null)
			{
				for (final Widget nestedChild : nestedChildren)
				{
					if (nestedChild.getHeight() == originalHeight)
					{
						nestedChild.setHeight(newHeight);
					}
				}
			}

			final Widget[] dynamicChildren = widget.getDynamicChildren();

			if (dynamicChildren != null)
			{
				for (final Widget child : dynamicChildren)
				{
					if (child.getHeight() == originalHeight)
					{
						child.setHeight(newHeight);
					}
				}
			}
		}
	}

	private void setWidgetsSizeTo(final int originalHeight, final int newHeight)
	{
		final Widget viewport = client.getViewportWidget();

		if (viewport != null)
		{
			viewport.setHeight(newHeight);
		}

		for (final Map.Entry<Integer, Integer> widgetInfo : TO_CONTRACT_WIDGETS)
		{
			final Widget widget = client.getWidget(widgetInfo.getKey(), widgetInfo.getValue());

			if (widget != null && !widget.isSelfHidden())
			{
				changeWidgetHeight(originalHeight, newHeight, widget);
			}
		}
	}

	private void setViewSizeTo(final int originalHeight, final int newHeight)
	{
		final Widget fixedMain = client.getWidget(FIXED_MAIN.getKey(), FIXED_MAIN.getValue());

		if (fixedMain != null && fixedMain.getHeight() == originalHeight)
		{
			fixedMain.setHeight(newHeight);

			final Widget[] staticChildren = fixedMain.getStaticChildren();

			// Expand all children of the main fixed view
			for (final Widget child : staticChildren)
			{
				changeWidgetHeight(originalHeight, newHeight, child);
			}
		}

	}

	private void resetWidgets()
	{
		if (client.isResized())
		{
			return;
		}

		// Contract the view if it is expanded
		setViewSizeTo(EXPANDED_VIEW_HEIGHT, DEFAULT_VIEW_HEIGHT);
		setWidgetsSizeTo(EXPANDED_VIEW_HEIGHT, DEFAULT_VIEW_HEIGHT);

		// Show the chat messages widget again
		final Widget chatboxMessages = client.getWidget(WidgetInfo.CHATBOX);

		if (chatboxMessages != null)
		{
			chatboxMessages.setHidden(false);
		}
	}
}