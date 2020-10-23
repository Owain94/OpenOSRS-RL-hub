package com.osrsstreamers.handler;

import lombok.*;

import java.time.ZonedDateTime;

@Data
public class NearbyPlayer {

    ZonedDateTime lastSeen;
    StreamStatus status;
    String twitchName;

    public NearbyPlayer(String twitchName) {
        this.status = StreamStatus.STREAMER;
        this.twitchName = twitchName;
        this.lastSeen = ZonedDateTime.now();
    }

    public NearbyPlayer() {
        this.status = StreamStatus.NOT_STREAMER;
        this.lastSeen = ZonedDateTime.now();
    }

}
