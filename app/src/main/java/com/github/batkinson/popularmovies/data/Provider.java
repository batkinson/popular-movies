package com.github.batkinson.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.provider.BaseColumns._ID;
import static com.github.batkinson.popularmovies.data.ProviderContract.DB_NAME;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.CONTENT_TYPE;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.ITEM_CONTENT_TYPE;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.TABLE;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.getItemUri;
import static java.lang.String.format;

public class Provider extends ContentProvider {

    private DatabaseHelper helper;

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext(), DB_NAME, null, 1);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (Matcher.getInstance().match(uri)) {
            case Matcher.FAVORITES:
                return CONTENT_TYPE;
            case Matcher.FAVORITE_ID:
                return ITEM_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c;
        switch (Matcher.getInstance().match(uri)) {
            case Matcher.FAVORITES:
                c = db.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case Matcher.FAVORITE_ID:
                c = db.query(TABLE, projection, selectionForItem(selection), selectionArgsForItem(uri, selectionArgs), null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransaction();
            switch (Matcher.getInstance().match(uri)) {
                case Matcher.FAVORITES:
                    long rowId = db.insertOrThrow(TABLE, null, values);
                    db.setTransactionSuccessful();
                    return getItemUri(rowId);
                default:
                    throw new IllegalArgumentException("unknown uri: " + uri);
            }
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransaction();
            int updated;
            switch (Matcher.getInstance().match(uri)) {
                case Matcher.FAVORITES:
                    updated = db.update(TABLE, values, selection, selectionArgs);
                    break;
                case Matcher.FAVORITE_ID:
                    updated = db.update(TABLE, values, selectionForItem(selection), selectionArgsForItem(uri, selectionArgs));
                    break;
                default:
                    throw new IllegalArgumentException("unknown uri: " + uri);
            }
            if (updated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            db.setTransactionSuccessful();
            return updated;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransaction();
            int deleted;
            switch (Matcher.getInstance().match(uri)) {
                case Matcher.FAVORITES:
                    deleted = db.delete(TABLE, selection, selectionArgs);
                    break;
                case Matcher.FAVORITE_ID:
                    deleted = db.delete(TABLE, selectionForItem(selection), selectionArgsForItem(uri, selectionArgs));
                    break;
                default:
                    throw new IllegalArgumentException("unknown uri: " + uri);
            }
            if (deleted > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            db.setTransactionSuccessful();
            return deleted;
        } finally {
            db.endTransaction();
        }
    }

    private String selectionForItem(String selection) {
        if (selection == null || selection.trim().isEmpty()) {
            return format("%s = ?", _ID);
        } else {
            return format("%s and %s = ?", selection, _ID);
        }
    }

    @NonNull
    private String[] selectionArgsForItem(@NonNull Uri uri, String[] selectionArgs) {
        String itemId = uri.getLastPathSegment();
        if (selectionArgs == null) {
            return new String[]{itemId};
        } else {
            String[] combinedArgs = new String[selectionArgs.length + 1];
            System.arraycopy(selectionArgs, 0, combinedArgs, 0, selectionArgs.length);
            combinedArgs[combinedArgs.length - 1] = itemId;
            return combinedArgs;
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        helper.close();
        super.shutdown();
    }
}

