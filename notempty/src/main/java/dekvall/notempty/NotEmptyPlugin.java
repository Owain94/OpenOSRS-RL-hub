package dekvall.notempty;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Not Empty",
	description = "Never empty potions again!",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class NotEmptyPlugin extends Plugin
{
	private static final String DRINK_PATTERN = ".*\\(\\d\\)";

	@Inject
	private Client client;

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();
		List<MenuEntry> cleaned = new ArrayList<>();

		for (MenuEntry entry : menuEntries)
		{
			int type = entry.getOpcode();
			String option = entry.getOption().toLowerCase();

			if (type != MenuOpcode.ITEM_FOURTH_OPTION.getId()
				|| !"empty".equals(option)
				|| !Pattern.matches(DRINK_PATTERN, entry.getTarget()))
			{
				cleaned.add(entry);
			}
		}
		client.setMenuEntries(cleaned.toArray(new MenuEntry[0]));
	}
}
