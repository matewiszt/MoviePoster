package com.example.android.movieposter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String LOG_TAG = "MovieAdapter";
    private static final String PICASSO_ERROR = "Picasso loading error";
    private ArrayList<Movie> mMovies;
    private Context mContext;
    private final MovieAdapterClickHandler mClickHandler;

    // Interface implementation for item click
    public interface MovieAdapterClickHandler {
        void onClickHandler(Movie movie);
    }

    // Public constructor
    public MovieAdapter(Context context, MovieAdapterClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    // The ViewHolder class of the MovieAdapter
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The title and the post of the Movie
        @BindView (R.id.title) TextView mTitleTextView;
        @BindView (R.id.poster) ImageView mPosterImageView;

        // Constructor for the ViewHolder class
        public MovieAdapterViewHolder(View view) {
            super(view);

            // Butterknife binding
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Movie movie = mMovies.get(getAdapterPosition());
            mClickHandler.onClickHandler(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create a layout from the list_item.xml and initalize the ViewHolder with it
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {

        // Get the current Movie and its needed properties
        Movie actualMovie = mMovies.get(position);
        final String actualTitle = actualMovie.getTitle();
        String imagePath = actualMovie.getImageFullPath();

        // Load the image of the Movie, and set the title (also as content description of the image)
        Picasso.with(mContext).load(imagePath).into(holder.mPosterImageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.mTitleTextView.setText(actualTitle);
                holder.mPosterImageView.setContentDescription(actualTitle);
            }

            @Override
            public void onError() {
                Log.e(LOG_TAG, PICASSO_ERROR);
                holder.mPosterImageView.setImageResource(R.drawable.poster_placeholder);
                holder.mTitleTextView.setText(actualTitle);
                holder.mPosterImageView.setContentDescription(actualTitle);
            }
        });

    }

    @Override
    public int getItemCount() {

        // If we don't have movies, return 0
        if (mMovies == null) {
            return 0;
        }

        // Otherwise, return the size of the Movie list
        return mMovies.size();
    }

    public void setMovieData(List<Movie> movies){

        // Refresh the movie list and notify the loader
        mMovies = (ArrayList<Movie>) movies;
        notifyDataSetChanged();
    }
}
