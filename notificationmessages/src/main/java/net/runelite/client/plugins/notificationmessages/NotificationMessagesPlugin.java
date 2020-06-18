package net.runelite.client.plugins.notificationmessages;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Notification Messages",
	tags = "hub,chat,notify,pet,pb,personal best,follow,follower",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
@Slf4j
public class NotificationMessagesPlugin extends Plugin
{
	private static final String FOLLOW_PET = "You have a funny feeling like you're being followed";
	private static final String INVENTORY_PET = "You feel something weird sneaking into your backpack";
	private static final String DUPE_PET = "You have a funny feeling like you would have been followed";
	private static final String NEW_PB = "new personal best";
	private static final String NEW_TOB_PB = "Personal best!";

	@Inject
	private NotificationMessagesConfig config;

	@Inject
	private Notifier notifier;

	@Provides
	NotificationMessagesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NotificationMessagesConfig.class);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		switch (chatMessage.getType())
		{
			case GAMEMESSAGE:
				if (chatMessage.getMessage().contains(FOLLOW_PET))
				{
					if (config.notifyOnPet())
					{
						notifier.notify(config.PetFollowMessage());
					}
				}
				if (chatMessage.getMessage().contains(INVENTORY_PET))
				{
					if (config.notifyOnPet())
					{
						notifier.notify(config.PetBackpackMessage());
					}
				}
				if (chatMessage.getMessage().contains(DUPE_PET))
				{
					if (config.notifyOnPet())
					{
						notifier.notify(config.PetDupeMessage());
					}
				}
				if (chatMessage.getMessage().contains(NEW_PB) || chatMessage.getMessage().contains(NEW_TOB_PB))
				{
					if (config.notifyOnPersonalBest())
					{
						notifier.notify(config.personalBestMessage());
					}
				}
				break;
			case FRIENDSCHATNOTIFICATION:
				if (chatMessage.getMessage().contains(NEW_PB))
				{
					if (config.notifyOnPersonalBest())
					{
						notifier.notify(config.personalBestMessage());
					}
				}
				break;
		}
	}
}