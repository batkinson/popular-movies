package com.github.batkinson.popularmovies.data;

import android.content.UriMatcher;

import static android.content.UriMatcher.NO_MATCH;
import static com.github.batkinson.popularmovies.data.ProviderContract.AUTHORITY;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.PATH;

class Matcher {

    private static UriMatcher instance;

    static final int FAVORITES = 1;
    static final int FAVORITE_ID = 2;

    public static UriMatcher getInstance() {
        if (instance == null) {
            instance = new UriMatcher(NO_MATCH);
            instance.addURI(AUTHORITY, PATH, FAVORITES);
            instance.addURI(AUTHORITY, PATH + "/#", FAVORITE_ID);
        }
        return instance;
    }
}
