package com.example.simpleleague.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.activities.EditProfileActivity;
import com.example.simpleleague.activities.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.activities.UserFollowersActivity;
import com.example.simpleleague.activities.UserFollowingActivity;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.example.simpleleague.viewholders.ViewHolder;
import com.parse.ParseUser;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final String TAG = "ProfileAdapter";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 76;
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
        private CardView cvFollowers;
        private CardView cvFollowing;
        private CardView cvEditProfile;
        private ImageButton ibtnEditProfile;

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
            cvFollowers = itemView.findViewById(R.id.cvFollowers);
            cvFollowing = itemView.findViewById(R.id.cvFollowing);
            cvEditProfile = itemView.findViewById(R.id.cvEditProfile);
            ibtnEditProfile = itemView.findViewById(R.id.ibtnEditProfile);
        }

        public void bind(Object object) {
            ParseUser parseUser = (ParseUser) object;
            ParseQueries.loadProfileImage(ivProfileImage, mContext, parseUser);
            tvUsername.setText(parseUser.getUsername());
            ParseQueries.setBio(tvBio, parseUser);
            ParseQueries.setFollowers(tvFollowers, parseUser);
            ParseQueries.setFollowing(tvFollowing, parseUser);
            ParseQueries.setNumberPosts(tvPosts, parseUser);
            // Change visibilities of views
            if (!parseUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                ibtnAddProfileImage.setVisibility(View.GONE);
                cvEditProfile.setVisibility(View.GONE);
            }
            // Change appearance of follow button
            if (parseUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                btnFollow.setVisibility(View.GONE);
            } else if (ParseQueries.userFollows(parseUser)){
                btnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                btnFollow.setTextColor(mContext.getResources().getColor(R.color.black));
                btnFollow.setText(R.string.Followed);
            }
            // Listeners
            listeners(parseUser);
        }

        private void listeners(ParseUser parseUser) {
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnFollow.getText().equals("Follow")) {
                        ParseQueries.followUser(parseUser);
                        btnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        btnFollow.setTextColor(mContext.getResources().getColor(R.color.black));
                        btnFollow.setText(R.string.Followed);
                        int followers = Integer.parseInt(tvFollowers.getText().toString())+1;
                        tvFollowers.setText(String.valueOf(followers));
                        Log.i(TAG, "Followed");
                    } else {
                        ParseQueries.unfollowUser(parseUser);
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
                    CameraFunctions.launchCamera(mContext, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    // Main Activity's onActivityResult() takes care of the rest
                }
            });
            cvFollowers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserFollowersActivity.class);
                    User user = new User();
                    user.setParseUser(parseUser);
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    mContext.startActivity(intent);
                }
            });
            cvFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserFollowingActivity.class);
                    User user = new User();
                    user.setParseUser(parseUser);
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    mContext.startActivity(intent);
                }
            });
            ibtnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditProfileActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class PostViewHolder extends com.example.simpleleague.viewholders.PostViewHolder implements DoubleClickListener {

        private ImageView ivLike;
        private AnimatedVectorDrawableCompat avdc;
        private AnimatedVectorDrawable avd;

        public PostViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            ivLike = itemView.findViewById(R.id.ivLike);
            itemView.setOnClickListener(new DoubleClick(this));
        }

        @Override
        public void bind(Object object) {
            super.bind(object);
            Post post = (Post) object;
            ivProfileImage.setVisibility(View.GONE);
            tvUsername.setVisibility(View.GONE);
            tvTitle.setText(post.getTitle());
            String blurb;
            int blurbCount = 125;
            String body = post.getBody();
            if (body == null) return;
            if (body.length() > blurbCount) {
                blurb = body.substring(0, blurbCount).trim()+"...";
                tvBody.setText(blurb);
            } else {
                tvBody.setText(body);
            }
        }

        @Override
        public void onSingleClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position-1);
                Intent intent = new Intent(mContext, PostDetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                mContext.startActivity(intent);
            }
        }

        @Override
        public void onDoubleClick(View view) {
            Drawable drawable = ivLike.getDrawable();
            ivLike.setAlpha(0.70f);
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                avdc = (AnimatedVectorDrawableCompat) drawable;
                avdc.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                avd = (AnimatedVectorDrawable) drawable;
                avd.start();
            }
            if (ibtnLike.getBackgroundTintList().equals(ColorStateList.valueOf(Color.BLACK))) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Post post = posts.get(position-1);
                    ParseQueries.likePost(post);
                    int likes;
                    try {
                        likes = Integer.parseInt(tvLikes.getText().toString())+1;
                    } catch (NumberFormatException e) {
                        likes = 1;
                    }
                    tvLikes.setText(String.valueOf(likes));
                    ibtnLike.setTag("Liked");
                    ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
                }
            }
        }
    }
}
