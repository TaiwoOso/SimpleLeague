package com.example.simpleleague.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.fragments.ProfileFragment;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.example.simpleleague.viewholders.PostViewHolder;
import com.example.simpleleague.viewholders.ViewHolder;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final String TAG = "ProfileAdapter";
    private Context mContext;
    private List<ParseUser> user;
    private List<Post> posts;

    public ProfileAdapter(Context mContext, List<Post> posts) {
        this.mContext = mContext;
        this.user = new ArrayList<>();
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_profile, parent, false);
            return new UserViewHolder(itemView);
        }
        itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
             holder.bind(user.get(position));
        } else {
            Post post = posts.get(position-1);
            holder.bind(post);
        }
    }

    @Override
    public int getItemCount() {
        return user.size()+posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addAll(ParseUser parseUser, List<Post> list) {
        user.add(parseUser);
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class UserViewHolder extends ViewHolder {

        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvBio;
        private TextView tvFollowers;
        private TextView tvFollowing;
        private TextView tvPosts;
        private Button btnFollow;
        private ImageButton ibtnAddProfileImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBio = itemView.findViewById(R.id.tvBio);
            tvFollowers = itemView.findViewById(R.id.tvFollowers);
            tvFollowing = itemView.findViewById(R.id.tvFollowing);
            tvPosts = itemView.findViewById(R.id.tvPosts);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            ibtnAddProfileImage = itemView.findViewById(R.id.ibtnAddProfileImage);
        }

        public void bind(Object object) {
            ParseUser user = (ParseUser) object;
            ParseQueries.loadProfileImage(ivProfileImage, mContext, user);
            tvUsername.setText(user.getUsername());
            ParseQueries.setBio(tvBio, user);
            ParseQueries.setFollowers(tvFollowers, user);
            ParseQueries.setFollowing(tvFollowing, user);
            ParseQueries.setNumberPosts(tvPosts, user);
            // Change visibility of add profile image button
            if (!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                ibtnAddProfileImage.setVisibility(View.GONE);
            }
            // Change appearance of follow button
            if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                btnFollow.setVisibility(View.GONE);
            } else if (ParseQueries.userFollows(user)){
                btnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                btnFollow.setTextColor(mContext.getResources().getColor(R.color.black));
                btnFollow.setText(R.string.Followed);
            }
            // Listeners
            listeners(user);
        }

        private void listeners(ParseUser user) {
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnFollow.getText().equals("Follow")) {
                        ParseQueries.followUser(user);
                        btnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        btnFollow.setTextColor(mContext.getResources().getColor(R.color.black));
                        btnFollow.setText(R.string.Followed);
                        int followers = Integer.parseInt(tvFollowers.getText().toString())+1;
                        tvFollowers.setText(String.valueOf(followers));
                        Log.i(TAG, "Followed");
                    } else {
                        ParseQueries.unfollowUser(user);
                        btnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.purple_200));
                        btnFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                        btnFollow.setText(R.string.Follow);
                        int followers = Integer.parseInt(tvFollowers.getText().toString())-1;
                        tvFollowers.setText(String.valueOf(followers));
                        Log.i(TAG, "Unfollowed");
                    }
                }
            });
            ibtnAddProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the photo
                    CameraFunctions.launchCamera(mContext);
                    // Main Activity's onActivityResult() takes care of the rest
                }
            });
        }
    }

    class PostViewHolder extends com.example.simpleleague.viewholders.PostViewHolder implements View.OnClickListener {

        public PostViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bind(Object object) {
            Post post = (Post) object;
            ivProfileImage.setVisibility(View.GONE);
            tvUsername.setVisibility(View.GONE);
            tvTitle.setText(post.getTitle());
            String blurb;
            int blurbCount = 125;
            String body = post.getBody();
            if (body == null) return;
            if (body.length() > blurbCount) {
                blurb = body.substring(0, blurbCount)+"...";
                tvBody.setText(blurb);
            } else {
                tvBody.setText(body);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position-1);
                Intent intent = new Intent(mContext, PostDetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                mContext.startActivity(intent);
            }
        }
    }
}
