package com.swim;

import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Timer;

class SwimTimer extends Timer
{
    SwimTimer(Plugin plugin, BufferedImage image)
    {
        super(62, ChronoUnit.SECONDS, image, plugin);
        setTooltip("Time left until location changes");
    }
}
