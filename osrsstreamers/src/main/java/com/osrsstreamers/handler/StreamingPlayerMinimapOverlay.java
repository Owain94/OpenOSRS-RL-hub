package com.osrsstreamers.handler;

import com.osrsstreamers.OsrsStreamersConfig;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.util.Objects;

public class StreamingPlayerMinimapOverlay extends Overlay {

    @Inject
    private Client client;

    public StreamerHandler streamerHandler;

    private final OsrsStreamersConfig config;

    private static final Color TWITCH_COLOR = new Color(133, 76, 231);
    private static final Color OFFLINE_COLOR = new Color(169, 169, 169);

    @Inject
    private StreamingPlayerMinimapOverlay(OsrsStreamersConfig config)
    {
        this.config = config;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.showOnMinimap()) {
            client.getPlayers().stream().filter(player -> !player.equals(client.getLocalPlayer())).forEach(player -> {
                NearbyPlayer nearbyPlayer = streamerHandler.getNearbyPlayer(player.getName());
                if (Objects.nonNull(nearbyPlayer) && !StreamStatus.NOT_STREAMER.equals(nearbyPlayer.status)) {

                    Color color = OFFLINE_COLOR;
                    if (StreamStatus.LIVE.equals(nearbyPlayer.getStatus())) {
                        color = TWITCH_COLOR;
                    }
                    if (config.onlyShowStreamersWhoAreLive() && (nearbyPlayer.status.equals(StreamStatus.NOT_LIVE) || nearbyPlayer.status.equals(StreamStatus.STREAMER))) {
                        return;
                    }

                    final net.runelite.api.Point minimapLocation = player.getMinimapLocation();

                    String twitchHandle = "t.tv/" + nearbyPlayer.getTwitchName();

                    if (minimapLocation != null)
                    {
                        OverlayUtil.renderTextLocation(graphics, minimapLocation, twitchHandle, color);
                    }
                }
            });
        }
        return null;
    }

}
