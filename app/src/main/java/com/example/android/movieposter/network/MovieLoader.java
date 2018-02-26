package com.example.android.movieposter.network;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.movieposter.Movie;

import java.util.List;


public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String mUrl;

    // Public constructor for the Loader
    public MovieLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {

        // If we don't have a URL, there is nothing to load
        if (mUrl == null) {
            return null;
        }

        // Call fetchPopularMoviesData from NetworkUtils with the URL
        List<Movie> movies = NetworkUtils.fetchPopularMoviesData(mUrl);
        return movies;
    }
}
