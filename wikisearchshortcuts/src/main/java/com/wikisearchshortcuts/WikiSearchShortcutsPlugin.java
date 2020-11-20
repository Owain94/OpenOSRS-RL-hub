package com.wikisearchshortcuts;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.wiki.WikiSearchChatboxTextInput;
import net.runelite.client.util.HotkeyListener;
import javax.inject.Inject;
import javax.inject.Provider;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Wiki Search Shortcuts",
	description = "Shortcut keys to open osrs wiki search",
	tags = {"wiki", "search", "shortcut", "hotkey"},
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class WikiSearchShortcutsPlugin extends Plugin
{
	@Inject
	private WikiSearchShortcutsConfig config;

	@Inject
	private Provider<WikiSearchChatboxTextInput> wikiSearchChatboxTextInputProvider;

	@Inject
	private KeyManager keyManager;

	@Getter(AccessLevel.PACKAGE)
	private final HotkeyListener hotkeyListener = new HotkeyListener(() -> config.hotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			wikiSearchChatboxTextInputProvider.get()
					.build();
		}
	};
	@Provides
	WikiSearchShortcutsConfig getConfig(ConfigManager configManager) { return configManager.getConfig(WikiSearchShortcutsConfig.class); }

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(hotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(hotkeyListener);
	}

}
