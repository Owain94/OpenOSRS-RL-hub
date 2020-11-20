package com.osrsstreamers.handler;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerifiedStreamers {

    private static final String VERIFIED_STREAMERS_LIST = "https://raw.githubusercontent.com/rhoiyds/osrs-streamers/master/resources/streamers.json";

    public Map<String, String> rsnToTwitchLoginMap;
    public List<Streamer> streamers;

    public VerifiedStreamers() {
        rsnToTwitchLoginMap = new HashMap<>();

        //Remote update without plug-in version update
        try {
            URL url = new URL(VERIFIED_STREAMERS_LIST);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            Type collectionType = new TypeToken<Collection<Streamer>>(){}.getType();
            Collection<Streamer> streamers = new Gson().fromJson(reader, collectionType);
            this.streamers = new ArrayList<>(streamers);
            streamers.forEach(streamer -> {
                streamer.characterNames.forEach(characterName -> rsnToTwitchLoginMap.put(characterName, streamer.twitchName));
            });
        } catch (Exception e) {
            log.error("Error loading streamer list from GitHub", e);
        }
    }

    public String getTwitchName(String rsn) {
        return this.rsnToTwitchLoginMap.get(rsn);
    }

    public boolean isVerifiedStreamer(String name) {
        return this.rsnToTwitchLoginMap.containsKey(name);
    }

}
