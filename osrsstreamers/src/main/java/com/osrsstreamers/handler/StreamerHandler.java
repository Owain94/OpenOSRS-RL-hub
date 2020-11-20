package com.osrsstreamers.handler;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.osrsstreamers.OsrsStreamersConfig;
import com.osrsstreamers.twitch.TwitchApiResponse;
import com.osrsstreamers.twitch.TwitchStream;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerMenuOptionClicked;
import net.runelite.api.events.PlayerSpawned;

import net.runelite.api.util.Text;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.util.LinkBrowser;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StreamerHandler {

    private static final String WATCH_STREAM_ACTION = "Twitch";
    private static final String PLAYER_INDICATOR = "Follow";
    private static final String TWITCH_BASE = "https://twitch.tv";
    private static final String TWITCH_CLIENT_ID = "ifhhbwyqdp5p9fmhn33wvlsufsemp8";
    private static final String TWITCH_API_URL = "https://api.twitch.tv/helix/streams";
    private static final String TWITCH_USER_QUERY_FIELD = "user_login";
    private static final int MAX_UNSEEN_DURATION_BEFORE_REMOVAL = 1;
    private static final Long TWITCH_API_USER_SEARCH_LIMIT = 100L;

    private Map<String, NearbyPlayer> nearbyPlayers;

    private Client client;

    private OsrsStreamersConfig config;

    private Gson gson = new Gson();

    public VerifiedStreamers verifiedStreamers;

    public StreamerHandler(Client client, OsrsStreamersConfig config, EventBus eventBus) {
		eventBus.subscribe(MenuEntryAdded.class, this, this::onMenuEntryAdded);
		eventBus.subscribe(PlayerMenuOptionClicked.class, this, this::onPlayerMenuOptionClicked);
		eventBus.subscribe(PlayerSpawned.class, this, this::onPlayerSpawned);
		eventBus.subscribe(PlayerDespawned.class, this, this::onPlayerDespawned);

        this.client = client;
        this.config = config;
        this.verifiedStreamers = new VerifiedStreamers();
        this.nearbyPlayers = new HashMap<>();
        this.addAllNearbyPlayers();
    }

    public void addStreamerFromConsole(String rsn, String twitchName) {
        this.verifiedStreamers.rsnToTwitchLoginMap.put(rsn, twitchName);
        this.addNewNearbyPlayer(rsn);
    }

    private void addAllNearbyPlayers() {
        this.client.getPlayers().forEach(player -> {
            this.addNewNearbyPlayer(player.getName());
        });
    }

    private void addNewNearbyPlayer(String rsn) {
        nearbyPlayers.remove(rsn);
        if (verifiedStreamers.isVerifiedStreamer(rsn)) {
            nearbyPlayers.put(rsn, new NearbyPlayer(verifiedStreamers.getTwitchName(rsn)));
        } else {
            nearbyPlayers.put(rsn, new NearbyPlayer());
        }
    }

    public void onMenuEntryAdded(MenuEntryAdded event) {

        MenuEntry[] menuEntries = client.getMenuEntries();
        if (PLAYER_INDICATOR.equals(event.getOption())) {
            NearbyPlayer nearbyPlayer = this.nearbyPlayers.get(Text.removeTags(event.getTarget()).split("[(]")[0].trim());
            if (Objects.nonNull(nearbyPlayer) && !StreamStatus.NOT_STREAMER.equals(nearbyPlayer.getStatus())) {
                if (config.onlyShowStreamersWhoAreLive() && (nearbyPlayer.status.equals(StreamStatus.NOT_LIVE) || nearbyPlayer.status.equals(StreamStatus.STREAMER))) {
                    return;
                }
                menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);
                MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();
                menuEntry.setOption(WATCH_STREAM_ACTION);
                menuEntry.setTarget(event.getTarget());
                menuEntry.setOpcode(MenuOpcode.RUNELITE.getId());
                client.setMenuEntries(menuEntries);
            }
        }

    }

    public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event) {
        if (event.getMenuOption().equals(WATCH_STREAM_ACTION)) {
            openTwitchStream(Text.removeTags(event.getMenuTarget()));
        }
    }

    public void onPlayerSpawned(PlayerSpawned playerSpawned) {

        final String name = playerSpawned.getPlayer().getName();

        NearbyPlayer nearbyPlayer = nearbyPlayers.get(name);

         if (name.equals(client.getLocalPlayer().getName())) {
             return;
        }

        if (Objects.isNull(nearbyPlayer)) {
            this.addNewNearbyPlayer(name);
        } else {
            nearbyPlayer.setLastSeen(ZonedDateTime.now());
        }

    }

    public void onPlayerDespawned(PlayerDespawned playerDespawned) {

        final String name = playerDespawned.getPlayer().getName();

        NearbyPlayer nearbyPlayer = nearbyPlayers.get(name);

        // Players may be asynchronously removed from nearby players prior to de-spawn
        if (Objects.nonNull(nearbyPlayer)) {
            nearbyPlayer.setLastSeen(ZonedDateTime.now());
        }

    }

    private void getTwitchStreams(List<NearbyPlayer> nearbyStreamers) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(TWITCH_API_URL).newBuilder();
        nearbyStreamers.forEach(nearbyStreamer -> httpBuilder.addQueryParameter(TWITCH_USER_QUERY_FIELD, nearbyStreamer.twitchName));

        Request request = new Request.Builder().url(httpBuilder.build()).addHeader("Client-ID", TWITCH_CLIENT_ID).addHeader("Authorization", "Bearer " + config.userAccessToken()).build();

        try  {
            Response response = RuneLiteAPI.CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                response.body().close();
                log.debug("Error while retrieving stream information from Twitch API: {}", response.body());
                return;
            }
            InputStream in = response.body().byteStream();
            TwitchApiResponse twitchApiResponse = gson.fromJson(new InputStreamReader(in), TwitchApiResponse.class);

            for (NearbyPlayer streamer : nearbyStreamers) {
                Optional<TwitchStream> optionalTwitchStream = twitchApiResponse.data.stream().filter(twitchStream -> twitchStream.getUser_name().equalsIgnoreCase(streamer.twitchName)).findFirst();
                if (optionalTwitchStream.isPresent()) {
                    streamer.setStatus(StreamStatus.LIVE);
                    log.debug("Streamer {} is currently live streaming", streamer.twitchName);
                } else {
                    streamer.setStatus(StreamStatus.NOT_LIVE);
                    log.debug("Streamer {} is currently NOT live streaming", streamer.twitchName);
                }
            }
            response.body().close();
        }
        catch (JsonParseException | IllegalArgumentException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openTwitchStream(String rsn) {
        LinkBrowser.browse(TWITCH_BASE + "/" + verifiedStreamers.getTwitchName(rsn));
    }

    public void removeOldNearbyPlayers() {
        nearbyPlayers = nearbyPlayers.entrySet().stream().filter(stringNearbyPlayerEntry -> {
            return Duration.between(stringNearbyPlayerEntry.getValue().getLastSeen().toInstant(), Instant.now()).compareTo(Duration.ofMinutes(MAX_UNSEEN_DURATION_BEFORE_REMOVAL)) < 0 ||
                    client.getPlayers().stream().anyMatch(player -> player.getName().equals(stringNearbyPlayerEntry.getKey()));
        }).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    public void fetchStreamStatusOfUndeterminedStreamers() {
        List<NearbyPlayer> undeterminedStreamers = nearbyPlayers.entrySet().stream()
                .filter(stringNearbyPlayerEntry -> stringNearbyPlayerEntry.getValue().getStatus().equals(StreamStatus.STREAMER))
                .map(Map.Entry::getValue)
                .limit(TWITCH_API_USER_SEARCH_LIMIT)
                .collect(Collectors.toList());

        if (!undeterminedStreamers.isEmpty()) {
            this.getTwitchStreams(undeterminedStreamers);
        }
    }

    public NearbyPlayer getNearbyPlayer(String name) {
        return nearbyPlayers.get(name);
    }

}
