package com.example.android.movieposter;

import com.google.gson.annotations.SerializedName;

public class Trailer {

    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_SITE = "site";
    private static final String JSON_KEY_KEY = "key";
    private static final String JSON_KEY_TYPE = "type";

    @SerializedName(JSON_KEY_ID)
    public String mId;

    @SerializedName(JSON_KEY_KEY)
    public String mKey;

    @SerializedName(JSON_KEY_NAME)
    public String mName;

    @SerializedName(JSON_KEY_SITE)
    public String mSite;

    @SerializedName(JSON_KEY_TYPE)
    public String mType;

    public Trailer(String id, String key, String name, String site, String type){
        mId = id;
        mKey = key;
        mName = name;
        mSite = site;
        mType = type;
    }

    public String getId(){
        return mId;
    }

    public void setId(String newId){
        mId = newId;
    }

    public String getKey(){
        return mKey;
    }

    public void setKey(String newKey){
        mId = newKey;
    }

    public String getName(){
        return mName;
    }

    public void setName(String newName){
        mName = newName;
    }

    public String getSite(){
        return mSite;
    }

    public void setSite(String newSite){
        mSite = newSite;
    }

    public String getType() {
        return mType;
    }

    public void setType(String newType) {
        mType = newType;
    }

}
