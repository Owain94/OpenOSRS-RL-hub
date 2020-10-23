package com.essencerunning;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("essencerunning")
public interface EssenceRunningConfig extends Config {

    @ConfigItem(
            position = 0,
            keyName = "clanChatOverlayHeight",
            name = "Clan Chat Overlay Height",
            description = "Displays messages in the clan chat as an overlay on top of the chat box"
    )
    default EssenceRunningItemDropdown.ClanChatOverlayHeight clanChatOverlayHeight() {
        return EssenceRunningItemDropdown.ClanChatOverlayHeight.ZERO;
    }

    @ConfigItem(
            position = 1,
            keyName = "filterTradeMessages",
            name = "Filter Trade Messages",
            description = "Filters out all messages in trade chat except for 'wishes to trade with you'"
    )
    default boolean filterTradeMessages() {
        return false;
    }

    @ConfigTitleSection(
		keyName = "runecrafterSettings",
            name = "Runecrafter Settings",
            description = "Section that houses all the relevant settings for Runecrafters",
            position = 100
    )
	default Title runecrafterSettings()
	{
		return new Title();
	}

    @ConfigItem(
            position = 0,
            keyName = "enableRunecraftingMode",
            name = "Enable Runecrafting Mode",
            description = "Must be enabled for any features in this section to work",
            titleSection = "runecrafterSettings"
    )
    default boolean enableRunecrafterMode() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "sessionStatistics",
            name = "Session Statistics",
            description = "Displays statistics such as Pure essence/Binding necklace traded per runner and Fire runes crafted",
            titleSection = "runecrafterSettings"
    )
    default boolean sessionStatistics() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "preventFireRunes",
            name = "Prevent Fire runes",
            description = "Forces menu to open when you click the Fire Altar if you would accidentally craft Fire runes",
            titleSection = "runecrafterSettings"
    )
    default boolean preventFireRunes() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "highlightEquipBindingNecklace",
            name = "Highlight Equip Binding necklace",
            description = "Highlights Binding necklace if you have no amulet equipped",
            titleSection = "runecrafterSettings"
    )
    default EssenceRunningItemDropdown.HighlightEquipBindingNecklace highlightEquipBindingNecklace() {
        return EssenceRunningItemDropdown.HighlightEquipBindingNecklace.EQUIP;
    }

    @ConfigTitleSection(
		keyName = "runnerSettings",
            name = "Runner Settings",
            description = "Section that houses all the relevant settings for Runners",
            position = 200
    )
	default Title drawingTitle()
	{
		return new Title();
	}

    @ConfigItem(
            position = 0,
            keyName = "enableRunnerMode",
            name = "Enable Runner Mode",
            description = "Must be enabled for any features in this section to work",
            titleSection = "runnerSettings"
    )
    default boolean enableRunnerMode() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "highlightTradeBindingNecklace",
            name = "Highlight Trade Binding necklace",
            description = "Highlights Binding necklace if the Runecrafter has 25/26 slots available in trade",
            titleSection = "runnerSettings"
    )
    default EssenceRunningItemDropdown.HighlightTradeBindingNecklace highlightTradeBindingNecklace() {
        return EssenceRunningItemDropdown.HighlightTradeBindingNecklace.TWENTY_FIVE;
    }

    @ConfigItem(
            position = 2,
            keyName = "highlightRingOfDueling",
            name = "Highlight Ring of dueling",
            description = "Highlights Ring of dueling(8) if you have no ring equipped",
            titleSection = "runnerSettings"
    )
    default boolean highlightRingOfDueling() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "highlightTradeSent",
            name = "Highlight Trade Sent",
            description = "Highlights chat box to green if trade offer has been successfully sent",
            titleSection = "runnerSettings"
    )
    default boolean highlightTradeSent() {
        return true;
    }

    @ConfigTitleSection(
    	keyName = "shiftClickSection",
            name = "Runner Shift-Click Settings",
            description = "Section that houses all the relevant settings for modifying shift-click behavior",
            position = 300
    )
	default Title shiftClickSection()
	{
		return new Title();
	}

    @ConfigItem(
            position = 0,
            keyName = "shiftClickCustomization",
            name = "Enable Customizable Shift-Click",
            description = "Allows customization of shift-clicks on items below that persist even when RuneLite loses focus",
            titleSection = "shiftClickSection"
    )
    default boolean shiftClickCustomization() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "swapOfferAll",
            name = "Swap Offer-All",
            description = "Swaps the 'Offer' option to 'Offer-All' when holding shift",
            titleSection = "shiftClickSection"
    )
    default boolean swapOfferAll() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "swapBankOp",
            name = "Swap Bank Op",
            description = "Swaps the extra menu option in banks (Wield, Eat, etc.) when holding shift",
            titleSection = "shiftClickSection"
    )
    default boolean swapBankOp() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "excludeBindingNecklaceOp",
            name = "Exclude Binding necklace Op",
            description = "Exclude swapping Binding necklace to 'Wear', should Disable this while soloing",
            titleSection = "shiftClickSection"
    )
    default boolean excludeBindingNecklaceOp() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "swapBankWithdrawOp",
            name = "Swap Bank Withdraw Op",
            description = "Swaps the Withdraw quantity of certain items (Ring of dueling, Binding necklace, etc.) when holding shift",
            titleSection = "shiftClickSection"
    )
    default boolean swapBankWithdrawOp() {
        return true;
    }

    @ConfigItem(
            position = 5,
            keyName = "pureEssence",
            name = "Pure essence",
            description = "Customize shift-click of 'Pure essence' in inventory",
            titleSection = "shiftClickSection"
    )
    default EssenceRunningItemDropdown.PureEssence pureEssence() {
        return EssenceRunningItemDropdown.PureEssence.USE;
    }

    @ConfigItem(
            position = 6,
            keyName = "essencePouch",
            name = "Essence pouch",
            description = "Customize shift-click of 'Essence pouch' in inventory",
            titleSection = "shiftClickSection"
    )
    default EssenceRunningItemDropdown.EssencePouch essencePouch() {
        return EssenceRunningItemDropdown.EssencePouch.EMPTY;
    }

    @ConfigItem(
            position = 7,
            keyName = "bindingNecklace",
            name = "Binding necklace",
            description = "Customize shift-click of 'Binding necklace' in inventory",
            titleSection = "shiftClickSection"
    )
    default EssenceRunningItemDropdown.BindingNecklace bindingNecklace() {
        return EssenceRunningItemDropdown.BindingNecklace.USE;
    }

    @ConfigItem(
            position = 8,
            keyName = "ringOfDueling",
            name = "Ring of dueling",
            description = "Customize shift-click of 'Ring of dueling' in inventory",
            titleSection = "shiftClickSection"
    )
    default EssenceRunningItemDropdown.RingOfDueling ringOfDueling() {
        return EssenceRunningItemDropdown.RingOfDueling.WEAR;
    }

    @ConfigItem(
            position = 9,
            keyName = "staminaPotion",
            name = "Stamina potion",
            description = "Customize shift-click of 'Stamina potion' and 'Energy potion' in inventory",
            titleSection = "shiftClickSection"
    )
    default EssenceRunningItemDropdown.StaminaPotion staminaPotion() {
        return EssenceRunningItemDropdown.StaminaPotion.DRINK;
    }

    @ConfigItem(
            position = 10,
            keyName = "earthTalisman",
            name = "Earth talisman",
            description = "Customize shift-click of 'Earth talisman' in inventory",
            titleSection = "shiftClickSection"
    )
    default EssenceRunningItemDropdown.EarthTalisman earthTalisman() {
        return EssenceRunningItemDropdown.EarthTalisman.USE;
    }

    @ConfigItem(
            position = 11,
            keyName = "craftingCape",
            name = "Crafting cape",
            description = "Customize shift-click of 'Crafting cape' in inventory",
            titleSection = "shiftClickSection"
    )
    default EssenceRunningItemDropdown.CraftingCape craftingCape() {
        return EssenceRunningItemDropdown.CraftingCape.TELEPORT;
    }
}
