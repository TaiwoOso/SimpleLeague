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

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
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

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";
    private RecyclerView rvPosts;
    public PostsAdapter adapter;
    private List<Post> posts;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize fields
        rvPosts = view.findViewById(R.id.rvPosts);
        posts = new ArrayList<Post>();
        adapter = new PostsAdapter(getContext(), posts);
        // RecyclerView
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(layout);
        // Get the posts from Parse
        queryPosts(0);
        // Load more posts during scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryPosts(skips);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
    }

    public void queryPosts(int skips) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Get list of user IDs that current user follows
        Follow follow = (Follow) currentUser.get(User.KEY_FOLLOW);
        if (follow == null) {
            follow = ParseQueries.createFollow(currentUser);
        }
        List<String> following = follow.getFollowing();
        // Error handling
        if (following == null) {
            following = new ArrayList<>();
        }
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Query posts where author of post is followed by current user
        query.whereContainedIn(Post.KEY_USER, following);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        int limit = 20;
        query.setLimit(limit);
        query.setSkip(skips);
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
                adapter.notifyItemRangeInserted(skips, skips+limit);
            }
        });
    }
}