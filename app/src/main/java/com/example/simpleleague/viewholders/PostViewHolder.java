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
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.R;
import com.example.simpleleague.activities.UserDetailsActivity;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.ParseFile;

import org.parceler.Parcels;

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
    public TextView tvDislikes;
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
        tvDislikes = itemView.findViewById(R.id.tvDislikes);
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
        if (post.getLikes() != null) {
            tvLikes.setText(String.valueOf(post.getLikes().size()));
        } else {
            tvLikes.setText(String.valueOf(0));
        }
        if (post.getDislikes() != null) {
            tvDislikes.setText(String.valueOf(post.getDislikes().size()));
        } else {
            tvDislikes.setText(String.valueOf(0));
        }
        if (post.getComments() != null && !post.getLikes().isEmpty()) {
            tvComments.setText(String.valueOf(post.getComments().size()));
        } else {
            tvComments.setText(R.string.Comment);
        }
        // Change appearance of like button
        if (ParseQueries.userLikesPost(post)) {
            ibtnLike.setTag("Liked");
            ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
        } else {
            ibtnLike.setTag("NotLiked");
            ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        }
        // Change appearance of dislike button
        if (ParseQueries.userDislikesPost(post)) {
            ibtnDislike.setTag("Disliked");
            ibtnDislike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.purple_500)));
        } else {
            ibtnDislike.setTag("NotDisliked");
            ibtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
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
                if (ibtnLike.getTag().equals("NotLiked")) {
                    ParseQueries.likePost(post);
                    int likes = Integer.parseInt(tvLikes.getText().toString())+1;
                    tvLikes.setText(String.valueOf(likes));
                    ibtnLike.setTag("Liked");
                    ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
                    if (ibtnDislike.getTag().equals("Disliked")) {
                        ParseQueries.removeDislikePost(post);
                        int dislikes = Integer.parseInt(tvDislikes.getText().toString())-1;
                        tvDislikes.setText(String.valueOf(dislikes));
                        ibtnDislike.setTag("NotDisliked");
                        ibtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                    }
                } else {
                    ParseQueries.removeLikePost(post);
                    int likes = Integer.parseInt(tvLikes.getText().toString())-1;
                    tvLikes.setText(String.valueOf(likes));
                    ibtnLike.setTag("NotLiked");
                    ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
            }
        });
        ibtnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ibtnDislike.getTag().equals("NotDisliked")) {
                    ParseQueries.dislikePost(post);
                    int dislikes = Integer.parseInt(tvDislikes.getText().toString())+1;
                    tvDislikes.setText(String.valueOf(dislikes));
                    ibtnDislike.setTag("Disliked");
                    ibtnDislike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.purple_500)));
                    if (ibtnLike.getTag().equals("Liked")) {
                        ParseQueries.removeLikePost(post);
                        int likes = Integer.parseInt(tvLikes.getText().toString())-1;
                        tvLikes.setText(String.valueOf(likes));
                        ibtnLike.setTag("NotLiked");
                        ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                    }
                } else {
                    ParseQueries.removeDislikePost(post);
                    int dislikes = Integer.parseInt(tvDislikes.getText().toString())-1;
                    tvDislikes.setText(String.valueOf(dislikes));
                    ibtnDislike.setTag("NotDisliked");
                    ibtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
            }
        });
    }
}
