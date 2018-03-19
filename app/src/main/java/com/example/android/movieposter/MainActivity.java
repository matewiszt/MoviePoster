package com.example.android.movieposter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieposter.data.FavouritesContract.FavouritesEntry;
import com.example.android.movieposter.movies.Movie;
import com.example.android.movieposter.movies.MovieAdapter;
import com.example.android.movieposter.movies.Movies;
import com.example.android.movieposter.network.MovieService;

import java.util.ArrayList;
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

    // Key of the Movie Parcelable for the detail intent
    public static final String DETAIL_MOVIE_KEY = "movie";

    public static final String SORT_ORDER_FAVOURITE = "favourite";
    public static final String SORT_ORDER_POPULAR = "popular";
    public static final String SORT_ORDER_TOP_RATED = "top_rated";

    public static final String LIST_STATE_KEY = "list_state";

    private boolean mSettingsUpdated = false;

    // Butterknife inits
    @BindView(R.id.recycler_container) LinearLayout mRecyclerContainer;
    @BindView(R.id.recycler_title) TextView mRecyclerTitle;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.empty_tv) TextView mEmptyView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;

    private MovieAdapter mAdapter;
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Butterknife binding
        ButterKnife.bind(this);

        // Get back the list_state Parcelable
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }

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

        switch (id) {
            // If the users clicks on the settings button, the SettingsActivity is launched
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.action_delete:
                showFavouritesDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String sortOrder = preferences.getString(getString(R.string.sort_order_key), getString(R.string.sort_order_popular_value));

        // If the sort order is not favourite, don't show the delete menu item
        if (!sortOrder.equals(SORT_ORDER_FAVOURITE)) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LIST_STATE_KEY, mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    /*
         * Manage the delete of favourites
         */
    private void showFavouritesDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_delete_favourites);
        builder.setMessage(R.string.delete_dialog_message);

        // If the user confirms, delete all the favourites from the DB
        builder.setPositiveButton(R.string.delete_dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int numberOfRowsDeleted = getContentResolver().delete(FavouritesEntry.CONTENT_URI, null, null);

                if (numberOfRowsDeleted != 0) {

                    Toast.makeText(MainActivity.this, getString(R.string.delete_dialog_success), Toast.LENGTH_LONG).show();
                    loadMoviesFromDb();

                } else {

                    Toast.makeText(MainActivity.this, getString(R.string.delete_dialog_error), Toast.LENGTH_LONG).show();

                }
            }
        });

        // Otherwise, just dismiss the dialog
        builder.setNegativeButton(R.string.delete_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        builder.create().show();

    }

    /*
     * Load movies
     */
    private void loadMovies() {

        // Show the progress bar
        showProgressBar();

        // Get the sort order from Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(getString(R.string.sort_order_key), getString(R.string.sort_order_popular_value));

        //If the sort order is favourite, load the movies from the DB
        if (sortOrder.equals(SORT_ORDER_FAVOURITE)) {

            loadMoviesFromDb();

        } else {

            // Otherwise load movies from the API with the corresponding endpoint
            loadMoviesFromApi(sortOrder);

        }

    }

    /*
     * Load favourite movies from the DB
     */
    private void loadMoviesFromDb() {

        mAdapter.setMovieData(null);

        // All the columns
        String[] projection = {
                FavouritesEntry.COLUMN_TITLE,
                FavouritesEntry.COLUMN_MOVIE_ID,
                FavouritesEntry.COLUMN_IMAGE_PATH,
                FavouritesEntry.COLUMN_SYNOPSIS,
                FavouritesEntry.COLUMN_RATING,
                FavouritesEntry.COLUMN_RELEASE_DATE
        };

        // Query the DB
        Cursor cursor = getContentResolver().query(
                FavouritesEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        if (cursor == null) {
            return;
        }

        List<Movie> movies = new ArrayList<>();

        // Loop through all the rows in the Cursor and convert them into Movie objects and add them to a list of movies
        for (int i = 0; i < cursor.getCount(); i++){

            cursor.moveToPosition(i);

            String title = cursor.getString(cursor.getColumnIndex(FavouritesEntry.COLUMN_TITLE));
            int movieId = cursor.getInt(cursor.getColumnIndex(FavouritesEntry.COLUMN_MOVIE_ID));
            String imagePath = cursor.getString(cursor.getColumnIndex(FavouritesEntry.COLUMN_IMAGE_PATH));
            String synopsis = cursor.getString(cursor.getColumnIndex(FavouritesEntry.COLUMN_SYNOPSIS));
            double rating = cursor.getDouble(cursor.getColumnIndex(FavouritesEntry.COLUMN_RATING));
            String date = cursor.getString(cursor.getColumnIndex(FavouritesEntry.COLUMN_RELEASE_DATE));

            Movie movie = new Movie(movieId, title, imagePath, synopsis, rating, date);

            movies.add(movie);

        }

        // Close the cursor replace the title and set the movie data
        cursor.close();
        mRecyclerTitle.setText(getString(R.string.title_favourites));

        if (movies.size() != 0) {

            mAdapter.setMovieData(movies);
            // Make the movie list visible
            showRecyclerContainer();
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);

        } else {
            mEmptyView.setText(R.string.favourite_empty_text);
            showEmptyText();
        }

    }


    /*
     * Load the movies from the API via MovieService class
     * @param String endpoint: the API endpoint to query
     */
    private void loadMoviesFromApi(String endpoint) {

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
                        case SORT_ORDER_TOP_RATED:
                            title = getString(R.string.title_top_rated);
                            break;
                        case SORT_ORDER_POPULAR:
                            title = getString(R.string.title_popular);
                            break;
                        default:
                            title = getString(R.string.title_popular);
                    }
                    mRecyclerTitle.setText(title);

                    // Make the movie list visible
                    showRecyclerContainer();
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
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
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    /*
     * Hide the ProgressBar and the empty view and show the results
     */
    private void showRecyclerContainer() {
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
