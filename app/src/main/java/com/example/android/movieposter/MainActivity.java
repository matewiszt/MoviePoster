package com.example.android.movieposter;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterClickHandler {

    // Log tag
    private String LOG_TAG = getClass().getName();

    //Define constants for API calls
    private static final String BASE_URL = "http://api.themoviedb.org/3";
    private static final String API_ENDPOINT_POPULAR = "popular";
    private static final String API_ENDPOINT_MOVIE = "movie";
    private static final String API_QUERY_PARAM_KEY = "api_key";
    private static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;
    private static final String API_QUERY_PARAM_LANG = "language";
    private static final String LANGCODE = "en-US";
    private static final String API_QUERY_PARAM_PAGE = "page";

    public RecyclerView mRecyclerView;
    public MovieAdapter mAdapter;
    public int gridColumnCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new GridLayoutManager and set it to the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, gridColumnCount);
        mRecyclerView.setLayoutManager(layoutManager);

        // Initialize a new MovieAdapter and set it to the RecyclerView
        mAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        // Initialize a ConnectivityManager to get the active network info
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If the device is connected to the internet, initialize a Loader
        if (isConnected) {

            getLoaderManager().initLoader(0, null, this);

        }

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        // Return a new Loader with the composed URL
        return new MovieLoader(this, composeUrl());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        // Clear the adapter and if our list is not empty, fill it with the data returned by the Loader
        mAdapter.setMovieData(null);

        if (movies != null){
            mAdapter.setMovieData(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

        // Clear the adapter
        mAdapter.setMovieData(null);
    }

    @Override
    public void onClickHandler(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", movie.getId());
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("image", movie.getImageFullPath());
        intent.putExtra("synopsis", movie.getSynopsis());
        intent.putExtra("rating", movie.getRating());
        intent.putExtra("date", movie.getReleaseDate());
        startActivity(intent);
    }

    /*
         * Compose the URL for the popular movies
         * @return String: the URL for the popular movies
         */
    private String composeUrl() {

        Uri.Builder builder = new Uri.Builder();

        Uri uri = builder.encodedPath(BASE_URL).appendPath(API_ENDPOINT_MOVIE).appendPath(API_ENDPOINT_POPULAR)
                .appendQueryParameter(API_QUERY_PARAM_KEY, API_KEY)
                .appendQueryParameter(API_QUERY_PARAM_LANG, LANGCODE)
                .appendQueryParameter(API_QUERY_PARAM_PAGE, "1" ).build();

        return uri.toString();

    }
}
