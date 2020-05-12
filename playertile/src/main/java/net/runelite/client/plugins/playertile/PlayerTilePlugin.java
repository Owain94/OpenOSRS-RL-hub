package net.runelite.client.plugins.playertile;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Player Server Tile",
	description = "Display server-tile of the local player",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class PlayerTilePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlayerTileOverlay tileOverlay;

	public boolean inPVPWorld = false;
	public boolean inBlockedRegion = false;

	private final int[] BLOCKED_REGIONS = new int[]{-1};//add to list if specific regions need to be blocked.

	@Override
	protected void startUp()
	{
		overlayManager.add(tileOverlay);
		CheckForIllegalAreas();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(tileOverlay);
	}

	void CheckForIllegalAreas()
	{
		if (client.getGameState() == GameState.LOGGED_IN || client.getGameState() == GameState.LOADING)
		{
			inPVPWorld = client.getWorldType().contains(WorldType.PVP);

			inBlockedRegion = false;
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer != null)
			{
				int currentRegion = localPlayer.getWorldLocation().getRegionID();
				for (int region : BLOCKED_REGIONS)
				{
					if (region == currentRegion)
					{
						inBlockedRegion = true;
						break;
					}
				}
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		CheckForIllegalAreas();
	}

	@Provides
	PlayerTileConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlayerTileConfig.class);
	}
}