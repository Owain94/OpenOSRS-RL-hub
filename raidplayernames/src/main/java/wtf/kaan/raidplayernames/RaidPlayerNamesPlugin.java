package wtf.kaan.raidplayernames;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.util.Text;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.raids.RaidRoom;
import net.runelite.client.plugins.raids.solver.Room;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Raid Player Names",
	description = "This plugin will log all player names when a CoX raid is starting",
	tags = {"combat", "raid", "pve", "pvm", "bosses", "cox", "names", "log"},
	enabledByDefault = false,
	type = PluginType.UTILITY
)
@Slf4j
public class RaidPlayerNamesPlugin extends Plugin {

	private static final String PLUGIN_NAME = "Raid name logger";
	private static final String ICON_FILE = "cox.png";

	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	Map<RaidRoom, Room> raidRoomRoomMap = new HashMap<>();

	private static final String RAID_START_MESSAGE = "The raid has begun!";

	private boolean inRaidChambers;

	private RaidPlayerNamesPanel panel;
	private NavigationButton navigationButton;

	@Override
	protected void startUp() throws Exception {
		panel = new RaidPlayerNamesPanel(this);
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), ICON_FILE);
		navigationButton = NavigationButton.builder()
				.tooltip(PLUGIN_NAME)
				.icon(icon)
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navigationButton);

		clientThread.invokeLater(() -> checkRaidPresence());
	}

	@Override
	protected void shutDown() throws Exception {
		inRaidChambers = false;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		checkRaidPresence();
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {

		if (inRaidChambers) {
			Actor actor = actorDeath.getActor();
			if (actor instanceof Player) {
				Player player = (Player) actor;

			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (inRaidChambers && event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION) {
			String message = Text.removeTags(event.getMessage());

			if (message.startsWith(RAID_START_MESSAGE)) {
				List<Player> players = client.getPlayers();
				List<String> people = new ArrayList<>();
				for (Player player : players) {
					log.info("Player in raid: " + player.getName());
					people.add(player.getName());
				}
				SwingUtilities.invokeLater(() -> panel.addPanel(people));
			}

		}
	}

	private void checkRaidPresence() {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}

		boolean setting = client.getVar(Varbits.IN_RAID) == 1;

		if (inRaidChambers != setting) {
			inRaidChambers = setting;
		}
	}
}