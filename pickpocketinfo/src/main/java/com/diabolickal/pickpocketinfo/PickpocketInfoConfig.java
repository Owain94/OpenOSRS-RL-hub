package com.diabolickal.pickpocketinfo;

import net.runelite.client.config.*;

@ConfigGroup("Pickpocket Info")
public interface PickpocketInfoConfig extends Config
{

    @ConfigItem(
            keyName = "showDodgy",
            name = "Show Dodgy Necklace Info",
            description = "Whether or not to display Dodgy Necklace charges.",
            position = 0
    )
    default boolean showDodgy()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showPouches",
            name = "Show Pouches Counter",
            description = "Whether or not to display the number pouches in your inventory on the overlay.",
            position = 1
    )
    default boolean showPouches()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showSessionTotal",
            name = "Show Attempts",
            description = "Whether or not to display the number of pickpocket attempts for this session",
            position = 2
    )
    default boolean showSessionTotal()
    {
        return false;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "warnThreshold",
            name = "Warning Threshold",
            description = "At how many dodgy necklace charges should the overlay text turn red.",
            position = 4
    )
    default int warnThreshold(){ return 1;}

    @Range(
            max = 30
    )
    @ConfigItem(
            keyName = "overlayDuration",
            name = "Overlay Duration",
            description = "How long the overlay lasts between pickpockets in seconds. Zero means overlay will never go away.",
            position = 5
    )
    default int overlayDuration(){ return 10;}

    @ConfigItem(
            keyName = "resetType",
            name = "Reset Rate on",
            description = "When to reset the timer. On logout or upon exiting Runelite.",
            position = 6
    )
    default ResetType resetType()
    {
        return ResetType.EXIT;
    }

    /* ======== Extras Dropdown ======== */
    @ConfigTitleSection(
    	keyName = "showExtras",
            name = "Extra Stats",
            description = "Settings for showing extra, 'fun', stats",
            position = 7
    )
	default Title showExtras()
	{
		return new Title();
	}


    @ConfigItem(
            keyName = "showBrokenDodgys",
            name = "Show Broken Dodgy Counter",
            description = "Whether or not to display the number of dodgy necklaces you've broken.",
            titleSection = "showExtras",
            position = 8
    )
    default boolean showBrokenDodgys()
    {
        return false;
    }

    @ConfigItem(
            keyName = "showPouchesTotal",
            name = "Show Total Pouches",
            description = "Whether or not to display the number of total pouches you've stolen.",
            titleSection = "showExtras",
            position = 9
    )
    default boolean showPouchesTotal()
    {
        return false;
    }


    /*===HIDDEN===*/

    //Keeps track of dodgy necklace charges even if Runelite is closed
    @ConfigItem(
            keyName = "dodgyNecklace",
            name = "",
            description = "",
            hidden = true
    )
    default int dodgyNecklace()
    {
        return -1;
    }

    @ConfigItem(
            keyName = "dodgyNecklace",
            name = "",
            description = ""
    )
    void dodgyNecklace(int dodgyNecklace);

    //Keeps track of total pickpocket attempts
    @ConfigItem(
            keyName = "totalAttempts",
            name = "",
            description = "",
            hidden = true
    )
    default int totalAttempts()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "totalAttempts",
            name = "",
            description = ""
    )
    void totalAttempts(int totalAttempts);

}


