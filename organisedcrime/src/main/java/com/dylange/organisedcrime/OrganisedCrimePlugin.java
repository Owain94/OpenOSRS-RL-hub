package com.dylange.organisedcrime;

import com.dylange.organisedcrime.config.OrganisedCrimeConfig;
import com.dylange.organisedcrime.models.GangInfo;
import com.dylange.organisedcrime.tools.InformationBoardTextReader;
import com.dylange.organisedcrime.tools.ViewStateMapper;
import com.dylange.organisedcrime.ui.LocationViewState;
import com.dylange.organisedcrime.ui.OrganisedCrimePanel;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.World;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.WorldResult;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dylange.organisedcrime.tools.WidgetConstants.GROUP_ID_INFORMATION_BOARD;
import static com.dylange.organisedcrime.tools.WidgetConstants.GROUP_ID_NO_INFORMATION_ATM;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Organised crime",
	description = "Keeps track of organised crime locations across worlds",
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
public class OrganisedCrimePlugin extends Plugin {
    private static final int PANEL_REFRESH_TICK_THRESHOLD = 50; // 50 ticks, 30 seconds.
    private static final int STALE_DATA_REFRESH_TICK_THRESHOLD = 50; // 50 ticks, 30 seconds.

    private NavigationButton navButton;
    private OrganisedCrimePanel panel;
    private int ticksSinceLastUiUpdate = 0;
    private int ticksSinceLastStaleDataCull = 0;

    private World quickHopTargetWorld;
    private int displaySwitcherAttempts = 0;

    private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;

    @Inject
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private WorldService worldService;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private OrganisedCrimeConfig config;

    public Map<Integer, GangInfo> gangInfoMap = new HashMap<>();

    @Provides
    OrganisedCrimeConfig provideOrganisedCrimeConfig(ConfigManager configManager) {
        return configManager.getConfig(OrganisedCrimeConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        panel = new OrganisedCrimePanel(config, this::worldClicked);

        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "icon.png");

        navButton = NavigationButton.builder()
                .tooltip("Organised Crime")
                .icon(icon)
                .priority(5)
                .panel(panel)
                .build();
        clientToolbar.addNavigation(navButton);

        updatePanelData(gangInfoMap);
    }

    @Override
    protected void shutDown() throws Exception {
        gangInfoMap.clear();
        clientToolbar.removeNavigation(navButton);
    }

    public void worldClicked(int world) {
        hop(world);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (++ticksSinceLastUiUpdate >= PANEL_REFRESH_TICK_THRESHOLD) {
            refreshPanel();
        }
        if (++ticksSinceLastStaleDataCull >= STALE_DATA_REFRESH_TICK_THRESHOLD) {
            // Only do this operation if there are still gang info items remaining.
            if (!gangInfoMap.isEmpty()) {
                clearStaleGangInfo();
            }
        }

        if (quickHopTargetWorld == null) {
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            client.openWorldHopper();

            if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS) {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.NORMAL)
                        .append("Failed to quick-hop after ")
                        .append(ChatColorType.HIGHLIGHT)
                        .append(Integer.toString(displaySwitcherAttempts))
                        .append(ChatColorType.NORMAL)
                        .append(" attempts.")
                        .build();

                chatMessageManager
                        .queue(QueuedMessage.builder()
                                .type(ChatMessageType.CONSOLE)
                                .runeLiteFormattedMessage(chatMessage)
                                .build());

                displaySwitcherAttempts = 0;
                quickHopTargetWorld = null;
            }
        } else {
            client.hopToWorld(quickHopTargetWorld);
            displaySwitcherAttempts = 0;
            quickHopTargetWorld = null;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("organised-crime")) {
            updatePanelData(gangInfoMap);
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (widgetLoaded.getGroupId() != GROUP_ID_INFORMATION_BOARD) return;
        if (widgetLoaded.getGroupId() == GROUP_ID_NO_INFORMATION_ATM) {
            gangInfoMap.remove(client.getWorld());
            updatePanelData(gangInfoMap);
            return;
        }

        try {
            final GangInfo gangInfo = InformationBoardTextReader.getDisplayedGangInfo(client);
            if (gangInfo != null) {
                gangInfoMap.put(gangInfo.getWorld(), gangInfo);
                updatePanelData(gangInfoMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            refreshPanel();
            clearStaleGangInfo();
        }
    }

    private void clearStaleGangInfo() {
        Map<Integer, GangInfo> gangInfoCopy = new HashMap<>();
        AtomicBoolean removedAnyStaleValue = new AtomicBoolean(false);
        gangInfoMap.forEach((world, gangInfo) -> {
            if (!gangInfo.getExpectedTime().isStale()) {
                gangInfoCopy.put(world, gangInfo);
            } else {
                removedAnyStaleValue.set(true);
            }
        });

        ticksSinceLastStaleDataCull = 0;
        if (removedAnyStaleValue.get()) {
            gangInfoMap = gangInfoCopy;
            updatePanelData(gangInfoMap);
        }
    }

    private void refreshPanel() {
        ticksSinceLastUiUpdate = 0;
        panel.refresh();
    }

    private void updatePanelData(Map<Integer, GangInfo> data) {
        final List<LocationViewState> listItems = ViewStateMapper.gangInfoMapToLocationListItems(data, config);
        SwingUtilities.invokeLater(() -> {
            if (listItems.isEmpty()) {
                panel.displayEmpty();
            } else {
                panel.display(listItems);
            }
        });
    }

    private void hop(int worldId) {
        WorldResult worldResult = worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        net.runelite.http.api.worlds.World world = worldResult.findWorld(worldId);
        if (world == null) {
            return;
        }

        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN) {
            // on the login screen we can just change the world by ourselves
            client.changeWorld(rsWorld);
            return;
        }

        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Quick-hopping to World ")
                .append(ChatColorType.HIGHLIGHT)
                .append(Integer.toString(world.getId()))
                .append(ChatColorType.NORMAL)
                .append("..")
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());

        quickHopTargetWorld = rsWorld;
        displaySwitcherAttempts = 0;
    }
}
