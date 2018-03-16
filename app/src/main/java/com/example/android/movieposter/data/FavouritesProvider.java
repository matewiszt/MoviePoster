package com.example.android.movieposter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.movieposter.data.FavouritesContract.FavouritesEntry;

public class FavouritesProvider extends ContentProvider {

    private static final String LOG_TAG = FavouritesProvider.class.getSimpleName();

    public static final int CODE_FAVOURITES = 100;
    public static final int CODE_FAVOURITES_ITEM = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavouritesDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        // Create a new instance of FavouritesDbHelper class
        mDbHelper = new FavouritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        switch (match){
            case CODE_FAVOURITES:

                // If the Uri matches the collection code, call the db query function with the params
                cursor = db.query(FavouritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVOURITES_ITEM:

                // If the Uri matches the item code, call the db query function on the specific item
                String[] args = {uri.getLastPathSegment()};
                cursor = db.query(FavouritesEntry.TABLE_NAME,
                        projection,
                        FavouritesEntry.COLUMN_MOVIE_ID + "=?",
                        args,
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        // If the contentValues is empty, return
        if (contentValues.size() == 0){

            return null;

        }

        long newRowId;
        int movieId = 0;
        String title = null;

        // Validate the data (the movie has a movieId and the title)

        if (contentValues.containsKey(FavouritesEntry.COLUMN_MOVIE_ID)){

            movieId = contentValues.getAsInteger(FavouritesEntry.COLUMN_MOVIE_ID);

        }

        if (contentValues.containsKey(FavouritesEntry.COLUMN_TITLE)){

            title = contentValues.getAsString(FavouritesEntry.COLUMN_TITLE);

        }

        if (movieId <= 0 || title == null){

            throw new IllegalArgumentException("A movie needs a valid movie id and a title");

        }

        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (match){
            case CODE_FAVOURITES:

                // Call the db insert function
                newRowId = db.insert(FavouritesEntry.TABLE_NAME, null, contentValues);

                if (newRowId == -1) {

                    Log.e(LOG_TAG, "Movie insert failed for URI: " + uri);

                } else {

                    // If the insert was successful, notify the ContentResolver about the change and return the new Uri
                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(uri, movieId);

                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        // If we don't have contentValues, simply return
        if (contentValues.size() == 0){

            return 0;

        }

        int numberOfRowsUpdated;
        int movieId = 0;
        String title = null;

        // Validate the data (the movie has a movieId and the title)

        if (contentValues.containsKey(FavouritesEntry.COLUMN_MOVIE_ID)){

            movieId = contentValues.getAsInteger(FavouritesEntry.COLUMN_MOVIE_ID);

        }

        if (contentValues.containsKey(FavouritesEntry.COLUMN_TITLE)){

            title = contentValues.getAsString(FavouritesEntry.COLUMN_TITLE);

        }

        if (movieId <= 0 || title == null){

            throw new IllegalArgumentException("A movie needs a valid movie id and a title");

        }

        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (match){
            case CODE_FAVOURITES:

                // Call the db update function with the input parameters
                numberOfRowsUpdated = db.update(FavouritesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case CODE_FAVOURITES_ITEM:

                // Call the db update function on the specific item
                String[] args = new String[]{uri.getLastPathSegment()};
                numberOfRowsUpdated = db.update(
                        FavouritesEntry.TABLE_NAME,
                        contentValues,
                        FavouritesEntry.COLUMN_MOVIE_ID + "=?",
                        args);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If the update was successful, notify the ContentResolver
        if (numberOfRowsUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numberOfRowsDeleted;

        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (match){
            case CODE_FAVOURITES:

                // Call the db update function with the input parameters
                numberOfRowsDeleted = db.delete(FavouritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_FAVOURITES_ITEM:

                // Call the db update function on the specific item
                String[] args = {uri.getLastPathSegment()};
                numberOfRowsDeleted = db.delete(FavouritesEntry.TABLE_NAME, FavouritesEntry.COLUMN_MOVIE_ID + "=?", args);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If the delete was successful, notify the ContentResolver
        if (numberOfRowsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_FAVOURITES:
                return FavouritesEntry.CONTENT_LIST_TYPE;
            case CODE_FAVOURITES_ITEM:
                return FavouritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Uri " + uri + "has no match with " + match);
        }
    }

    /*
     * Build a UriMatcher for the Favourites table
     */
    public static UriMatcher buildUriMatcher(){

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouritesContract.CONTENT_AUTHORITY, FavouritesContract.PATH_FAVOURITES, CODE_FAVOURITES);
        uriMatcher.addURI(FavouritesContract.CONTENT_AUTHORITY, FavouritesContract.PATH_FAVOURITES + "/#", CODE_FAVOURITES_ITEM);

        return uriMatcher;
    }
}
