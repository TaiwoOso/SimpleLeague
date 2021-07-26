package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Parcel(analyze = Post.class)
@ParseClassName("Post")
public class Post extends ParseObject implements Comparable<Post> {

    public static final String KEY_USER = "user";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_LIKES = "likes";
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

    public List<String> getTags() {
        return (List<String>) get(KEY_TAGS);
    }

    public void setTags(List<String> tags) {
        addAllUnique(KEY_TAGS, tags);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseFile getVideo() {
        return getParseFile(KEY_VIDEO);
    }

    public void setVideo(ParseFile parseFile) {
        put(KEY_VIDEO, parseFile);
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

    public List<String> getComments() {
        return (List<String>) get(KEY_COMMENTS);
    }

    public void addComment(String commentId) {
        addUnique(KEY_COMMENTS, commentId);
    }

    @Override
    public int compareTo(Post o) {
        if (this.getLikes() == null && o.getLikes() == null) return 0;
        if (this.getLikes() == null) return o.getLikes().size();
        if (o.getLikes() == null) return -(this.getLikes().size());
        return -(this.getLikes().size()-o.getLikes().size());
    }
}
