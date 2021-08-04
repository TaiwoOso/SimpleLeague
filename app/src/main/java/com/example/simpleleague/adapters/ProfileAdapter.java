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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.ParseFunctions;
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
    private List<ParseUser> mUsers;
    private List<Post> mPosts;

    public ProfileAdapter(Context mContext, ParseUser user, List<Post> mPosts) {
        this.mContext = mContext;
        this.mUsers = new ArrayList<>();
        this.mUsers.add(user);
        this.mPosts = mPosts;
        notifyDataSetChanged();
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
             holder.bind(mUsers.get(position));
        } else {
            Post post = mPosts.get(position-1);
            holder.bind(post);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size()+mPosts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addAll(List<Post> list) {
        mPosts.addAll(list);
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    class UserViewHolder extends ViewHolder {

        private ImageView mIvProfileImage;
        private TextView mTvUsername;
        private TextView mTvBio;
        private TextView mTvFollowers;
        private TextView mTvFollowing;
        private TextView mTvPosts;
        private Button mBtnFollow;
        private ImageButton mIbtnAddProfileImage;
        private CardView mCvFollowers;
        private CardView mCvFollowing;
        private CardView mCvEditProfile;
        private ImageButton mIbtnEditProfile;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvProfileImage = itemView.findViewById(R.id.ivProfileImage);
            mTvUsername = itemView.findViewById(R.id.tvUsername);
            mTvBio = itemView.findViewById(R.id.tvBio);
            mTvFollowers = itemView.findViewById(R.id.tvFollowers);
            mTvFollowing = itemView.findViewById(R.id.tvFollowing);
            mTvPosts = itemView.findViewById(R.id.tvPosts);
            mBtnFollow = itemView.findViewById(R.id.btnFollow);
            mIbtnAddProfileImage = itemView.findViewById(R.id.ibtnAddProfileImage);
            mCvFollowers = itemView.findViewById(R.id.cvFollowers);
            mCvFollowing = itemView.findViewById(R.id.cvFollowing);
            mCvEditProfile = itemView.findViewById(R.id.cvEditProfile);
            mIbtnEditProfile = itemView.findViewById(R.id.ibtnEditProfile);
        }

        public void bind(Object object) {
            ParseUser user = (ParseUser) object;
            ParseFunctions.loadProfileImage(mIvProfileImage, mContext, user);
            mTvUsername.setText(user.getUsername());
            ParseFunctions.setBio(mTvBio, user);
            ParseFunctions.setFollowers(mTvFollowers, user);
            ParseFunctions.setFollowing(mTvFollowing, user);
            ParseFunctions.setNumberPosts(mTvPosts, user);
            // Change visibilities of views
            if (!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                mIbtnAddProfileImage.setVisibility(View.GONE);
                mCvEditProfile.setVisibility(View.GONE);
            }
            // Change appearance of follow button
            if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                mBtnFollow.setVisibility(View.GONE);
            } else if (ParseFunctions.userFollows(user)){
                mBtnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                mBtnFollow.setTextColor(mContext.getResources().getColor(R.color.black));
                mBtnFollow.setText(R.string.Followed);
            }
            listeners(user);
        }

        private void listeners(ParseUser user) {
            mBtnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBtnFollow.getText().equals("Follow")) {
                        ParseFunctions.followUser(user);
                        mBtnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        mBtnFollow.setTextColor(mContext.getResources().getColor(R.color.black));
                        mBtnFollow.setText(R.string.Followed);
                        int followers = Integer.parseInt(mTvFollowers.getText().toString())+1;
                        mTvFollowers.setText(String.valueOf(followers));
                    } else {
                        ParseFunctions.unfollowUser(user);
                        mBtnFollow.setBackgroundColor(mContext.getResources().getColor(R.color.blue_500));
                        mBtnFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                        mBtnFollow.setText(R.string.Follow);
                        int followers = Integer.parseInt(mTvFollowers.getText().toString())-1;
                        mTvFollowers.setText(String.valueOf(followers));
                    }
                }
            });
            mIbtnAddProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the photo
                    CameraFunctions.launchCamera(mContext, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    // Main Activity's onActivityResult() takes care of the rest
                }
            });
            mCvFollowers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserFollowersActivity.class);
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(new User(user)));
                    mContext.startActivity(intent);
                }
            });
            mCvFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserFollowingActivity.class);
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(new User(user)));
                    mContext.startActivity(intent);
                }
            });
            mIbtnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditProfileActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class PostViewHolder extends com.example.simpleleague.viewholders.PostViewHolder implements DoubleClickListener {

        private ImageView mIvLike;
        private AnimatedVectorDrawableCompat mAvdc;
        private AnimatedVectorDrawable mAvd;
        private LinearLayout mLlPostHeader;

        public PostViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            mIvLike = itemView.findViewById(R.id.ivLike);
            mLlPostHeader = itemView.findViewById(R.id.llPostHeader);
            itemView.setOnClickListener(new DoubleClick(this));
        }

        @Override
        public void bind(Object object) {
            super.bind(object);
            Post post = (Post) object;
            mLlPostHeader.setVisibility(View.GONE);
            String blurb;
            int blurbCount = 125;
            String body = post.getBody();
            if (body == null) return;
            if (body.length() > blurbCount) {
                blurb = body.substring(0, blurbCount).trim()+"...";
                mTvBody.setText(blurb);
            } else {
                mTvBody.setText(body);
            }
        }

        @Override
        public void onSingleClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = mPosts.get(position-1);
                Intent intent = new Intent(mContext, PostDetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                mContext.startActivity(intent);
            }
        }

        @Override
        public void onDoubleClick(View view) {
            Drawable drawable = mIvLike.getDrawable();
            mIvLike.setAlpha(0.70f);
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                mAvdc = (AnimatedVectorDrawableCompat) drawable;
                mAvdc.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                mAvd = (AnimatedVectorDrawable) drawable;
                mAvd.start();
            }
            if (mIbtnLike.getTag().equals("NotLiked")) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Post post = mPosts.get(position-1);
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
                }
            }
        }
    }
}
