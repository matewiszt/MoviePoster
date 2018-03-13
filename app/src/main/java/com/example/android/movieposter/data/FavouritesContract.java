package com.example.android.movieposter.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavouritesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.movieposter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVOURITES = "favourites";

    public static final class FavouritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        // Table name for favourites
        public static final String TABLE_NAME = "favourites";

        // Column names for favourites
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_PATH = "image_path";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
