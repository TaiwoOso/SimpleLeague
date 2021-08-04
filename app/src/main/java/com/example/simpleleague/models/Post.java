package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

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
    public static final String KEY_VIEWS = "views";
    public static final String KEY_VIEWS_COUNT = "viewsCount";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_DISLIKES = "dislikes";
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

    public ParseFile getVideo() {
        return getParseFile(KEY_VIDEO);
    }

    public void setVideo(ParseFile parseFile) {
        put(KEY_VIDEO, parseFile);
    }

    public List<String> getTags() {
        return (List<String>) get(KEY_TAGS);
    }

    public void setTags(List<String> tags) {
        addAllUnique(KEY_TAGS, tags);
    }

    public List<String> getViews() {
        return (List<String>) get(KEY_VIEWS);
    }

    public void addView(String userId) {
        addUnique(KEY_VIEWS, userId);
    }

    public int getViewsCount() {
        Number count = getNumber(KEY_VIEWS_COUNT);
        if (count == null) return 0;
        return (int) count;
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

    public List<String> getComments() {
        return (List<String>) get(KEY_COMMENTS);
    }

    public void addComment(String commentId) {
        addUnique(KEY_COMMENTS, commentId);
    }

    /**
     * Sorts post where posts that are updated recently,
     * have more views, more likes, less dislikes, and more comments
     * are placed closer to the top of the list
     * @param o post that is being compared to this post
     * @return 0 if the posts are considered equal;
     * a value less than 0 if this post is ranked higher than post argument;
     * a value greater than 0 if this post is ranked lower than post argument
     */
    @Override
    public int compareTo(Post o) {
        double score = -(this.getUpdatedAt().compareTo(o.getUpdatedAt()));
        score += compareViews(o)*1.5;
        score += compareLikes(o)*2;
        score -= compareDislikes(o)*1.2;
        score += compareComments(o)*1.2;
        score *= 100;
        return (int)score;
    }

    public int compareViews(Post o) {
        if (this.getViews() == null && o.getViews() == null) return 0;
        if (this.getViews() == null) return 1;
        if (o.getViews() == null) return -1;
        int val = -(this.getViews().size()-o.getViews().size());
        return Integer.compare(val, 0);
    }

    public int compareLikes(Post o) {
        if (this.getLikes() == null && o.getLikes() == null) return 0;
        if (this.getLikes() == null) return 1;
        if (o.getLikes() == null) return -1;
        int val = -(this.getLikes().size()-o.getLikes().size());
        return Integer.compare(val, 0);
    }

    public int compareDislikes(Post o) {
        if (this.getDislikes() == null && o.getDislikes() == null) return 0;
        if (this.getDislikes() == null) return 1;
        if (o.getDislikes() == null) return -1;
        int val = -(this.getDislikes().size()-o.getDislikes().size());
        return Integer.compare(val, 0);
    }

    public int compareComments(Post o) {
        if (this.getComments() == null && o.getComments() == null) return 0;
        if (this.getComments() == null) return 1;
        if (o.getComments() == null) return -1;
        int val = -(this.getComments().size()-o.getComments().size());
        return Integer.compare(val, 0);
    }
}