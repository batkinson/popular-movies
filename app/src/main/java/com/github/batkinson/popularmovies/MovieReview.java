package com.github.batkinson.popularmovies;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieReview {

    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String URL = "url";

    private String author;
    private String content;
    private String url;

    MovieReview(JSONObject review) throws JSONException {
        author = review.getString(AUTHOR);
        content = review.getString(CONTENT);
        url = review.getString(URL);
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Uri getUri() {
        return Uri.parse(url);
    }
}
