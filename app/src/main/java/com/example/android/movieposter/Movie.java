package com.example.android.movieposter;

/*
 * Movie class to store data related to one movie
 */

public class Movie {

    private int mId;
    private String mOriginalTitle;
    private String mThumbnailImagePath;
    private String mSynopsis;
    private double mUserRating;
    private String mReleaseDate;

    //Public constructor for an instance of the Movie class
    public Movie(int id, String title, String imagePath, String synopsis, double rating, String releaseDate) {
        mId = id;
        mOriginalTitle = title;
        mThumbnailImagePath = imagePath;
        mSynopsis = synopsis;
        mUserRating = rating;
        mReleaseDate = releaseDate;
    }

    //Getter and setter methods for every property

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
