package com.example.simpleleague.viewholders;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.UserDetailsActivity;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.google.android.gms.common.util.NumberUtils;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.text.NumberFormat;

public class PostViewHolder extends ViewHolder {

    public static final String TAG = "PostViewHolder";
    Context mContext;
    public ImageView ivProfileImage;
    public TextView tvUsername;
    public TextView tvTitle;
    public TextView tvBody;
    public ImageButton ibtnLike;
    public TextView tvLikes;
    public ImageButton ibtnDislike;
    public ImageButton ibtnComment;
    public TextView tvComments;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvBody = itemView.findViewById(R.id.tvBody);
        ibtnLike = itemView.findViewById(R.id.ibtnLike);
        tvLikes = itemView.findViewById(R.id.tvLikes);
        ibtnDislike = itemView.findViewById(R.id.ibtnDislike);
        ibtnComment = itemView.findViewById(R.id.ibtnComment);
        tvComments = itemView.findViewById(R.id.tvComments);
    }

    public void bind(Object object) {
        Post post = (Post) object;
        ParseFile profileImage = (ParseFile) post.getUser().get(User.KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(mContext).load(profileImage.getUrl()).placeholder(R.drawable.default_profile_image).centerCrop().into(ivProfileImage);
        } else {
            Glide.with(mContext).load(R.drawable.default_profile_image).centerCrop().into(ivProfileImage);
        }
        tvUsername.setText(post.getUser().getUsername());
        tvTitle.setText(post.getTitle());
        if (post.getBody() != null && !post.getBody().isEmpty()) {
            tvBody.setVisibility(View.VISIBLE);
            tvBody.setText(post.getBody());
        } else {
            tvBody.setVisibility(View.GONE);
        }
        if (post.getLikes() != null && !post.getLikes().isEmpty()) {
            tvLikes.setText(String.valueOf(post.getLikes().size()));
        } else {
            tvLikes.setText(R.string.Like);
        }
        if (post.getComments() != null) {
            tvComments.setText(String.valueOf(post.getComments().size()));
        } else {
            tvComments.setText(R.string.Comment);
        }
        // Change appearance of like button
        if (ParseQueries.userLikesPost(post)) {
            ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
        } else {
            ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        }
        // Listeners
        listeners(post);
    }

    public void listeners(Post post) {
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked on "+post.getUser().getUsername()+"'s profile.");
                Intent intent = new Intent(mContext, UserDetailsActivity.class);
                User user = new User();
                user.setParseUser(post.getUser());
                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                mContext.startActivity(intent);
            }
        });
        ibtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ibtnLike.getBackgroundTintList().equals(ColorStateList.valueOf(Color.BLACK))) {
                    ParseQueries.likePost(post);
                    int likes;
                    try {
                        likes = Integer.parseInt(tvLikes.getText().toString())+1;
                    } catch (NumberFormatException e) {
                        likes = 1;
                    }
                    tvLikes.setText(String.valueOf(likes));
                    ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
                    Log.i(TAG, "Liked");
                } else {
                    ParseQueries.unlikePost(post);
                    int likes = Integer.parseInt(tvLikes.getText().toString())-1;
                    if (likes == 0) {
                        tvLikes.setText(R.string.Like);
                    } else {
                        tvLikes.setText(String.valueOf(likes));
                    }
                    ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                    Log.i(TAG, "Unliked");
                }
            }
        });
    }
}
