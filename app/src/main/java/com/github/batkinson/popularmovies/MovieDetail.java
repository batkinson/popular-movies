package com.github.batkinson.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import static com.github.batkinson.popularmovies.Api.ORIGINAL_TITLE;
import static com.github.batkinson.popularmovies.Api.OVERVIEW;
import static com.github.batkinson.popularmovies.Api.POSTER_PATH;
import static com.github.batkinson.popularmovies.Api.RELEASE_DATE;
import static com.github.batkinson.popularmovies.Api.VOTE_AVERAGE;
import static com.github.batkinson.popularmovies.Api.getPosterUri;

public class MovieDetail {

    private String imageUrl;
    private String title;
    private String releaseYear;
    private String rating;
    private String overview;

    MovieDetail(JSONObject movie) throws JSONException {
        imageUrl = getPosterUri(movie.getString(POSTER_PATH));
        title = movie.getString(ORIGINAL_TITLE);
        try {
            releaseYear = Api.getReleaseYear(movie.getString(RELEASE_DATE));
        } catch (ParseException e) {
            // leave it null
        }
        rating = movie.getString(VOTE_AVERAGE);
        overview = movie.getString(OVERVIEW);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getRating() {
        return rating;
    }

    public String getOverview() {
        return overview;
    }
}
