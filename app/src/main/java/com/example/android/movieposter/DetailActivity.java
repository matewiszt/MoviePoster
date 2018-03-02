package com.example.android.movieposter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    // Butterknife inits
    @BindView(R.id.detail_title_tv) TextView mTitleTextView;
    @BindView(R.id.detail_image_iv) ImageView mPosterImageView;
    @BindView(R.id.detail_synopsis_tv) TextView mSynopsisTextView;
    @BindView(R.id.detail_rating_tv) TextView mRatingTextView;
    @BindView(R.id.detail_date_tv) TextView mReleaseDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Butterknife binding
        ButterKnife.bind(this);

        // Initialize the local variables for the Intent extras
        int id = 0;
        String title = null;
        String image = null;
        String synopsis = null;
        String rating = null;
        String date = null;
        Movie movie = null;

        // If we have extras, check if the extras have the following keys and store them in the variables
        Bundle extras = getIntent().getExtras();
        if (extras != null){

            if (extras.containsKey(MainActivity.DETAIL_MOVIE_KEY)) {
                movie = extras.getParcelable(MainActivity.DETAIL_MOVIE_KEY);
                title = movie.getTitle();
                image = movie.getImageFullPath();
                synopsis = movie.getSynopsis();
                rating = String.valueOf(movie.getRating());
                date = movie.getReleaseDate();
            }
        }

        // Set the Movie title as the title of the Activity
        setTitle(title);

        // Pass the data to the proper Views
        mTitleTextView.setText(title);
        Picasso.with(this).load(image).into(mPosterImageView);
        mPosterImageView.setContentDescription(title);
        mSynopsisTextView.setText(synopsis);
        mRatingTextView.setText(rating);
        mReleaseDateTextView.setText(date);
    }
}
