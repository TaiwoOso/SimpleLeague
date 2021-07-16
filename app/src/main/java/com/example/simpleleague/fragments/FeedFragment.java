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

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> posts;

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
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        // Get the posts from Parse
        queryPosts();
    }

    private void queryPosts() {
        // Get the current user
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
        // Query from Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Query posts where author of post is followed by current user
        // Include User class
        query.include(Post.KEY_USER);
        // ASK ABOUT OLD CODE EXECUTING!!!
        query.whereContainedIn(Post.KEY_USER, following);
        // Send query to Parse
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                    return;
                }
                // log posts retrieved
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getBody() +", username: " + post.getUser().getUsername());
                }
                // save received posts to list and notify adapter of new data
                adapter.addAll(posts);
            }
        });
    }
}