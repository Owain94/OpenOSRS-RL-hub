package com.profittracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

/**
 * The ProfitTrackerConfig class is used to provide user preferences to the ProfitTrackerPlugin.
 */
@ConfigGroup("ptconfig")
public interface ProfitTrackerConfig extends Config
{

    @ConfigItem(
            keyName = "goldDrops",
            name = "Show value changes (gold drops) ",
            description = "Show each profit increase or decrease"
    )
    default boolean goldDrops()
    {
        return true;
    }
}

