package com.example.android.movieposter.network;

import android.support.annotation.NonNull;

import com.example.android.movieposter.BuildConfig;
import com.example.android.movieposter.movies.Movies;
import com.example.android.movieposter.reviews.Reviews;
import com.example.android.movieposter.trailers.Trailers;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MovieService {

    //Define constants for API calls
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String API_QUERY_PARAM_KEY = "api_key";
    private static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;
    private static final String API_QUERY_PARAM_LANG = "language";
    private static final String LANGCODE = "en-US";

    // Create a private constructor because we won't create an instance of this class
    private MovieService(){}

    /*
    * Create an interface for the MovieAPI
    */
    private interface MovieAPI {
        @GET("movie/{endpoint}")
        Call<Movies> fetchMovies(@Path("endpoint") String endpoint);

        @GET("movie/{id}/videos")
        Call<Trailers> fetchTrailers(@Path("id") String id);

        @GET("movie/{id}/reviews")
        Call<Reviews> fetchReviews(@Path("id") String id);

    }

    /*
    * Create a standard API request with api key and English language parameter
    * @return Retrofit: the API service instance
    */
    private static Retrofit getApiService() throws IOException {

        // Create a new HTTP client and add an interceptor to always include api key and English language code
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient client = httpClient.addInterceptor(new Interceptor() {

            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                // Add constant parameters(api key, language) to all requests
                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter(API_QUERY_PARAM_KEY, API_KEY)
                        .addQueryParameter(API_QUERY_PARAM_LANG, LANGCODE)
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);

            }
        }).build();

        // Create a basic REST adapter which points to the BASE_URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    /*
    * Implementation of the MovieAPI interface
    * @return MovieAPI: the implemented interface
    */
    private static MovieAPI implementApi() {

        Retrofit service = null;

        try {
            service = getApiService();

        } catch (IOException e){
            e.printStackTrace();
        }

        if ( service == null ) {
            return null;
        }

        return service.create(MovieAPI.class);

    }

    /*
    * Implementation of the fetchMovies abstract method
    * @param endpoint: the endpoint (popular/top_rated) to query
    * @return Call<Movies>: a call which returns a Movies object
    */
    public static Call<Movies> getMovies(String endpoint) {

        MovieAPI api = implementApi();

        if (api != null) {
            return api.fetchMovies(endpoint);
        }

        return null;

    }

    public static Call<Trailers> getTrailers(int id) {

        MovieAPI api = implementApi();

        if (api != null) {
            return api.fetchTrailers(String.valueOf(id));
        }

        return null;

    }

    public static Call<Reviews> getReviews(int id) {

        MovieAPI api = implementApi();

        if (api != null) {
            return api.fetchReviews(String.valueOf(id));
        }

        return null;

    }
}
