package com.example.simpleleague.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class PostViewHolder extends ViewHolder {

    Context mContext;
    public ImageView ivProfileImage;
    public TextView tvUsername;
    public TextView tvTitle;
    public TextView tvBody;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvBody = itemView.findViewById(R.id.tvBody);
    }

    public void bind(Object object) {
        Post post = (Post) object;
        ParseFile profileImage = (ParseFile) post.getUser().get(User.KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(mContext)
                    .load(profileImage.getUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .centerCrop()
                    .into(ivProfileImage);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_profile_image)
                    .centerCrop()
                    .into(ivProfileImage);
        }
        tvUsername.setText(post.getUser().getUsername());
        tvTitle.setText(post.getTitle());
        tvBody.setText(post.getBody());
    }
}
