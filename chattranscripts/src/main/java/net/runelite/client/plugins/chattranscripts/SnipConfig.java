package net.runelite.client.plugins.chattranscripts;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Chat Transcripts")
public interface SnipConfig extends Config
{
	@ConfigItem(
		keyName = "location",
		name = "Side Panel Location",
		description = "Determines the location of the icon in the side panel.",
		position = 1
	)
	default int location()
	{
		return 15;
	}

	@ConfigItem(
		keyName = "clipboard",
		name = "Copy to clipboard",
		description = "Copies the image to clipboard after generating.",
		position = 4
	)
	default boolean clipboard()
	{
		return true;
	}

	@ConfigItem(
		keyName = "save",
		name = "Save after generation",
		description = "Saves the transcript image after creation.",
		position = 2
	)
	default boolean saveImage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "open",
		name = "Open after saving",
		description = "Opens the image after saving.",
		position = 3
	)
	default boolean postOpen()
	{
		return true;
	}

}