package net.runelite.client.plugins.hidewidgets;

import java.awt.event.KeyEvent;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.input.KeyListener;

@Singleton
public class HideWidgetsKeyboardListener implements KeyListener
{
	@Inject
	private HideWidgetsConfig hideWidgetsConfig;

	@Inject
	private HideWidgetsPlugin plugin;

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (hideWidgetsConfig.hideWidgetsToggle().matches(e))
		{
			plugin.toggle();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}