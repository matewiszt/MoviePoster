package com.example.android.movieposter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Find all the views of activity_detail.xml
        TextView mTitleTextView = (TextView) findViewById(R.id.detail_title_tv);
        ImageView mPosterImageView = (ImageView) findViewById(R.id.detail_image_iv);
        TextView mSynopsisTextView = (TextView) findViewById(R.id.detail_synopsis_tv);
        TextView mRatingTextView = (TextView) findViewById(R.id.detail_rating_tv);
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.detail_date_tv);

        // Initialize the local variables for the Intent extras
        int id = 0;
        String title = null;
        String image = null;
        String synopsis = null;
        double rating = 0;
        String date = null;

        // If we have extras, check if the extras have the following keys and store them in the variables
        Bundle extras = getIntent().getExtras();
        if (extras != null){

            // The id is for later use
            if (extras.containsKey(MainActivity.DETAIL_KEY_ID)) {
                id = extras.getInt(MainActivity.DETAIL_KEY_ID);
            }
            if (extras.containsKey(MainActivity.DETAIL_KEY_TITLE)) {
                title = extras.getString(MainActivity.DETAIL_KEY_TITLE);
            }
            if (extras.containsKey(MainActivity.DETAIL_KEY_IMAGE)) {
                image = extras.getString(MainActivity.DETAIL_KEY_IMAGE);
            }
            if (extras.containsKey(MainActivity.DETAIL_KEY_SYNOPSIS)) {
                synopsis = extras.getString(MainActivity.DETAIL_KEY_SYNOPSIS);
            }
            if (extras.containsKey(MainActivity.DETAIL_KEY_RATING)) {
                rating = extras.getDouble(MainActivity.DETAIL_KEY_RATING);
            }
            if (extras.containsKey(MainActivity.DETAIL_KEY_DATE)) {
                date = extras.getString(MainActivity.DETAIL_KEY_DATE);
            }
        }

        // Set the Movie title as the title of the Activity
        setTitle(title);

        // Pass the data to the proper Views
        mTitleTextView.setText(title);
        Picasso.with(this).load(image).into(mPosterImageView);
        mPosterImageView.setContentDescription(title);
        mSynopsisTextView.setText(synopsis);
        mRatingTextView.setText(String.valueOf(rating));
        mReleaseDateTextView.setText(date);
    }
}
