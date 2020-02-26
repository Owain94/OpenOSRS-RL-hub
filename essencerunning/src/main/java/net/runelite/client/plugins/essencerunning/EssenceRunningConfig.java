package net.runelite.client.plugins.essencerunning;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("essencerunning")
public interface EssenceRunningConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "sessionStatistics",
		name = "Session Statistics",
		description = "Displays statistics such as Pure essence/Binding necklace traded per runner and Fire runes crafted"
	)
	default boolean sessionStatistics()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "preventFireRunes",
		name = "Prevent Fire runes",
		description = "Forces menu to open when you click the Fire Altar if you would accidentally craft Fire runes"
	)
	default boolean preventFireRunes()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "highlightBindingNecklace",
		name = "Highlight Binding necklace",
		description = "Highlights Binding necklace if you have no amulet equipped or the Runecrafter has 25/26 slots available in trade"
	)
	default EssenceRunningItemDropdown.HighlightBindingNecklace highlightBindingNecklace()
	{
		return EssenceRunningItemDropdown.HighlightBindingNecklace.OFF;
	}

	@ConfigItem(
		position = 3,
		keyName = "highlightRingOfDueling",
		name = "Highlight Ring of dueling",
		description = "Highlights Ring of dueling(8) if you have no ring equipped"
	)
	default boolean highlightRingOfDueling()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "highlightTradeSent",
		name = "Highlight Trade Sent",
		description = "Highlights chat box to green if trade offer has been successfully sent"
	)
	default boolean highlightTradeSent()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "clanChatOverlay",
		name = "Clan Chat Overlay",
		description = "Displays messages in the clan chat as an overlay on top of the chat box"
	)
	default boolean clanChatOverlay()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "filterTradeMessages",
		name = "Filter Trade Messages",
		description = "Filters out all messages in trade chat except for 'wishes to trade with you'"
	)
	default boolean filterTradeMessages()
	{
		return false;
	}

	@ConfigItem(
		position = 10,
		keyName = "swapOfferAll",
		name = "Swap Offer-All",
		description = "Swaps the 'Offer' option to 'Offer-All' when holding shift"
	)
	default boolean swapOfferAll()
	{
		return true;
	}

	@ConfigItem(
		position = 11,
		keyName = "swapBankOp",
		name = "Swap Bank Op",
		description = "Swaps the extra menu option in banks (Wield, Eat, etc.) when holding shift"
	)
	default boolean swapBankOp()
	{
		return true;
	}

	@ConfigItem(
		position = 12,
		keyName = "swapBankWithdrawOp",
		name = "Swap Bank Withdraw Op",
		description = "Swaps the Withdraw quantity of certain items (Ring of dueling, Binding necklace, etc.) when holding shift"
	)
	default boolean swapBankWithdrawOp()
	{
		return true;
	}

	@ConfigItem(
		position = 20,
		keyName = "shiftClickCustomization",
		name = "Customizable shift-click",
		description = "Allows customization of shift-clicks on items below that persist even when RuneLite loses focus"
	)
	default boolean shiftClickCustomization()
	{
		return true;
	}

	@ConfigItem(
		position = 21,
		keyName = "pureEssence",
		name = "Pure essence",
		description = "Customize shift-click of 'Pure essence' in inventory"
	)
	default EssenceRunningItemDropdown.RingOfDueling pureEssence()
	{
		return EssenceRunningItemDropdown.RingOfDueling.USE;
	}

	@ConfigItem(
		position = 22,
		keyName = "essencePouch",
		name = "Essence pouch",
		description = "Customize shift-click of 'Essence pouch' in inventory"
	)
	default EssenceRunningItemDropdown.EssencePouch essencePouch()
	{
		return EssenceRunningItemDropdown.EssencePouch.EMPTY;
	}

	@ConfigItem(
		position = 23,
		keyName = "bindingNecklace",
		name = "Binding necklace",
		description = "Customize shift-click of 'Binding necklace' in inventory"
	)
	default EssenceRunningItemDropdown.BindingNecklace bindingNecklace()
	{
		return EssenceRunningItemDropdown.BindingNecklace.USE;
	}

	@ConfigItem(
		position = 24,
		keyName = "ringOfDueling",
		name = "Ring of dueling",
		description = "Customize shift-click of 'Ring of dueling' in inventory"
	)
	default EssenceRunningItemDropdown.RingOfDueling ringOfDueling()
	{
		return EssenceRunningItemDropdown.RingOfDueling.WEAR;
	}

	@ConfigItem(
		position = 25,
		keyName = "staminaPotion",
		name = "Stamina potion",
		description = "Customize shift-click of 'Stamina potion' and 'Energy potion' in inventory"
	)
	default EssenceRunningItemDropdown.StaminaPotion staminaPotion()
	{
		return EssenceRunningItemDropdown.StaminaPotion.DRINK;
	}

	@ConfigItem(
		position = 26,
		keyName = "earthTalisman",
		name = "Earth talisman",
		description = "Customize shift-click of 'Earth talisman' in inventory"
	)
	default EssenceRunningItemDropdown.EarthTalisman earthTalisman()
	{
		return EssenceRunningItemDropdown.EarthTalisman.USE;
	}

	@ConfigItem(
		position = 27,
		keyName = "craftingCape",
		name = "Crafting cape",
		description = "Customize shift-click of 'Crafting cape' in inventory"
	)
	default EssenceRunningItemDropdown.CraftingCape craftingCape()
	{
		return EssenceRunningItemDropdown.CraftingCape.TELEPORT;
	}
}