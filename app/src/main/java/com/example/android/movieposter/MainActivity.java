package com.example.android.movieposter;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

    RecyclerView mRecyclerView;
    MovieAdapter mAdapter;
    int gridColumnCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new GridLayoutManager and set it to the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, gridColumnCount);
        mRecyclerView.setLayoutManager(layoutManager);

        // Initialize a new MovieAdapter and set it to the RecyclerView
        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        String requestUrl = composeUrl();
        new FetchMovieData().execute(requestUrl);
    }

    public class FetchMovieData extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            return NetworkUtils.fetchPopularMoviesData(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mAdapter.setMovieData(movies);
        }
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
