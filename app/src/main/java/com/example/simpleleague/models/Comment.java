package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_USER = "user";
    public static final String KEY_BODY = "body";
    public static final String KEY_LIKES = "likes";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public List<String> getLikes() {
        return (List<String>) get(KEY_LIKES);
    }

    public void addLike(String userId) {
        addUnique(KEY_LIKES, userId);
    }

    public void removeLike(String userId) {
        removeAll(KEY_LIKES, Collections.singletonList(userId));
    }

}
