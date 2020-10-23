package com.osrsstreamers.twitch;


import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TwitchStream {


    @Expose(deserialize = false)
    private List<String> community_ids;

    private String game_id;

    private String id;

    @Expose(deserialize = false)
    private String language;

    @Expose(deserialize = false)
    private String started_at;

    @Expose(deserialize = false)
    private String[] tag_ids;

    @Expose(deserialize = false)
    private String thumbnail_url;

    private String title;

    private String type;

    private String user_id;

    private String user_name;

    private int viewer_count;

}
