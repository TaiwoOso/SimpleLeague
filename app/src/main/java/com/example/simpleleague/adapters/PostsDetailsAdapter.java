package com.example.simpleleague.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.DateFunctions;
import com.example.simpleleague.ParseFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.activities.UserDetailsActivity;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.example.simpleleague.viewholders.ViewHolder;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostsDetailsAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final String TAG = "PostsDetailsAdapter";

    private Context mContext;
    private List<Post> mPosts;
    private List<Comment> mComments;

    public PostsDetailsAdapter(Context mContext, Post post, List<Comment> comments) {
        this.mContext = mContext;
        this.mPosts = new ArrayList<>();
        this.mPosts.add(post);
        this.mComments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_post_details, parent, false);
            return new PostViewHolder(view);
        }
        view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            holder.bind(mPosts.get(position));
        } else {
            Comment comment = mComments.get(position-1);
            holder.bind(comment);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size()+mComments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addAll(List<Comment> list) {
        mComments.addAll(list);
    }


    class PostViewHolder extends com.example.simpleleague.viewholders.PostViewHolder {

        private TextView mTvTag1;
        private TextView mTvTag2;
        private ImageView mImageView;
        private VideoView mVideoView;
        private ImageButton mIbtnPlayVideo;
        private LinearLayout mLLTags;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView, PostsDetailsAdapter.this.mContext);
            mTvTag1 = itemView.findViewById(R.id.tvTag1);
            mTvTag2 = itemView.findViewById(R.id.tvTag2);
            mImageView = itemView.findViewById(R.id.imageView);
            mVideoView = itemView.findViewById(R.id.videoView);
            mIbtnPlayVideo = itemView.findViewById(R.id.ibtnPlayVideo);
            mLLTags = itemView.findViewById(R.id.llTags);
        }

        @Override
        public void bind(Object object) {
            super.bind(object);
            Post post = (Post) object;
            if (post.getImage() != null) {
                Glide.with(mContext).load(post.getImage().getUrl()).centerCrop().into(mImageView);
                mImageView.setVisibility(View.VISIBLE);
            }
            if (post.getVideo() != null) {
                post.getVideo().getFileInBackground(new GetFileCallback() {
                    @Override
                    public void done(File file, ParseException e) {
                        Uri uri = Uri.fromFile(file);
                        mVideoView.setVisibility(View.VISIBLE);
                        mIbtnPlayVideo.setVisibility(View.VISIBLE);
                        mVideoView.setVideoURI(uri);
                        mVideoView.seekTo(1);
                    }
                });
            }
            List<String> tags = post.getTags();
            if (tags != null && !tags.isEmpty()) {
                mLLTags.setVisibility(View.VISIBLE);
                List<TextView> tvTags = Arrays.asList(mTvTag1, mTvTag2);
                for (int i = 0; i < tags.size(); i++) {
                    TextView tvTag = tvTags.get(i);
                    tvTag.setText(tags.get(i));
                }
            }
            listeners();
        }

        private void listeners() {
            mIbtnPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVideoView.isPlaying()) {
                        mVideoView.pause();
                        mIbtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
                    } else {
                        mVideoView.start();
                        mIbtnPlayVideo.setBackgroundResource(R.drawable.ic_pause_button);
                    }
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mIbtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
                }
            });
        }
    }

    class CommentViewHolder extends ViewHolder {

        private ImageView mIvProfileImage;
        private TextView mTvUsername;
        private TextView mTvTimestamp;
        private TextView mTvBody;
        public ImageButton mIbtnLike;
        private TextView mTvLikes;
        private ImageButton mIbtnDislike;
        private TextView mTvDislikes;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvProfileImage = itemView.findViewById(R.id.ivProfileImage);
            mTvUsername = itemView.findViewById(R.id.tvUsername);
            mTvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            mTvBody = itemView.findViewById(R.id.tvBody);
            mIbtnLike = itemView.findViewById(R.id.ibtnLike);
            mTvLikes = itemView.findViewById(R.id.tvLikes);
            mIbtnDislike = itemView.findViewById(R.id.ibtnDislike);
            mTvDislikes = itemView.findViewById(R.id.tvDislikes);
        }

        @Override
        public void bind(Object object) {
            Comment comment = (Comment) object;
            ParseFile profileImage = (ParseFile) comment.getUser().get(User.KEY_PROFILE_IMAGE);
            if (profileImage != null) {
                Glide.with(mContext).load(profileImage.getUrl()).placeholder(R.drawable.default_profile_image).centerCrop().into(mIvProfileImage);
            } else {
                Glide.with(mContext).load(R.drawable.default_profile_image).centerCrop().into(mIvProfileImage);
            }
            mTvUsername.setText(comment.getUser().getUsername());
            mTvTimestamp.setText(DateFunctions.calculateTimeAgo(comment.getCreatedAt()));
            mTvBody.setText(comment.getBody());
            if (comment.getLikes() != null) {
                mTvLikes.setText(String.valueOf(comment.getLikes().size()));
            } else {
                mTvLikes.setText(String.valueOf(0));
            }
            if (comment.getDislikes() != null) {
                mTvDislikes.setText(String.valueOf(comment.getDislikes().size()));
            } else {
                mTvDislikes.setText(String.valueOf(0));
            }
            // Change appearance of like button
            if (ParseFunctions.userLikesComment(comment)) {
                mIbtnLike.setTag("Liked");
                mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
            } else {
                mIbtnLike.setTag("NotLiked");
                mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
            // Change appearance of dislike button
            if (ParseFunctions.userDislikesComment(comment)) {
                mIbtnDislike.setTag("Disliked");
                mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.purple_500)));
            } else {
                mIbtnDislike.setTag("NotDisliked");
                mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
            listeners(comment);
        }

        private void listeners(Comment comment) {
            mIvProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserDetailsActivity.class);
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(new User(comment.getUser())));
                    mContext.startActivity(intent);
                }
            });
            mIbtnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIbtnLike.getTag().equals("NotLiked")) {
                        ParseFunctions.likeComment(comment);
                        int likes = Integer.parseInt(mTvLikes.getText().toString())+1;
                        mTvLikes.setText(String.valueOf(likes));
                        mIbtnLike.setTag("Liked");
                        mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
                        if (mIbtnDislike.getTag().equals("Disliked")) {
                            ParseFunctions.removeDislikeComment(comment);
                            int dislikes = Integer.parseInt(mTvDislikes.getText().toString())-1;
                            mTvDislikes.setText(String.valueOf(dislikes));
                            mIbtnDislike.setTag("NotDisliked");
                            mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        }
                    } else {
                        ParseFunctions.removeLikeComment(comment);
                        int likes = Integer.parseInt(mTvLikes.getText().toString())-1;
                        mTvLikes.setText(String.valueOf(likes));
                        mIbtnLike.setTag("NotLiked");
                        mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                }
            });
            mIbtnDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIbtnDislike.getTag().equals("NotDisliked")) {
                        ParseFunctions.dislikeComment(comment);
                        int dislikes = Integer.parseInt(mTvDislikes.getText().toString())+1;
                        mTvDislikes.setText(String.valueOf(dislikes));
                        mIbtnDislike.setTag("Disliked");
                        mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.purple_500)));
                        if (mIbtnLike.getTag().equals("Liked")) {
                            ParseFunctions.removeLikeComment(comment);
                            int likes = Integer.parseInt(mTvLikes.getText().toString())-1;
                            mTvLikes.setText(String.valueOf(likes));
                            mIbtnLike.setTag("NotLiked");
                            mIbtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        }
                    } else {
                        ParseFunctions.removeDislikeComment(comment);
                        int dislikes = Integer.parseInt(mTvDislikes.getText().toString())-1;
                        mTvDislikes.setText(String.valueOf(dislikes));
                        mIbtnDislike.setTag("NotDisliked");
                        mIbtnDislike.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                }
            });
        }
    }
}