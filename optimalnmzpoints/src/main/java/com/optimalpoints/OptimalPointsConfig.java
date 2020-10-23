package com.optimalpoints;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup("nmzoptimalpoints")
public interface OptimalPointsConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "highlightHull",
            name = "Highlight hull",
            description = "Configures whether or not NPC should be highlighted by hull"
    )
    default boolean highlightHull() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "highlightTile",
            name = "Highlight tile",
            description = "Configures whether or not NPC should be highlighted by tile"
    )
    default boolean highlightTile() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "highlightSouthWestTile",
            name = "Highlight south west tile",
            description = "Configures whether or not NPC should be highlighted by south western tile"
    )
    default boolean highlightSouthWestTile() {
        return false;
    }


    @Range(
            min = 1,
            max = 4
    )
    @ConfigItem(
            position = 3,
            keyName = "maxRankToShow",
            name = "Number of bosses to highlight",
            description = "Number of bosses to highlight in NMZ between 1-4"
    )
    default int maxRankToShow() {
        return 1;
    }

    @ConfigItem(
            position = 4,
            keyName = "npcColor1",
            name = "Highlight Color #1",
            description = "Color of the NPC highlight for the enemy worth the most points"
    )
    @Alpha
    default Color getHighlightColor1() {
        return new Color(255, 223, 0);
    }

    @ConfigItem(
            position = 5,
            keyName = "npcColor2",
            name = "Highlight Color #2",
            description = "Color of the NPC highlight for the enemy worth the second most points"
    )
    @Alpha
    default Color getHighlightColor2() {
        return new Color(192, 192, 192);
    }

    @ConfigItem(
            position = 6,
            keyName = "npcColor3",
            name = "Highlight Color #3",
            description = "Color of the NPC highlight for the enemy worth the third most points"
    )
    @Alpha
    default Color getHighlightColor3() {
        return new Color(205, 127, 50);
    }

    @ConfigItem(
            position = 7,
            keyName = "npcColor4",
            name = "Highlight Color #4",
            description = "Color of the NPC highlight for the enemy worth the least points"
    )
    @Alpha
    default Color getHighlightColor4() {
        return Color.WHITE;
    }
}