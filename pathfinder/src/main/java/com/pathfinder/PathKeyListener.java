package com.pathfinder;

import net.runelite.client.config.Keybind;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

public class PathKeyListener implements KeyListener {
    @Inject
    PathHighlightConfig config;

    @Inject
    PathHighlightPlugin plugin;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //TODO: Add integration with Key Rebinding plugin
        Keybind keybind = config.displayKeybind();
        if (keybind.matches(e)) {
            if (config.displaySetting() == PathDisplaySetting.DISPLAY_WHILE_KEY_PRESSED) {
                plugin.setDisplay(true);
            } else if (config.displaySetting() == PathDisplaySetting.TOGGLE_ON_KEY_PRESSED) {
                plugin.setDisplay(!plugin.isDisplay());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keybind keybind = config.displayKeybind();
        if (keybind.matches(e) && config.displaySetting() == PathDisplaySetting.DISPLAY_WHILE_KEY_PRESSED) {
            plugin.setDisplay(false);
        }
    }
}
