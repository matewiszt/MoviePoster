package com.example.android.movieposter.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews {

    private Reviews() {}

    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_TOTAL_RESULTS = "total_results";
    private static final String JSON_KEY_ID = "id";

    // Collection of trailers
    @SerializedName(JSON_KEY_RESULTS)
    public List<Review> items = null;

    // Collection of trailers
    @SerializedName(JSON_KEY_TOTAL_RESULTS)
    public int reviewCount;

    // ID of the movie which the trailers belong to
    @SerializedName(JSON_KEY_ID)
    public int movieId;

    public int getReviewCount() {
        return reviewCount;
    }

    public List<Review> getItems() {
        return items;
    }

    public int getMovieId() {
        return movieId;
    }
}
