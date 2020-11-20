package com.hidewidgets;

import net.runelite.client.config.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("hidewidgets")
public interface HideWidgetsConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "hideWidgetsToggle",
            name = "Hide widgets toggle",
            description = "Enable hotkey to hide all widgets"
    )
    default Keybind hideWidgetsToggle()
    {
        return new Keybind(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK);
    }
}
