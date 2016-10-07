package com.github.batkinson.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.INDEX_SQL;
import static com.github.batkinson.popularmovies.data.ProviderContract.Favorite.TABLE_SQL;

class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_SQL);
        db.execSQL(INDEX_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL(INDEX_SQL);
        }
    }
}
