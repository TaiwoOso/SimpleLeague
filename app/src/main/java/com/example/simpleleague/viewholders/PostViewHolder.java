package com.example.simpleleague.viewholders;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.simpleleague.ParseFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.activities.UserDetailsActivity;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class PostViewHolder extends ViewHolder {

    public static final String TAG = "PostViewHolder";
    public Context mContext;
    public ImageView mIvProfileImage;
    public TextView mTvUsername;
    public TextView mTvTitle;
    public TextView mTvBody;
    public ImageButton mIbtnLike;
    public TextView mTvLikes;
    public ImageButton mIbtnDislike;
    public TextView mTvDislikes;
    public ImageButton mIbtnComment;
    public TextView mTvComments;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        mIvProfileImage = itemView.findViewById(R.id.ivProfileImage);
        mTvUsername = itemView.findViewById(R.id.tvUsername);
        mTvTitle = itemView.findViewById(R.id.tvTitle);
        mTvBody = itemView.findViewById(R.id.tvBody);
        mIbtnLike = itemView.findViewById(R.id.ibtnLike);
        mTvLikes = itemView.findViewById(R.id.tvLikes);
        mIbtnDislike = itemView.findViewById(R.id.ibtnDislike);
        mTvDislikes = itemView.findViewById(R.id.tvDislikes);
        mIbtnComment = itemView.findViewById(R.id.ibtnComment);
        mTvComments = itemView.findViewById(R.id.tvComments);
    }

    public void bind(Object object) {
        Post post = (Post) object;
        ParseFile profileImage = (ParseFile) post.getUser().get(User.KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(mContext).load(profileImage.getUrl()).placeholder(R.drawable.default_profile_image).centerCrop().into(mIvProfileImage);
        } else {
            Glide.with(mContext).load(R.drawable.default_profile_image).centerCrop().into(mIvProfileImage);
        }
        mTvUsername.setText(post.getUser().getUsername());
        mTvTitle.setText(post.getTitle());
        if (post.getBody() != null && !post.getBody().isEmpty()) {
            mTvBody.setVisibility(View.VISIBLE);
            mTvBody.setText(post.getBody());
        } else {
            mTvBody.setVisibility(View.GONE);
        }
        if (post.getLikes() != null) {
            mTvLikes.setText(String.valueOf(post.getLikes().size()));
        } else {
            mTvLikes.setText(String.valueOf(0));
        }
        if (post.getDislikes() != null) {
            mTvDislikes.setText(String.valueOf(post.getDislikes().size()));
        } else {
            mTvDislikes.setText(String.valueOf(0));
        }
        if (post.getComments() != null && !post.getLikes().isEmpty()) {
            mTvComments.setText(String.valueOf(post.getComments().size()));
        } else {
            mTvComments.setText(R.string.Comment);
        }
        // Change appearance of like button
        if (ParseFunctions.userLikesPost(post)) {
            mIbtnLike.setTag("Liked");
            mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
        } else {
            mIbtnLike.setTag("NotLiked");
            mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        }
        // Change appearance of dislike button
        if (ParseFunctions.userDislikesPost(post)) {
            mIbtnDislike.setTag("Disliked");
            mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.purple_500)));
        } else {
            mIbtnDislike.setTag("NotDisliked");
            mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        }
        // Listeners
        listeners(post);
    }

    public void listeners(Post post) {
        mIvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserDetailsActivity.class);
                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(new User(post.getUser())));
                mContext.startActivity(intent);
            }
        });
        mIbtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIbtnLike.getTag().equals("NotLiked")) {
                    ParseFunctions.likePost(post);
                    int likes = Integer.parseInt(mTvLikes.getText().toString())+1;
                    mTvLikes.setText(String.valueOf(likes));
                    mIbtnLike.setTag("Liked");
                    mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
                    if (mIbtnDislike.getTag().equals("Disliked")) {
                        ParseFunctions.removeDislikePost(post);
                        int dislikes = Integer.parseInt(mTvDislikes.getText().toString())-1;
                        mTvDislikes.setText(String.valueOf(dislikes));
                        mIbtnDislike.setTag("NotDisliked");
                        mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                    }
                } else {
                    ParseFunctions.removeLikePost(post);
                    int likes = Integer.parseInt(mTvLikes.getText().toString())-1;
                    mTvLikes.setText(String.valueOf(likes));
                    mIbtnLike.setTag("NotLiked");
                    mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
            }
        });
        mIbtnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIbtnDislike.getTag().equals("NotDisliked")) {
                    ParseFunctions.dislikePost(post);
                    int dislikes = Integer.parseInt(mTvDislikes.getText().toString())+1;
                    mTvDislikes.setText(String.valueOf(dislikes));
                    mIbtnDislike.setTag("Disliked");
                    mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.purple_500)));
                    if (mIbtnLike.getTag().equals("Liked")) {
                        ParseFunctions.removeLikePost(post);
                        int likes = Integer.parseInt(mTvLikes.getText().toString())-1;
                        mTvLikes.setText(String.valueOf(likes));
                        mIbtnLike.setTag("NotLiked");
                        mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                    }
                } else {
                    ParseFunctions.removeDislikePost(post);
                    int dislikes = Integer.parseInt(mTvDislikes.getText().toString())-1;
                    mTvDislikes.setText(String.valueOf(dislikes));
                    mIbtnDislike.setTag("NotDisliked");
                    mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
            }
        });
    }
}
