package com.example.simpleleague.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simpleleague.CameraFunctions;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CAMERA_SERVICE;

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
        adapter = new ProfileAdapter(getContext(), posts);
        // Recycler View
        rvProfile.setAdapter(adapter);
        rvProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        // Query user's posts from Parse
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_USER, currentUser);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts by "+currentUser.getUsername()+".", e);
                    return;
                }
                Log.i(TAG, "Retrieved "+posts.size()+" post(s) by "+currentUser.getUsername()+".");
                // save received posts to list and notify adapter of new data
                adapter.addAll(currentUser, posts);
            }
        });
    }
}