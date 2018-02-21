package com.example.android.movieposter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = getClass().getName();

    private static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_ENDPOINT_TOP_RATED = "movie/top_rated";
    private static final String API_ENDPOINT_MOVIE = "movie/";
    private static final String LANGCODE = "en-US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imageView);

        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);
    }
}
