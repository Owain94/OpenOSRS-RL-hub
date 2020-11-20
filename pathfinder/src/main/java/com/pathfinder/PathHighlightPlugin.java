package com.pathfinder;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
		name = "Pathfinder",
		description = "Highlight the path your character will take to the hovered tile",
		tags = {"highlight", "overlay", "path", "tile", "tiles", "gauntlet", "zalcano"},
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class PathHighlightPlugin extends Plugin
{
	@Inject
	private PathHighlightConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PathHighlightOverlay overlay;

	@Inject
	private KeyManager keyManager;

	@Inject
	private PathKeyListener keyListener;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	boolean display;

	@Provides
	PathHighlightConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PathHighlightConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		keyManager.registerKeyListener(keyListener);
		setDisplay(config.displaySetting() == PathDisplaySetting.ALWAYS_DISPLAY);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		keyManager.unregisterKeyListener(keyListener);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("pathfinder") && event.getKey().equals("displaySetting")) {
			setDisplay(config.displaySetting() == PathDisplaySetting.ALWAYS_DISPLAY);
		}
	}
}
