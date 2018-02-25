package com.example.android.movieposter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private int mId;
    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mSynopsisTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = findViewById(R.id.detail_title_tv);
        mPosterImageView = findViewById(R.id.detail_image_iv);
        mSynopsisTextView = findViewById(R.id.detail_synopsis_tv);
        mRatingTextView = findViewById(R.id.detail_rating_tv);
        mReleaseDateTextView = findViewById(R.id.detail_date_tv);

        String title = null;
        String image = null;
        String synopsis = null;
        double rating = 0;
        String date = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null){

            if (extras.containsKey(MainActivity.DETAIL_KEY_ID)) {
                mId = extras.getInt(MainActivity.DETAIL_KEY_ID);
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

        setTitle(title);

        mTitleTextView.setText(title);
        Picasso.with(this).load(image).into(mPosterImageView);
        mPosterImageView.setContentDescription(title);
        mSynopsisTextView.setText(synopsis);
        mRatingTextView.setText(String.valueOf(rating));
        mReleaseDateTextView.setText(date);
    }
}
