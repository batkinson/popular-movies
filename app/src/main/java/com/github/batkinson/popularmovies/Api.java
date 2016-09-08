package com.github.batkinson.popularmovies;

import android.net.Uri;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Api {

    public static final String MOVIE_KEY = "movie_key";
    public static final String URI_KEY = "uri_key";
    public static final String API_PARAM_KEY = "api_key";

    public static final int IMAGE_WIDTH = 185;
    public static final int IMAGE_HEIGHT = 278;

    public static final String MOVIE_BASE_URI = "http://api.themoviedb.org/3/movie";
    public static final String POPULAR_URI = MOVIE_BASE_URI + "/popular";
    public static final String TOP_RATED_URI = MOVIE_BASE_URI + "/top_rated";
    public static final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p/w" + IMAGE_WIDTH;

    public static final String POSTER_PATH = "poster_path";
    public static final String RESULTS = "results";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String OVERVIEW = "overview";

    public static String getApiUri(String uriWithoutKey) {
        return Uri.parse(uriWithoutKey).buildUpon()
                .appendQueryParameter(API_PARAM_KEY, BuildConfig.MOVIE_DB_API_KEY).build().toString();
    }

    public static String getPosterUri(String relativeUri) {
        return Uri.parse(IMAGE_BASE_URI).buildUpon().appendEncodedPath(relativeUri).build().toString();
    }

    public static String getReleaseYear(String dateString) throws ParseException {
        DateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat displayFormat = new SimpleDateFormat("yyyy");
        return displayFormat.format(apiFormat.parse(dateString));

    }
}
