package net.runelite.client.plugins.wikisearchshortcuts;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.inject.Provider;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.wiki.WikiSearchChatboxTextInput;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Wiki Search Shortcuts",
	description = "Shortcut keys to open osrs wiki search",
	tags = {"wiki", "search", "shortcut", "hotkey"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
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
	WikiSearchShortcutsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WikiSearchShortcutsConfig.class);
	}

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(hotkeyListener);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(hotkeyListener);
	}

}