package dekvall.pushnotification;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("pushnotifications")
public interface PushNotificationsConfig extends Config
{
	@ConfigTitleSection(
		keyName = "pushbulletTitle",
		name = "Pushbullet",
		description = "Pushbullet Settings",
		position = 0
	)
	default Title pushbulletTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "pushbullet",
		name = "Pushbullet token",
		description = "API token for pushbullet",
		titleSection = "pushbulletTitle"
	)
	String pushbullet();

	@ConfigTitleSection(
		keyName = "pushoverTitle",
		name = "Pushover",
		description = "Pushover Settings",
		position = 1
	)
	default Title pushoverTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "pushover_user",
		name = "Pushover user key",
		description = "User key for Pushover",
		titleSection = "pushoverTitle"
	)
	String pushover_user();

	@ConfigItem(
		keyName = "pushover_api",
		name = "Pushover API token",
		description = "API token for Pushover",
		titleSection = "pushoverTitle"
	)
	String pushover_api();
}
