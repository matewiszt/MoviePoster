package com.example.android.movieposter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieposter.data.FavouritesContract.FavouritesEntry;
import com.example.android.movieposter.movies.Movie;
import com.example.android.movieposter.network.MovieService;
import com.example.android.movieposter.reviews.Review;
import com.example.android.movieposter.reviews.ReviewAdapter;
import com.example.android.movieposter.reviews.Reviews;
import com.example.android.movieposter.trailers.Trailer;
import com.example.android.movieposter.trailers.TrailerAdapter;
import com.example.android.movieposter.trailers.Trailers;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterClickHandler {

    // Butterknife inits
    @BindView(R.id.detail_title_tv) TextView mTitleTextView;
    @BindView(R.id.detail_image_iv) ImageView mPosterImageView;
    @BindView(R.id.detail_synopsis_tv) TextView mSynopsisTextView;
    @BindView(R.id.detail_rating_tv) TextView mRatingTextView;
    @BindView(R.id.detail_date_tv) TextView mReleaseDateTextView;
    @BindView(R.id.detail_reviews_lv) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.detail_reviews_empty_tv) TextView mReviewEmptyView;
    @BindView(R.id.detail_trailers_lv) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.detail_trailers_empty_tv) TextView mTrailerEmptyView;
    @BindView(R.id.detail_favourite_button) RelativeLayout mFavouriteButton;
    @BindView(R.id.detail_favourite_icon) ImageView mFavouriteIcon;

    private int mId;
    private String mTitle;
    private String mImagePath;
    private String mSynopsis;
    private double mRating;
    private String mDate;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private Uri mUri;
    private boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Butterknife binding
        ButterKnife.bind(this);
        Movie movie;

        // If we have extras, check if the extras have the following keys and store them in the variables
        Bundle extras = getIntent().getExtras();
        if (extras != null){

            if (extras.containsKey(MainActivity.DETAIL_MOVIE_KEY)) {
                movie = extras.getParcelable(MainActivity.DETAIL_MOVIE_KEY);
                mId = movie.getId();
                mTitle = movie.getTitle();
                if (movie.getImagePath() != null){
                    mImagePath = movie.getImageFullPath();
                } else {
                    mImagePath = null;
                }
                mSynopsis = movie.getSynopsis();
                mRating = movie.getRating();
                mDate = movie.getReleaseDate();
            }
        }

        // Set the Movie title as the title of the Activity
        setTitle(mTitle);

        // If there is no poster accessible, set the image placeholder as poster
        if (mImagePath != null){
            Picasso.with(this).load(mImagePath).into(mPosterImageView);
        } else {
            mPosterImageView.setImageResource(R.drawable.poster_placeholder);
        }

        // Pass the data to the proper Views
        mTitleTextView.setText(mTitle);
        mPosterImageView.setContentDescription(mTitle);
        mSynopsisTextView.setText(mSynopsis);
        mRatingTextView.setText(String.valueOf(mRating));
        mReleaseDateTextView.setText(mDate);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewsRecyclerView.setAdapter(mReviewAdapter);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailersRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerAdapter = new TrailerAdapter(this, this);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);

        // Initialize a ConnectivityManager to get the active network info
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If the device is connected to the internet, initialize the API calls
        if (isConnected) {

            loadReviews();
            loadTrailers();

        } else {
            showReviewEmptyText();
            showTrailerEmptyText();
        }

        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFavourite){

                    insertMovie();

                } else {

                    deleteMovie();
                }

            }
        });
    }

    private void insertMovie() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouritesEntry.COLUMN_MOVIE_ID, mId);
        contentValues.put(FavouritesEntry.COLUMN_TITLE, mTitle);
        contentValues.put(FavouritesEntry.COLUMN_IMAGE_PATH, mImagePath);
        contentValues.put(FavouritesEntry.COLUMN_SYNOPSIS, mSynopsis);
        contentValues.put(FavouritesEntry.COLUMN_RATING, mRating);
        contentValues.put(FavouritesEntry.COLUMN_RELEASE_DATE, mDate);

        mUri = getContentResolver().insert(FavouritesEntry.CONTENT_URI, contentValues);

        if (mUri != null) {
            Toast.makeText(DetailActivity.this, String.format(getString(R.string.detail_add_favourite_text), mTitle), Toast.LENGTH_LONG).show();
            mFavouriteIcon.setImageResource(R.drawable.ic_favorite);
            isFavourite = true;
        }

    }

    private void deleteMovie() {

        Uri deleteUri = FavouritesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mId)).build();
        int numbersOfDeleted = getContentResolver().delete(deleteUri, null, null);

        if (numbersOfDeleted != 0) {
            Toast.makeText(DetailActivity.this, String.format(getString(R.string.detail_remove_favourite_text), mTitle), Toast.LENGTH_LONG).show();
            mFavouriteIcon.setImageResource(R.drawable.ic_favorite_border);
            isFavourite = false;
        }

    }

    /*
        * Load the reviews of the Movie
        */
    private void loadReviews() {

        // Make a call with the endpoint
        Call<Reviews> call = MovieService.getReviews(mId);

        if (call == null) {
            return;
        }

        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(@NonNull Call<Reviews> call, @NonNull Response<Reviews> response) {

                mReviewAdapter.setReviewData(null);

                Reviews reviews = response.body();

                if (reviews != null && reviews.getReviewCount() > 0) {

                    List<Review> reviewList = reviews.getItems();
                    mReviewAdapter.setReviewData(reviewList);
                    showReviewRecyclerContainer();
                } else {
                    showReviewEmptyText();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Reviews> call, @NonNull Throwable t) {
                call.cancel();
                showReviewEmptyText();
            }
        });

    }

    /*
    * Load the trailers of the Movie
    */
    private void loadTrailers() {

        // Make a call with the endpoint
        Call<Trailers> call = MovieService.getTrailers(mId);

        if (call == null) {
            return;
        }

        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(@NonNull Call<Trailers> call, @NonNull Response<Trailers> response) {

                mTrailerAdapter.setTrailerData(null);

                Trailers trailers = response.body();

                if (trailers != null && trailers.getItems().size() > 0) {

                    List<Trailer> trailerList = trailers.getItems();
                    mTrailerAdapter.setTrailerData(trailerList);
                    showTrailerRecyclerContainer();

                } else {
                    showTrailerEmptyText();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Trailers> call, @NonNull Throwable t) {
                call.cancel();
                showTrailerEmptyText();
            }
        });

    }

    @Override
    public void onClickHandler(Trailer trailer) {
        Uri url = Uri.parse(trailer.getFullPath());
        Intent launchTrailerIntent = new Intent(Intent.ACTION_VIEW, url);
        if (launchTrailerIntent.resolveActivity(getPackageManager()) != null){
            startActivity(launchTrailerIntent);
        }
    }

    /*
        * Hide the empty text and show the recycler container
        */
    private void showReviewRecyclerContainer(){
        mReviewEmptyView.setVisibility(View.INVISIBLE);
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    /*
   * Hide the recycler container and show the empty text
   */
    private void showReviewEmptyText(){
        mReviewsRecyclerView.setVisibility(View.INVISIBLE);
        mReviewEmptyView.setVisibility(View.VISIBLE);
    }

    /*
    * Hide the empty text and show the recycler container
    */
    private void showTrailerRecyclerContainer(){
        mTrailerEmptyView.setVisibility(View.INVISIBLE);
        mTrailersRecyclerView.setVisibility(View.VISIBLE);
    }

    /*
   * Hide the recycler container and show the empty text
   */
    private void showTrailerEmptyText(){
        mTrailersRecyclerView.setVisibility(View.INVISIBLE);
        mTrailerEmptyView.setVisibility(View.VISIBLE);
    }
}
