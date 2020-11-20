package com.hidewidgets;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;

@Singleton
public class HideWidgetsKeyboardListener implements KeyListener
{
    @Inject
    private HideWidgetsConfig hideWidgetsConfig;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

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
