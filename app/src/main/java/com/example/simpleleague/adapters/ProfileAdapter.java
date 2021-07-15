package com.example.simpleleague.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.fragments.ProfileFragment;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public static final String TAG = "ProfileAdapter";
    Context mContext;
    List<Post> posts;
    ParseUser user;

    public ProfileAdapter(Context mContext, List<Post> posts, ParseUser user) {
        this.mContext = mContext;
        this.posts = posts;
        this.user = user;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_profile, parent, false);
            return new UserViewHolder(itemView);
        }
        itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        if (position == 0) {
            holder.bind(user);
        } else {
            Post post = posts.get(position-1);
            holder.bind(post);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        return 1;
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bind(Object object);
    }

    class UserViewHolder extends ProfileAdapter.ViewHolder {

        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvFollowers;
        private TextView tvFollowing;
        private TextView tvPosts;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFollowers = itemView.findViewById(R.id.tvFollowers);
            tvFollowing = itemView.findViewById(R.id.tvFollowing);
            tvPosts = itemView.findViewById(R.id.tvPosts);
        }

        public void bind(Object object) {
            ParseUser user = (ParseUser) object;
            ProfileFragment.loadProfileImage(ivProfileImage, mContext);
            tvUsername.setText(user.getUsername());
            ProfileFragment.setFollowers(tvFollowers, user);
            ProfileFragment.setFollowing(tvFollowing, user);
            ProfileFragment.setNumberPosts(tvPosts);
        }
    }

    class PostViewHolder extends ProfileAdapter.ViewHolder implements View.OnClickListener {
        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvTitle;
        private TextView tvBody;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            itemView.setOnClickListener(this);
        }

        public void bind(Object object) {
            Post post = (Post) object;
            ivProfileImage.setVisibility(View.GONE);
            tvUsername.setVisibility(View.GONE);
            tvTitle.setText(post.getTitle());
            tvBody.setText(post.getBody());
        }

        @Override
        public void onClick(View v) {
            // Get the position
            int position = getAdapterPosition();
            // Make sure position is valid
            if (position != RecyclerView.NO_POSITION) {
                // Get the post at position
                Post post = posts.get(position-1);
                // Create an intent to display PostDetailsActivity
                Intent intent = new Intent(mContext, PostDetailsActivity.class);
                // Serialize the movie using parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                // Show the activity
                mContext.startActivity(intent);
            }
        }
    }
}
