package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.PostsAdapter;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchPostsFragment extends SearchFragment {

    public static final String TAG = "SearchPostsFragment";
    RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize fields
        // Initialize fields
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<Post>();
        adapter = new PostsAdapter(getContext(), posts);
        // RecyclerView
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        // Get the posts from Parse
        queryPosts();
    }

    private void queryPosts() {
        // Get the current user
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Query from Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // Include User class
        query.include(Post.KEY_USER);
        // Send query to Parse
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
            }
        });
    }
}