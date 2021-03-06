package com.github.batkinson.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import static com.github.batkinson.popularmovies.Api.ID;
import static com.github.batkinson.popularmovies.Api.POSTER_PATH;
import static com.github.batkinson.popularmovies.Api.getPosterUri;

public class MovieDetail {

    private static final String ORIGINAL_TITLE = "original_title";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String OVERVIEW = "overview";

    private long id;
    private String imageUrl;
    private String title;
    private String releaseYear;
    private String rating;
    private String overview;

    MovieDetail(JSONObject movie) throws JSONException {
        id = movie.getLong(ID);
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

    public long getId() {
        return id;
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
