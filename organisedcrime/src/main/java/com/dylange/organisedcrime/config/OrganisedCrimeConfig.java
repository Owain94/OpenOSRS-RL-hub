package com.dylange.organisedcrime.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("organised-crime")
public interface OrganisedCrimeConfig extends Config {
    @ConfigItem(
            keyName = "multi",
            position = 0,
            name = "Multi only",
            description = "Keep track of only multi combat areas."
    )
    default boolean multiCombatOnly() {
        return false;
    }

    @ConfigItem(
            keyName = "arceeus",
            name = "Arceeus",
            position = 1,
            description = "Track Arceeus locations."
    )
    default boolean trackArceeus() {
        return true;
    }

    @ConfigItem(
            keyName = "hosidius",
            name = "Hosidius",
            position = 2,
            description = "Track Hosidius locations."
    )
    default boolean trackHosidius() {
        return true;
    }

    @ConfigItem(
            keyName = "lovakengj",
            name = "Lovakengj",
            position = 3,
            description = "Track Lovakengj locations."
    )
    default boolean trackLovakengj() {
        return true;
    }

    @ConfigItem(
            keyName = "piscarilius",
            name = "Piscarilius",
            position = 4,
            description = "Track Piscarilius locations."
    )
    default boolean trackPiscarilius() {
        return true;
    }

    @ConfigItem(
            keyName = "shayzien",
            name = "Shayzien",
            position = 5,
            description = "Track Shayzien locations."
    )
    default boolean trackShayzien() {
        return true;
    }

    @ConfigItem(
            keyName = "other",
            name = "Other",
            position = 6,
            description = "Track other locations, not within above house boundaries"
    )
    default boolean trackOther() {
        return true;
    }
}
