package com.example.simpleleague.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

@ParseClassName("Comment")
public class Comment extends ParseObject implements Comparable<Comment> {

    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_USER = "user";
    public static final String KEY_BODY = "body";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_DISLIKES = "dislikes";

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

    public List<String> getDislikes() {
        return (List<String>) get(KEY_DISLIKES);
    }

    public void addDislike(String userId) {
        addUnique(KEY_DISLIKES, userId);
    }

    public void removeDislike(String userId) {
        removeAll(KEY_DISLIKES, Collections.singletonList(userId));
    }

    /**
     * Sorts comments where comments that are updated recently,
     * have more likes, less dislikes
     * are placed closer to the top of the list
     * @param o comment that is being compared to this comment
     * @return 0 if the comments are considered equal;
     * a value less than 0 if this comment is ranked higher than comment argument;
     * a value greater than 0 if this comment is ranked lower than comment argument
     */
    @Override
    public int compareTo(Comment o) {
        double score = -(this.getUpdatedAt().compareTo(o.getUpdatedAt()));
        score += compareLikes(o)*2;
        score -= compareDislikes(o)*1.2;
        score *= 100;
        return (int)score;
    }

    public int compareLikes(Comment o) {
        if (this.getLikes() == null && o.getLikes() == null) return 0;
        if (this.getLikes() == null) return 1;
        if (o.getLikes() == null) return -1;
        int val = -(this.getLikes().size()-o.getLikes().size());
        return Integer.compare(val, 0);
    }

    public int compareDislikes(Comment o) {
        if (this.getDislikes() == null && o.getDislikes() == null) return 0;
        if (this.getDislikes() == null) return 1;
        if (o.getDislikes() == null) return -1;
        int val = -(this.getDislikes().size()-o.getDislikes().size());
        return Integer.compare(val, 0);
    }
}
