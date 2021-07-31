package com.example.simpleleague.models;

import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel
public class User {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_BIO = "bio";
    public static final String KEY_FOLLOW = "follow";
    public static final String KEY_FOLLOWERS_COUNT = "followersCount";
    public static final String KEY_TAGS = "tags";
    private ParseUser parseUser;

    // empty constructor needed by the Parceler library
    public User () {}

    public User (ParseUser user) { this.parseUser = user; }

    public ParseUser getParseUser() {
        return parseUser;
    }
}
