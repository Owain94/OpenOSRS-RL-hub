package net.runelite.client.plugins.wikisearchshortcuts;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("wikisearchshortcuts")
public interface WikiSearchShortcutsConfig extends Config
{
	@ConfigItem(
		keyName = "hotkey",
		name = "Wiki search hotkey ",
		description = "When you press this key wiki search interface opens",
		position = 1
	)
	default Keybind hotkey()
	{
		return Keybind.NOT_SET;
	}
}