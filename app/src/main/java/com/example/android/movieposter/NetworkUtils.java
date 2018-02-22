package com.example.android.movieposter;

import android.content.res.Resources;
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
    private static final String LOG_TAG = "NetworkUtils: ";
    
    private static final Resources res = Resources.getSystem();

    //Public constructor which will be never used
    public NetworkUtils(){}

    /*
     * Fetch the data from the /movie/popular endpoint
     * @param urlString: the URL in String format that we want to query
     * @return List<Movie>: a list of Movie objects
     */
    public static List<Movie> fetchPopularMoviesData(String urlString){

        URL requestUrl = createUrl(urlString);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(requestUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, res.getString(R.string.http_request_error), e);
        }

        List<Movie> movies = parsePopularJson(jsonResponse);
        return movies;
    }

    /*
     * Create a movie list from the data returned from an HTTP request
     * @param responseJson: the response from the HTTP request in String format
     * @return List<Movie>: a list of Movie objects created from the result
     */
    private static List<Movie> parsePopularJson(String responseJson){

        // If the response is empty, just return null
        if (TextUtils.isEmpty(responseJson)){
            return null;
        }

        // Create a new movie list for the results
        List<Movie> movies = new ArrayList<>();

        try {

            //Try to parse the result as JSONObject and get the results array
            JSONObject resultObject = new JSONObject(responseJson);
            JSONArray resultsArray = resultObject.getJSONArray(res.getString(R.string.json_key_results));

            for (int i = 0; i <= resultsArray.length(); i++){

                //Loop through all the results and extract the data from them
                JSONObject currentMovie = resultsArray.getJSONObject(i);
                int id = currentMovie.getInt(res.getString(R.string.json_key_id));
                String title = currentMovie.getString(res.getString(R.string.json_key_title));
                String imagePath = currentMovie.getString(res.getString(R.string.json_key_image));
                String synopsis = currentMovie.getString(res.getString(R.string.json_key_synopsis));
                double rating = currentMovie.getDouble(res.getString(R.string.json_key_rating));
                String releaseDate = currentMovie.getString(res.getString(R.string.json_key_date));

                //Create a Movie instance of every result and add them to the movie list
                Movie movieInstance = new Movie(id, title, imagePath, synopsis, rating, releaseDate);
                movies.add(movieInstance);
            }

        } catch (JSONException e){

            Log.e(LOG_TAG, res.getString(R.string.server_error_text), e);
        }

        return movies;

    }

    /*
     * Get the response from an InputStream
     * @param stream: the InputStream that we want to read the response from
     * @return String: the String read from the InputStream
     */

    private static String readFromStream(InputStream stream) throws IOException{

        StringBuilder responseString = new StringBuilder();

        //If we don't have an input stream, we cannot get the response String
        if (stream == null){
            return null;
        }

        //Create a new InputStreamReader and a BufferedReader
        InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        //Read the buffered response from line to line and append it to the StringBuilder
        String line = bufferedReader.readLine();
        while(line != null){
            responseString.append(line);
            line = bufferedReader.readLine();
        }

        //Convert the StringBuilder to a String and return it
        return responseString.toString();

    }

    /*
     * Make an HTTP request
     * @param url: the URL to which we want to send a request
     * @return String: the response String (unparsed JSON)
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String responseString = null;

        //If we don't have URL, we won't have a response, so return null
        if (url == null) {
            return null;
        }

        HttpURLConnection connection = null;
        InputStream responseStream = null;

        try {

            //Set the parameters of the connection and connect
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.connect();

            if (connection.getResponseCode() == 200){

                //If the connection is successful, get the input stream and read the response String from it
                responseStream = connection.getInputStream();
                responseString = readFromStream(responseStream);
            } else {
                Log.e(LOG_TAG, res.getString(R.string.server_error_text));
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, res.getString(R.string.connection_creation_error), e);
        } finally {

            //If we are not disconnected yet, disconnect
            if (connection != null){
                connection.disconnect();
            }

            //If the input stream is not closed yet, close it
            if (responseStream != null){
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
    private static URL createUrl(String urlString){
        URL url = null;

        try {
            url = new URL(urlString);

        } catch (MalformedURLException e){
            Log.e(LOG_TAG, res.getString(R.string.url_creation_error_text), e);
        }
        return url;

    }

}
