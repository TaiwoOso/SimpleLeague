package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

public class TrendingFragment extends FeedFragment {

    public static final String TAG = "TrendingFragment";

    private ParseUser mCurrentUser = ParseUser.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Queries posts for the user's Trending page
     * @param skips - tells Parse how much data to skip
     */
    @Override
    public void queryPosts(int skips) {
        List<String> userTags = (List<String>) mCurrentUser.get(User.KEY_TAGS);
        if (userTags == null) {
            queryPostsNullTags(skips);
            return;
        }
        queryPostsWithTags(skips/2, userTags);
        queryPostsWithoutTags(skips/2, userTags);
    }

    /**
     * Queries posts with most views and activity for users who don't have any tags
     * then sorts by custom algorithm in Post's compareTo
     * @param skips - tells Parse how much data to skip
     */
    private void queryPostsNullTags(int skips) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(20);
        query.setSkip(skips);
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_UPDATED_AT).addDescendingOrder(Post.KEY_VIEWS);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts for "+mCurrentUser.getUsername()+".", e);
                    return;
                }
                Collections.sort(posts);
                mAdapter.addAll(posts);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), posts.size());
            }
        });
    }

    /**
     * Queries posts with most views and activity that have user's tags
     * then sorts by custom algorithm in Post's compareTo
     * @param skips - tells Parse how much data to skip
     */
    private void queryPostsWithTags(int skips, List<String> userTags) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereContainedIn(Post.KEY_TAGS, userTags);
        query.setLimit(10);
        query.setSkip(skips);
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_UPDATED_AT).addDescendingOrder(Post.KEY_VIEWS);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts for "+mCurrentUser.getUsername()+".", e);
                    return;
                }
                Collections.sort(posts);
                mAdapter.addAll(posts);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), posts.size());
            }
        });
    }

    /**
     * Queries posts with most views and activity that don't have user's tags
     * then sorts by custom algorithm in Post's compareTo
     * @param skips - tells Parse how much data to skip
     */
    private void queryPostsWithoutTags(int skips, List<String> userTags) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereNotContainedIn(Post.KEY_TAGS, userTags);
        query.setLimit(10);
        query.setSkip(skips);
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_UPDATED_AT).addDescendingOrder(Post.KEY_VIEWS);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts for "+mCurrentUser.getUsername()+".", e);
                    return;
                }
                Collections.sort(posts);
                mAdapter.addAll(posts);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), posts.size());
            }
        });
    }

}