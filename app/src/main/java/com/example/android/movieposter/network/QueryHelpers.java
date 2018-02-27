package com.example.android.movieposter.network;

import android.net.Uri;

import com.example.android.movieposter.BuildConfig;

public class QueryHelpers {

    //Define constants for API calls
    private static final String BASE_URL = "http://api.themoviedb.org/3";
    private static final String API_ENDPOINT_MOVIE = "movie";
    private static final String API_QUERY_PARAM_KEY = "api_key";
    private static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;
    private static final String API_QUERY_PARAM_LANG = "language";
    private static final String LANGCODE = "en-US";

    // Public constructor which we never use
    public QueryHelpers() {}

    /*
         * Compose the URL for the popular movies
         * @return String: the URL for the popular movies
         */
    public static String composeEndpointUrl(String endpoint) {

        Uri.Builder builder = new Uri.Builder();

        Uri uri = builder.encodedPath(BASE_URL)
                .appendPath(API_ENDPOINT_MOVIE)
                .appendEncodedPath(endpoint)
                .appendQueryParameter(API_QUERY_PARAM_LANG, LANGCODE)
                .appendQueryParameter(API_QUERY_PARAM_KEY, API_KEY).build();

        return uri.toString();

    }

}
