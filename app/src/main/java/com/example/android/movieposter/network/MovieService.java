package com.example.android.movieposter.network;

import com.example.android.movieposter.BuildConfig;
import com.example.android.movieposter.Movies;

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

    private MovieService(){}

    private interface MovieAPI {
        @GET("movie/{endpoint}")
        Call<Movies> fetchMovies(@Path("endpoint") String endpoint);

    }

    private static Retrofit getApiService() throws IOException {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient client = httpClient.addInterceptor(new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {
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
        // Create a very simple REST adapter which points to the BASE_URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static Call<Movies> getMovies(String endpoint) {

        Retrofit service = null;

        try {
            service = getApiService();

        } catch (IOException e){
            e.printStackTrace();
        }

        if ( service == null ) {
            return null;
        }

        MovieAPI movieAPI = service.create(MovieAPI.class);

        return movieAPI.fetchMovies(endpoint);

    }
}
