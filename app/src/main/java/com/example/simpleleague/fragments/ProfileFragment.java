package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private final ParseUser currentUser = ParseUser.getCurrentUser();
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private TextView tvPosts;

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
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        tvFollowing = view.findViewById(R.id.tvFollowing);
        tvPosts = view.findViewById(R.id.tvPosts);

        // Load profile image
        loadProfileImage();
        // Set the username
        tvUsername.setText(currentUser.getUsername());
        // Set the followers
        setFollowers();
        // Set the following
        setFollowing();
        // Set the posts
        setNumberPosts();
    }

    private void loadProfileImage() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile image = currentUser.getParseFile(User.KEY_PROFILE_IMAGE);
        if (image != null) {
            Glide.with(getContext())
                    .load(image.getUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .centerCrop()
                    .into(ivProfileImage);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.default_profile_image)
                    .centerCrop()
                    .into(ivProfileImage);
        }
    }

    private void setFollowing() {
        List<String> list = (List<String>) currentUser.get(User.KEY_FOLLOWING);
        if (list == null) {
            tvFollowing.setText(String.valueOf(0));
        } else {
            tvFollowing.setText(String.valueOf(list.size()));
        }
    }

    private void setFollowers() {
        List<String> list = (List<String>) currentUser.get(User.KEY_FOLLOWERS);
        if (list == null) {
            tvFollowers.setText(String.valueOf(0));
        } else {
            tvFollowers.setText(String.valueOf(list.size()));
        }
    }

    private void setNumberPosts() {
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