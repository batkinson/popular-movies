package com.github.batkinson.popularmovies;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import static com.github.batkinson.popularmovies.Api.getYouTubeUri;

public class MovieVideo {

    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String TYPE = "type";

    private String name;
    private String key;
    private String type;

    MovieVideo(JSONObject video) throws JSONException {
        name = video.getString(NAME);
        key = video.getString(KEY);
        type = video.getString(TYPE);
    }

    public String getName() {
        return name;
    }

    public boolean isTrailer() {
        return "trailer".equalsIgnoreCase(type);
    }

    public Uri getUri() {
        return getYouTubeUri(key);
    }
}
