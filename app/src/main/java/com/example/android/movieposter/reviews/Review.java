package com.example.android.movieposter.reviews;

import com.google.gson.annotations.SerializedName;

public class Review {

    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_AUTHOR = "author";
    private static final String JSON_KEY_CONTENT = "content";
    private static final String JSON_KEY_URL = "url";

    @SerializedName(JSON_KEY_ID)
    public String mId;

    @SerializedName(JSON_KEY_AUTHOR)
    public String mAuthor;

    @SerializedName(JSON_KEY_CONTENT)
    public String mContent;

    @SerializedName(JSON_KEY_URL)
    public String mUrl;

    public Review(String id, String author, String content, String url){
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public String getId(){
        return mId;
    }

    public void setId(String newId){
        mId = newId;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public void setAuthor(String newAuthor){
        mAuthor = newAuthor;
    }

    public String getContent(){
        return mContent;
    }

    public void setContent(String newContent){
        mContent = newContent;
    }

    public String getUrl(){
        return mUrl;
    }

    public void setUrl(String newUrl){
        mUrl = newUrl;
    }
}
