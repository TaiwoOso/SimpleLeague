package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

@Parcel(analyze = Post.class)
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_COMMENTS = "comments";

    // empty constructor needed by the Parceler library
    public Post() {}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public List<String> getComments() {
        return (List<String>) get(KEY_COMMENTS);
    }

    public void addComment(Comment comment) {
        addUnique(KEY_COMMENTS, comment);
    }

}
