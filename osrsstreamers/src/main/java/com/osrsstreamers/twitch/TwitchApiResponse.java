package com.osrsstreamers.twitch;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.List;

@Data
public class TwitchApiResponse {

    public List<TwitchStream> data;

    @Expose(deserialize = false)
    private Object pagination;

}
