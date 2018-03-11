package com.example.android.movieposter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movieposter.movies.Movie;
import com.example.android.movieposter.movies.MovieAdapter;
import com.example.android.movieposter.movies.Movies;
import com.example.android.movieposter.network.MovieService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Log tag
    private String LOG_TAG = getClass().getName();

    // Intent extra keys - public because we use the same ones in the DetailActivity to return them
    public static final String DETAIL_MOVIE_KEY = "movie";

    private boolean mSettingsUpdated = false;

    // Butterknife inits
    @BindView(R.id.recycler_container) LinearLayout mRecyclerContainer;
    @BindView(R.id.recycler_title) TextView mRecyclerTitle;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.empty_tv) TextView mEmptyView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;

    private MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Butterknife binding
        ButterKnife.bind(this);

        // Create a new GridLayoutManager and set it to the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        // Initialize a new MovieAdapter and set it to the RecyclerView
        mAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        // Initialize a ConnectivityManager to get the active network info
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If the device is connected to the internet, load the movies
        if (isConnected) {

            loadMovies();

        } else {

            // If the device is NOT connected to the internet, show the empty text
            showEmptyText();

        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onClickHandler(Movie movie) {

        // Create an Intent to launch the DetailActivity and pass the current Movie object
        Intent detailLaunchIntent = new Intent(this, DetailActivity.class);
        detailLaunchIntent.putExtra(DETAIL_MOVIE_KEY, movie);

        // Start the DetailActivity
        startActivity(detailLaunchIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If the flag is true, restart the Loader and set the flag back to false
        if (mSettingsUpdated) {
            loadMovies();
            mSettingsUpdated = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        // Set the flag to true if the Settings have been updated
        mSettingsUpdated = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Create a menu item from the main.xml menu resource
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // If the users clicks on the settings button, the SettingsActivity is launched
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Load the movies via MovieService class
     */
    private void loadMovies() {

        // Show the progress bar
        showProgressBar();

        // Get the sort order from Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String endpoint = preferences.getString(getString(R.string.sort_order_key), getString(R.string.sort_order_popular_value));

        // Make a call with the endpoint
        Call<Movies> call = MovieService.getMovies(endpoint);

        if (call == null) {
            return;
        }

        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {

                // Clear the adapter
                mAdapter.setMovieData(null);

                // Get the body of the response
                Movies movies = response.body();

                // If movie list is not null, set as the adapter data
                if (movies != null) {
                    List<Movie> movieList = movies.getItems();
                    mAdapter.setMovieData(movieList);

                    //Set the title based on the Settings
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    String endpoint = preferences.getString(getString(R.string.sort_order_key), getString(R.string.sort_order_popular_value));
                    String title = "";
                    switch (endpoint){
                        case "top_rated":
                            title = getString(R.string.title_top_rated);
                            break;
                        case "popular":
                            title = getString(R.string.title_popular);
                    }
                    mRecyclerTitle.setText(title);

                    // Make the movie list visible
                    showRecyclerContainer();
                } else {
                    showEmptyText();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                call.cancel();
                showEmptyText();
            }
        });

    }

    /*
     * Hide the RecyclerView and the ProgressBar show the empty text
     */
    private void showEmptyText() {
        mRecyclerContainer.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    /*
     * Hide the ProgressBar and the empty view and show the results
     */
    private void showRecyclerContainer() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerContainer.setVisibility(View.VISIBLE);
    }

    /*
     * Hide the results container and the empty view and show the ProgressBar
     */
    private void showProgressBar() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mRecyclerContainer.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
