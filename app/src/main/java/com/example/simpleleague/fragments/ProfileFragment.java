package com.example.simpleleague.fragments;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ProfileAdapter;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private final ParseUser currentUser = ParseUser.getCurrentUser();
    private RecyclerView rvProfile;
    private List<Post> posts;
    private ProfileAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize fields
        rvProfile = view.findViewById(R.id.rvProfile);
        posts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), posts, currentUser);
        
        // Recycler View
        rvProfile.setAdapter(adapter);
        rvProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Query posts from Parse
        queryPosts();
    }

    private void queryPosts() {
        // Query from Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Query posts with current user
        query.whereEqualTo(Post.KEY_USER, currentUser);
        // Include User class
        query.include(Post.KEY_USER);
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

    public static void loadProfileImage(ImageView ivProfileImage, Context mContext) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile image = currentUser.getParseFile(User.KEY_PROFILE_IMAGE);
        if (image != null) {
            Glide.with(mContext)
                    .load(image.getUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .centerCrop()
                    .into(ivProfileImage);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_profile_image)
                    .centerCrop()
                    .into(ivProfileImage);
        }
    }

    public static void setFollowing(TextView tvFollowing, ParseUser user) {
        List<String> list = (List<String>) user.get(User.KEY_FOLLOWING);
        if (list == null) {
            tvFollowing.setText(String.valueOf(0));
        } else {
            tvFollowing.setText(String.valueOf(list.size()));
        }
    }

    public static void setFollowers(TextView tvFollowers, ParseUser user) {
        List<String> list = (List<String>) user.get(User.KEY_FOLLOWERS);
        if (list == null) {
            tvFollowers.setText(String.valueOf(0));
        } else {
            tvFollowers.setText(String.valueOf(list.size()));
        }
    }

    public static void setNumberPosts(TextView tvPosts) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    tvPosts.setText(String.valueOf(count));
                } else {
                    Log.e(TAG, "Error getting # of posts", e);
                }
            }
        });
    }
}