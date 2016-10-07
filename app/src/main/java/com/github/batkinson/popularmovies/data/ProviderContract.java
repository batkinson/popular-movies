package com.github.batkinson.popularmovies.data;

import android.net.Uri;

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;
import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.provider.BaseColumns._ID;
import static java.lang.String.format;

public class ProviderContract {

    public static final String AUTHORITY = "com.github.batkinson.popularmovies";

    public static final String DB_NAME = "movies";

    public static final Uri BASE_URI = Uri.parse(SCHEME_CONTENT + "://" + AUTHORITY);

    public static final class Favorite {

        public static final String PATH = "favorite";

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE = CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH;
        public static final String ITEM_CONTENT_TYPE = CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH;

        public static String TABLE = PATH;
        public static String COLUMN_MOVIE_ID = "movie_id";
        public static String TABLE_SQL = format("create table %s (%s integer primary key autoincrement, %s text);",
                TABLE, _ID, COLUMN_MOVIE_ID);
        public static String IDX_NAME = "movieid_idx";
        public static String INDEX_SQL = format("create index %s on %s(%s)",
                IDX_NAME, TABLE, COLUMN_MOVIE_ID);

        public static Uri getItemUri(long rowId) {
            return URI.buildUpon().appendPath(String.valueOf(rowId)).build();
        }
    }
}
