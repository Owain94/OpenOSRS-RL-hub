package net.runelite.client.plugins.playertile;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Player Server Tile",
	description = "Display server-tile of the local player",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class PlayerTilePlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlayerTileOverlay tileOverlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(tileOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(tileOverlay);
	}


	@Provides
	PlayerTileConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlayerTileConfig.class);
	}
}