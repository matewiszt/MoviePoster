package com.example.android.movieposter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trailers {

    private Trailers() {}

    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_ID = "id";

    // Collection of trailers
    @SerializedName(JSON_KEY_RESULTS)
    public List<Trailer> items = null;

    // ID of the movie which the trailers belong to
    @SerializedName(JSON_KEY_ID)
    public int movieId;

    public int getMovieId() {
        return movieId;
    }

    public List<Trailer> getItems() {
        return items;
    }
}
