package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends FeedFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void queryPosts() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> userTags = (List<String>) currentUser.get(User.KEY_TAGS);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        if (userTags != null && !userTags.isEmpty()) {
            query.whereContainedIn(Post.KEY_TAGS, userTags);
        }
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts for "+currentUser.getUsername()+".", e);
                    return;
                }
                Log.i(TAG, "Retrieved "+posts.size()+" post(s) for "+currentUser.getUsername()+".");
                // save received posts to list and notify adapter of new data
                adapter.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }


}