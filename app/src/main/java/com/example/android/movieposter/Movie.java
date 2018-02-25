package com.example.android.movieposter;

/*
 * Movie class to store data related to one movie
 */

public class Movie {

    // Properties of the class
    private int mId;
    private String mOriginalTitle;
    private String mThumbnailImagePath;
    private String mSynopsis;
    private double mUserRating;
    private String mReleaseDate;

    private static final String IMAGE_HOME_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String IMAGE_DETAIL_BASE_URL = "http://image.tmdb.org/t/p/w342";

    // Public constructor for an instance of the Movie class
    public Movie(int id, String title, String imagePath, String synopsis, double rating, String releaseDate) {
        mId = id;
        mOriginalTitle = title;
        mThumbnailImagePath = imagePath;
        mSynopsis = synopsis;
        mUserRating = rating;
        mReleaseDate = releaseDate;
    }

    // Getter and setter methods for every property except for getImageFullPath (it is dependant on mThumbnailImagePath)

    public int getId(){
        return mId;
    }

    public void setId(int newId){
        mId = newId;
    }

    public String getTitle(){
        return mOriginalTitle;
    }

    public void setTitle(String newTitle){
        mOriginalTitle = newTitle;
    }

    public String getImagePath(){
        return mThumbnailImagePath;
    }

    /*
     * Append the returned image path fragment to the base URL
     * @return String: a full path of an image
     */
    public String getImageHomeFullPath(){
        return IMAGE_HOME_BASE_URL.concat(mThumbnailImagePath);
    }

    public String getImageDetailFullPath(){
        return IMAGE_DETAIL_BASE_URL.concat(mThumbnailImagePath);
    }

    public void setImagePath(String newImagePath){
        mThumbnailImagePath = newImagePath;
    }

    public String getSynopsis(){
        return mSynopsis;
    }

    public void setSynopsis(String newSynopsis){
        mSynopsis = newSynopsis;
    }

    public double getRating(){
        return mUserRating;
    }

    public void setRating(double newRating){
        mUserRating = newRating;
    }

    public String getReleaseDate(){
        return mReleaseDate;
    }

    public void setReleaseDate(String newDate){
        mReleaseDate = newDate;
    }
}
