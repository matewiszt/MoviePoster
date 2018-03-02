package com.example.android.movieposter;

/*
 * Movie class to store data related to one movie
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    // Properties of the class
    private int mId;
    private String mTitle;
    private String mThumbnailImagePath;
    private String mSynopsis;
    private double mUserRating;
    private String mReleaseDate;

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

    // Public constructor for an instance of the Movie class
    public Movie(int id, String title, String imagePath, String synopsis, double rating, String releaseDate) {
        mId = id;
        mTitle = title;
        mThumbnailImagePath = imagePath;
        mSynopsis = synopsis;
        mUserRating = rating;
        mReleaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mThumbnailImagePath = in.readString();
        mSynopsis = in.readString();
        mUserRating = in.readDouble();
        mReleaseDate = in.readString();
    }

    // Getter and setter methods for every property except for getImageFullPath (it is dependant on mThumbnailImagePath)

    public int getId(){
        return mId;
    }

    public void setId(int newId){
        mId = newId;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String newTitle){
        mTitle = newTitle;
    }

    public String getImagePath(){
        return mThumbnailImagePath;
    }

    /*
     * Append the returned image path fragment to the base URL
     * @return String: a full path of an image
     */
    public String getImageFullPath(){
        return IMAGE_BASE_URL.concat(mThumbnailImagePath);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mThumbnailImagePath);
        parcel.writeString(mSynopsis);
        parcel.writeDouble(mUserRating);
        parcel.writeString(mReleaseDate);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
