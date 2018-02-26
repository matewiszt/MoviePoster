package com.example.android.movieposter;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movieposter.network.MovieLoader;
import com.example.android.movieposter.network.QueryHelpers;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterClickHandler {

    // Log tag
    private String LOG_TAG = getClass().getName();

    public static final String DETAIL_KEY_ID = "id";
    public static final String DETAIL_KEY_TITLE = "title";
    public static final String DETAIL_KEY_IMAGE = "image";
    public static final String DETAIL_KEY_SYNOPSIS = "synopsis";
    public static final String DETAIL_KEY_RATING = "rating";
    public static final String DETAIL_KEY_DATE = "date";

    public RecyclerView mRecyclerView;
    public MovieAdapter mAdapter;
    public TextView mEmptyView;
    public ProgressBar mProgressBar;

    public int gridColumnCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mEmptyView = (TextView) findViewById(R.id.empty_tv);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Create a new GridLayoutManager and set it to the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridColumnCount);
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

        } else {

            // If the device is NOT connected to the internet, show the empty text
            showEmptyText();

        }

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {

        showProgressBar();
        // Return a new Loader with the composed URL
        return new MovieLoader(this, QueryHelpers.composePopularEndpointUrl());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {

        // Clear the adapter and if our list is not empty, fill it with the data returned by the Loader
        mAdapter.setMovieData(null);

        if (movies != null){
            mAdapter.setMovieData(movies);
            showRecyclerView();
        } else {
            showEmptyText();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

        // Clear the adapter
        mAdapter.setMovieData(null);
    }

    @Override
    public void onClickHandler(Movie movie) {

        // Create an Intent to launch the DetailActivity and pass the current Movie data
        Intent detailLaunchIntent = new Intent(this, DetailActivity.class);
        detailLaunchIntent.putExtra(DETAIL_KEY_ID, movie.getId());
        detailLaunchIntent.putExtra(DETAIL_KEY_TITLE, movie.getTitle());
        detailLaunchIntent.putExtra(DETAIL_KEY_IMAGE, movie.getImageFullPath());
        detailLaunchIntent.putExtra(DETAIL_KEY_SYNOPSIS, movie.getSynopsis());
        detailLaunchIntent.putExtra(DETAIL_KEY_RATING, movie.getRating());
        detailLaunchIntent.putExtra(DETAIL_KEY_DATE, movie.getReleaseDate());

        // Start the DetailActivity
        startActivity(detailLaunchIntent);
    }

    /*
     * Hide the RecyclerView and the ProgressBar show the empty text
     */
    private void showEmptyText() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    /*
     * Hide the ProgressBar and the empty view and show the results
     */
    private void showRecyclerView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /*
     * Hide the results container and the empty view and show the ProgressBar
     */
    private void showProgressBar() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
