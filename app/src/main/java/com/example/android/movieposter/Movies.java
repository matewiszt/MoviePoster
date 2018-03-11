package com.example.android.movieposter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Movies class to store data related to the collection of movies
 */

public class Movies {

    private Movies(){}

    private static final String JSON_KEY_RESULTS = "results";

    // Collection of movies
    @SerializedName(JSON_KEY_RESULTS)
    public List<Movie> items = null;

    public List<Movie> getItems() {
        return items;
    }
}
