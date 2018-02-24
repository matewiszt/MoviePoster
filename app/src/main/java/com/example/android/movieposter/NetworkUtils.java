package com.example.android.movieposter;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    //Tag constant for logging
    private static final String LOG_TAG = "NetworkUtils";

    //JSON key constants for parsing the JSON
    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_TITLE = "original_title";
    private static final String JSON_KEY_IMAGE = "poster_path";
    private static final String JSON_KEY_SYNOPSIS = "overview";
    private static final String JSON_KEY_RATING = "vote_average";
    private static final String JSON_KEY_DATE = "release_date";

    //Error String constants
    private static final String URL_ERROR_TEXT = "URL creation failed";
    private static final String SERVER_ERROR_TEXT = "Server error";
    private static final String CONN_ERROR_TEXT = "Connection creation failed";
    private static final String JSON_ERROR_TEXT = "JSON parsing failed";
    private static final String HTTP_ERROR_TEXT = "HTTP request failed";

    //Public constructor which will be never used
    public NetworkUtils() {
    }

    /*
     * Fetch the data from the /movie/popular endpoint
     * @param urlString: the URL in String format that we want to query
     * @return List<Movie>: a list of Movie objects
     */
    public static List<Movie> fetchPopularMoviesData(String urlString) {

        URL requestUrl = createUrl(urlString);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(requestUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, HTTP_ERROR_TEXT, e);
        }

        List<Movie> movies = parsePopularJson(jsonResponse);
        return movies;
    }

    /*
     * Create a movie list from the data returned from an HTTP request
     * @param responseJson: the response from the HTTP request in String format
     * @return List<Movie>: a list of Movie objects created from the result
     */
    private static List<Movie> parsePopularJson(String responseJson) {

        // If the response is empty, just return null
        if (TextUtils.isEmpty(responseJson)) {
            return null;
        }

        // Create a new movie list for the results
        List<Movie> movies = new ArrayList<>();

        try {

            // Try to parse the result as JSONObject and get the results array
            JSONObject resultObject = new JSONObject(responseJson);

            if (resultObject.has(JSON_KEY_RESULTS)) {

                JSONArray resultsArray = resultObject.getJSONArray(JSON_KEY_RESULTS);

                for (int i = 0; i < resultsArray.length(); i++) {

                    // Loop through all the results and extract the data from them
                    JSONObject currentMovie = resultsArray.getJSONObject(i);
                    int id = 0;
                    String title = null;
                    String imagePath = null;
                    String synopsis = null;
                    double rating = 0;
                    String releaseDate = null;

                    if (currentMovie.has(JSON_KEY_ID)) {
                        id = currentMovie.getInt(JSON_KEY_ID);
                    }
                    if (currentMovie.has(JSON_KEY_TITLE)) {
                        title = currentMovie.getString(JSON_KEY_TITLE);
                    }
                    if (currentMovie.has(JSON_KEY_IMAGE)) {
                        imagePath = currentMovie.getString(JSON_KEY_IMAGE);
                    }
                    if (currentMovie.has(JSON_KEY_SYNOPSIS)) {
                        synopsis = currentMovie.getString(JSON_KEY_SYNOPSIS);
                    }
                    if (currentMovie.has(JSON_KEY_RATING)) {
                        rating = currentMovie.getDouble(JSON_KEY_RATING);
                    }
                    if (currentMovie.has(JSON_KEY_DATE)) {
                        releaseDate = currentMovie.getString(JSON_KEY_DATE);
                    }

                    // Create a Movie instance of every result and add them to the movie list
                    Movie movieInstance = new Movie(id, title, imagePath, synopsis, rating, releaseDate);
                    movies.add(movieInstance);
                }
            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, JSON_ERROR_TEXT, e);
        }

        return movies;

    }

    /*
     * Get the response from an InputStream
     * @param stream: the InputStream that we want to read the response from
     * @return String: the String read from the InputStream
     */

    private static String readFromStream(InputStream stream) throws IOException {

        StringBuilder responseString = new StringBuilder();

        // If we don't have an input stream, we cannot get the response String
        if (stream == null) {
            return null;
        }

        // Create a new InputStreamReader and a BufferedReader
        InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        // Read the buffered response from line to line and append it to the StringBuilder
        String line = bufferedReader.readLine();
        while (line != null) {
            responseString.append(line);
            line = bufferedReader.readLine();
        }

        // Convert the StringBuilder to a String and return it
        return responseString.toString();

    }

    /*
     * Make an HTTP request
     * @param url: the URL to which we want to send a request
     * @return String: the response String (unparsed JSON)
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String responseString = null;

        // If we don't have URL, we won't have a response, so return null
        if (url == null) {
            return null;
        }

        HttpURLConnection connection = null;
        InputStream responseStream = null;

        try {

            // Set the parameters of the connection and connect
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.connect();

            if (connection.getResponseCode() == 200) {

                // If the connection is successful, get the input stream and read the response String from it
                responseStream = connection.getInputStream();
                responseString = readFromStream(responseStream);

            } else {
                Log.e(LOG_TAG, SERVER_ERROR_TEXT);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, CONN_ERROR_TEXT, e);
        } finally {

            // If we are not disconnected yet, disconnect
            if (connection != null) {
                connection.disconnect();
            }

            // If the input stream is not closed yet, close it
            if (responseStream != null) {
                responseStream.close();
            }

        }

        return responseString;

    }

    /*
     * Create a URL from a String
     * @param urlString: the String from which we want to create a URL
     * @return URL: the properly formatted URL
     */
    private static URL createUrl(String urlString) {
        URL url = null;

        try {
            url = new URL(urlString);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, URL_ERROR_TEXT, e);
        }
        return url;

    }

}
