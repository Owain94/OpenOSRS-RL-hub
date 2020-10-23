package ejedev.chompyhunter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;


@ConfigGroup("chompyhunter")
public interface ChompyHunterConfig extends Config
{
    @ConfigItem(
            keyName = "notifyChompy",
            name = "Notify on spawn",
            description = "Sends a notification when a chompy spawns",
            position = 1
    )
    default boolean notifyChompySpawn()
    {
        return false;
    }
}
