package net.runelite.client.plugins.notificationmessages;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
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

	@ConfigItem(
		keyName = "notifyOnPet",
		name = "Notify on pet drop",
		description = "Send a notification when you receive a pet",
		position = 4,
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
		position = 5,
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
		position = 6,
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
		position = 7,
		titleSection = "petTitle"

	)
	default String PetDupeMessage()
	{
		return "Congratulations! You just received a pet, again...";
	}
}