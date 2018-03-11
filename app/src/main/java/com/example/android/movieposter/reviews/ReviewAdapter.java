package com.example.android.movieposter.reviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movieposter.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private ArrayList<Review> mReviews;
    private Context mContext;

    // Public constructor
    public ReviewAdapter(Context context) {
        mContext = context;
    }

    // The ViewHolder class of the ReviewAdapter
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        // The title and the post of the Review
        @BindView(R.id.review_item_content_tv) TextView mContentTextView;
        @BindView (R.id.review_item_author_tv) TextView mAuthorTextView;

        // Constructor for the ViewHolder class
        public ReviewAdapterViewHolder(View view) {
            super(view);

            // Butterknife binding
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create a layout from the list_item.xml and initalize the ViewHolder with it
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewAdapter.ReviewAdapterViewHolder holder, int position) {

        // Get the current Review and its needed properties
        Review actualReview = mReviews.get(position);
        String actualContent = actualReview.getContent();
        String actualAuthor = actualReview.getAuthor();

        holder.mContentTextView.setText(actualContent);
        holder.mAuthorTextView.setText(actualAuthor);

    }

    @Override
    public int getItemCount() {

        // If we don't have reviews, return 0
        if (mReviews == null) {
            return 0;
        }

        // Otherwise, return the size of the Review list
        return mReviews.size();
    }

    public void setReviewData(List<Review> reviews){

        // Refresh the review list and notify the loader
        mReviews = (ArrayList<Review>) reviews;
        notifyDataSetChanged();
    }
}
