package com.example.android.movieposter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovies;

    public MovieAdapter() {

    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTitle;

        public MovieAdapterViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.title);
        }

    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String actualTitle = mMovies.get(position).getTitle();
        holder.mTitle.setText(actualTitle);
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    public void setMovieTitles(List<Movie> movies){
        mMovies = (ArrayList<Movie>) movies;
        notifyDataSetChanged();
    }
}
