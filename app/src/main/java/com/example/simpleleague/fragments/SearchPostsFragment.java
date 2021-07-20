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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.PostsAdapter;
import com.example.simpleleague.models.Champion;
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
    private EndlessRecyclerViewScrollListener scrollListener;
    private SearchView svSearch;
    private Button btnSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize fields
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<Post>();
        adapter = new PostsAdapter(getContext(), posts);
        svSearch = view.findViewById(R.id.svSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        // RecyclerView
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(layout);
        // Get the posts from Parse
        queryPosts(0, "");
        // Search for specific queries
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                String search = svSearch.getQuery().toString();
                queryPosts(0, search);
            }
        });
        // Load more champions during scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                String search = svSearch.getQuery().toString();
                loadNextPostsFromParse(skips, search);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);
    }

    // Append the next "page" of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private void loadNextPostsFromParse(int skips, String search) {
        // Send an API request to retrieve appropriate "paginated" data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()
        queryPosts(skips, search);
    }

    private void queryPosts(int skips, String search) {
        // Get the current user
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Query from Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Search for substring
        query.whereContains(Post.KEY_TITLE, search);
        // limit query to [limit] champions
        int limit = 20;
        query.setLimit(limit);
        // skip this amount
        query.setSkip(skips);
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
                adapter.notifyItemRangeInserted(skips, skips+limit);
            }
        });
    }
}