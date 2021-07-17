package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel
public class User {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_BIO = "bio";
    public static final String KEY_FOLLOW = "follow";
    private ParseUser parseUser;

    // empty constructor needed by the Parceler library
    public User () {}

    public ParseUser getParseUser() {
        return parseUser;
    }

    public void setParseUser(ParseUser parseUser) {
        this.parseUser = parseUser;
    }
}
