package com.example.simpleleague;

import android.content.Context;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ParseQueries {

    public static final String TAG = "ParseQueries";

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
