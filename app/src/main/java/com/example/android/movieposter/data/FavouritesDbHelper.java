package com.example.android.movieposter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.movieposter.data.FavouritesContract.FavouritesEntry;

public class FavouritesDbHelper extends SQLiteOpenHelper {

    // Basic data for the DB
    private static final String DATABASE_NAME = "favourites.db";
    private static final int DATABASE_VERSION = 1;
    private static final String MODIFY_TABLE_COMMAND = "";

    // Public constructor for the DB helper
    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create table command for favourites table
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavouritesEntry.TABLE_NAME + " (" +
                FavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavouritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_IMAGE_PATH + " TEXT, " +
                FavouritesEntry.COLUMN_SYNOPSIS + " TEXT, " +
                FavouritesEntry.COLUMN_RATING + " DOUBLE, " +
                FavouritesEntry.COLUMN_RELEASE_DATE + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if (oldVersion < DATABASE_VERSION) {

            sqLiteDatabase.execSQL(MODIFY_TABLE_COMMAND);

        }
    }
}
