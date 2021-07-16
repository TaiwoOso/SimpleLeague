package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Follow")
public class Follow extends ParseObject {

    public static final String TAG = "Follow";
    public static final String KEY_USER = "user";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void addFollower(ParseUser user) {
        addUnique(KEY_FOLLOWERS, user.getObjectId());
    }

    public List<String> getFollowers() {
        List<String> followers = null;
        try {
            followers = (List<String>) fetchIfNeeded().get(KEY_FOLLOWERS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return followers;
    }

    public void removeFollower(ParseUser user) {
        List<String> followers = getFollowers();
        followers.remove(user.getObjectId());
        put(KEY_FOLLOWERS, followers);
    }

    public void addFollowing(ParseUser user) {
        addUnique(KEY_FOLLOWING, user.getObjectId());
    }

    public List<String> getFollowing() {
        List<String> following = null;
        try {
            following = (List<String>) fetchIfNeeded().get(KEY_FOLLOWING);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return following;
    }

    public void removeFollowing(ParseUser user) {
        List<String> following = getFollowing();
        following.remove(user.getObjectId());
        put(KEY_FOLLOWING, following);
    }
}
