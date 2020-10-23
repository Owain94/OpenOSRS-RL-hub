package com.swim;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;
import java.awt.*;

@ConfigGroup("underwater agility")
public interface SwimConfig extends Config
{
    enum CompassDirections
    {
        _4,
        _8,
        _16
    }
    @ConfigItem(
            position = 0,
            keyName = "showTimer",
            name = "Show Timer",
            description = "Show chest timer"
    )
    default boolean isTimerShown()
    {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "showChest",
            name = "Show Chest",
            description = "Show highlight for chest objects"
    )
    default boolean isChestShown()
    {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "colorChest",
            name = "Chest Color",
            description = "Color for the objective chest"
    )
    default Color getChestColor()
    {
        return Color.RED;
    }

    @ConfigItem(
            position = 3,
            keyName = "showPuffer",
            name = "Show Puffer Fish",
            description = "Show highlight for puffed puffer fish"
    )
    default boolean isPufferShown()
    {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "colorPuffer",
            name = "Puffer Fish Color",
            description = "Color for puffed puffer fish"
    )
    default Color getPufferColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            position = 5,
            keyName = "showHazard",
            name = "Show Currents",
            description = "Show highlights for hazardous currents"
    )
    default boolean isHazardShown() { return false; }

    @ConfigItem(
            position = 6,
            keyName = "colorHazard",
            name = "Current Color",
            description = "Color for hazardous currents"
    )
    default Color getHazardColor()
    {
        return Color.ORANGE;
    }

    @ConfigItem(
            position = 7,
            keyName = "showTearCount",
            name = "Show tear count",
            description = "Show tears found during current session"
    )
    default boolean isTearCountShown()
    {
        return true;
    }

    @ConfigItem(
            position = 8,
            keyName = "showTearsPerHour",
            name = "Show tears per hour",
            description = "Show tears per hour during current session"
    )
    default boolean isTearsPerHourShown()
    {
        return true;
    }

    @ConfigItem(
            keyName = "timeout",
            name = "Tear Count Timeout",
            description = "Time until the counter hides/resets",
            position = 9
    )
    @Units(Units.MINUTES)
    default int timeout()
    {
        return 5;
    }

    @ConfigItem(
            position = 10,
            keyName = "showDistance",
            name = "Show Distance",
            description = "Show distance to chest"
    )
    default boolean isDistanceShown() { return false; }

    @ConfigItem(
            position = 11,
            keyName = "showDirection",
            name = "Show Direction",
            description = "Show direction to chest"
    )
    default boolean isDirectionShown()
    {
        return false;
    }

    @ConfigItem(
            position = 12,
            keyName = "compassDirections",
            name = "Compass Points",
            description = "Number of cardinal and intercardinal directions"
    )
    default CompassDirections compassDirections() { return CompassDirections._8; }

    @ConfigItem(
            position = 13,
            keyName = "soundPlayed",
            name = "Play Sound",
            description = "Play chime when chest moves"
    )
    default boolean isSoundEnabled() { return false; }

    @ConfigItem(
            position = 14,
            keyName = "adjacentBubble",
            name = "Adjacent Bubble",
            description = "Indicate if current chest is adjacent to a bubble"
    )
    default boolean isAdjacentBubbleShown() { return true; }
}
