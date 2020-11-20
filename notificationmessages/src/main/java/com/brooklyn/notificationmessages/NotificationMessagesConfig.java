package com.brooklyn.notificationmessages;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("Notification Messages")
public interface NotificationMessagesConfig extends Config
{
	@ConfigTitleSection(
		keyName = "pbTitle",
		name = "Personal Best",
		description = "Settings for Personal Best notifications",
		position = 0
	)
	default Title pbTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "petTitle",
		name = "Pet Drop",
		description = "Send a notification when you achieve a new personal best",
		position = 3
	)
	default Title petTitle()
	{
		return new Title();
	}

	@ConfigTitleSection(
		keyName = "potionsTitle",
		name = "Potions",
		description = "Settings for potion expiration notifications",
		position = 2
	)
	default Title potionsTitle()
	{
		return new Title();
	}


	@ConfigItem(

		keyName = "notifyOnPersonalBest",
		name = "Notify on new PB",
		description = "Send a notification when you achieve a new personal best",
		position = 1,
		titleSection = "pbTitle"

	)
	default boolean notifyOnPersonalBest()
	{
		return true;
	}

	@ConfigItem(
		keyName = "messagePersonalBest",
		name = "New PB notification message",
		description = "The message with which to notify you of a new personal best",
		position = 2,
		titleSection = "pbTitle"
	)
	default String personalBestMessage()
	{
		return "New personal best!";
	}

	@ConfigItem(
		keyName = "notifyOnPet",
		name = "Notify on pet drop",
		description = "Send a notification when you receive a pet",
		position = 3,
		titleSection = "petTitle"
	)
	default boolean notifyOnPet()
	{
		return true;
	}

	@ConfigItem(
		keyName = "followPet",
		name = "Following pet notification message",
		description = "The message with which to notify you of a new follower <br> (You have a funny feeling like you're being followed)",
		position = 4,
		titleSection = "petTitle"

	)
	default String PetFollowMessage()
	{
		return "Congratulations! You just received a pet!";
	}

	@ConfigItem(
		keyName = "backpackPet",
		name = "Backpack pet notification message",
		description = "The message with which to notify you of a new pet in your inventory <br> (You feel something weird sneaking into your backpack)",
		position = 5,
		titleSection = "petTitle"

	)
	default String PetBackpackMessage()
	{
		return "Congratulations! You just received a pet! Check your inventory!";
	}

	@ConfigItem(
		keyName = "dupePet",
		name = "Duplicate pet notification message",
		description = "The message with which to notify you of a duplicate pet <br> (You have a funny feeling like you would have been followed...)",
		position = 6,
		titleSection = "petTitle"

	)
	default String PetDupeMessage()
	{
		return "Congratulations! You just received a pet, again...";
	}

	@ConfigItem(
		keyName = "antifireNotification",
		name = "Antifire expiration",
		description = "Notifies you when your antifire expires",
		position = 7,
		titleSection = "potionsTitle"
	)
	default boolean antifireNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "antifireMessage",
		name = "Antifire notification message",
		description = "The message with which to notify you of an antifire's expiration",
		position = 8,
		titleSection = "potionsTitle"
	)
	default String antifireMessage()
	{
		return "Your antifire protection has expired!";
	}

	@ConfigItem(
		keyName = "preAntifire",
		name = "Pre-Antifire expiration",
		description = "Notifies you before your antifire expires",
		position = 8,
		titleSection = "potionsTitle"
	)
	default boolean preAntifire()
	{
		return true;
	}

	@ConfigItem(
		keyName = "preAntifireMessage",
		name = "Pre-Antifire notification message",
		description = "The message with which to notify you before an antifire's expiration",
		position = 8,
		titleSection = "potionsTitle"
	)
	default String preAntifireMessage()
	{
		return "Your antifire protection is about to expire!";
	}

	@ConfigItem(
		keyName = "antipoisonNotification",
		name = "Antipoison expiration",
		description = "Notifies you when your antipoison expires",
		position = 9,
		titleSection = "potionsTitle"
	)
	default boolean antipoisonNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "antipoisonMessage",
		name = "Antipoison notification message",
		description = "The message with which to notify you of an antipoison's expiration",
		position = 10,
		titleSection = "potionsTitle"
	)
	default String antipoisonMessage()
	{
		return "Your poison protection has expired!";
	}

	@ConfigItem(
		keyName = "divinePotionNotification",
		name = "Divine Potion expiration",
		description = "Notifies you when your divine potion expires",
		position = 11,
		titleSection = "potionsTitle"
	)
	default boolean divinePotionNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "divinePotionMessage",
		name = "Divine Potion notification message",
		description = "The message with which to notify you of a divine potion's expiration",
		position = 12,
		titleSection = "potionsTitle"
	)
	default String divinePotionMessage()
	{
		return "Your Divine potion has expired!";
	}

	@ConfigItem(
		keyName = "overloadNotification",
		name = "Overload expiration",
		description = "Notifies you when your Overload expires",
		position = 13,
		titleSection = "potionsTitle"
	)
	default boolean overloadNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overloadMessage",
		name = "Overload notification message",
		description = "The message with which to notify you of an Overload's expiration",
		position = 14,
		titleSection = "potionsTitle"
	)
	default String overloadMessage()
	{
		return "Your Overload has expired!";
	}

	@ConfigItem(
		keyName = "staminaNotification",
		name = "Stamina expiration",
		description = "Notifies you when your stamina potion expires",
		position = 15,
		titleSection = "potionsTitle"
	)
	default boolean staminaNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "staminaMessage",
		name = "Stamina notification message",
		description = "The message with which to notify you of a stamina potion's expiration",
		position = 16,
		titleSection = "potionsTitle"
	)
	default String staminaMessage()
	{
		return "Your Stamina potion has expired!";
	}

	@ConfigItem(
		keyName = "imbuedHeartNotification",
		name = "Imbued Heart notification",
		description = "Notifies you when your Imbued heart has regained its power",
		position = 17,
		titleSection = "potionsTitle"
	)
	default boolean imbuedHeartNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "imbuedHeartMessage",
		name = "Imbued Heart notification message",
		description = "The message with which to notify you of an Imbued heart's reinvigoration",
		position = 18,
		titleSection = "potionsTitle"
	)
	default String imbuedHeartMessage()
	{
		return "Your Imbued heart is ready!";
	}
}
