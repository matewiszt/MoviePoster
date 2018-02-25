package com.example.android.movieposter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

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
        public final TextView mTitle;
        public final ImageView mPoster;

        // Constructor for the ViewHolder class
        public MovieAdapterViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mPoster = (ImageView) view.findViewById(R.id.poster);
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
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        // Get the current Movie and its needed properties
        Movie actualMovie = mMovies.get(position);
        String actualTitle = actualMovie.getTitle();
        String imagePath = actualMovie.getImageFullPath();

        // Load the image of the Movie, and set the title (also as content description of the image)
        Picasso.with(mContext).load(imagePath).into(holder.mPoster);
        holder.mTitle.setText(actualTitle);
        holder.mPoster.setContentDescription(actualTitle);
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
