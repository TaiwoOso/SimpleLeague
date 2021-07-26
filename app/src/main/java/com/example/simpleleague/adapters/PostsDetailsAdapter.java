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
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.R;
import com.example.simpleleague.UserDetailsActivity;
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
    private List<Post> post;
    private List<Comment> comments;

    public PostsDetailsAdapter(Context mContext, List<Comment> comments) {
        this.mContext = mContext;
        this.post = new ArrayList<>();
        this.comments = comments;
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
            holder.bind(post.get(position));
        } else {
            Comment comment = comments.get(position-1);
            holder.bind(comment);
        }
    }

    @Override
    public int getItemCount() {
        return post.size()+comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addAll(Post p, List<Comment> list) {
        post.add(p);
        comments.addAll(list);
        notifyDataSetChanged();
    }


    class PostViewHolder extends com.example.simpleleague.viewholders.PostViewHolder {

        private TextView tvTag1;
        private TextView tvTag2;
        private VideoView videoView;
        private ImageButton ibtnPlayVideo;
        private LinearLayout llTags;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView, mContext);
            tvTag1 = itemView.findViewById(R.id.tvTag1);
            tvTag2 = itemView.findViewById(R.id.tvTag2);
            videoView = itemView.findViewById(R.id.videoView);
            ibtnPlayVideo = itemView.findViewById(R.id.ibtnPlayVideo);
            llTags = itemView.findViewById(R.id.llTags);
        }

        @Override
        public void bind(Object object) {
            super.bind(object);
            Post post = (Post) object;
            if (post.getVideo() != null) {
                post.getVideo().getFileInBackground(new GetFileCallback() {
                    @Override
                    public void done(File file, ParseException e) {
                        Uri uri = Uri.fromFile(file);
                        videoView.setVisibility(View.VISIBLE);
                        ibtnPlayVideo.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(uri);
                        videoView.seekTo(1);
                    }
                });
            }
            List<String> tags = post.getTags();
            if (tags != null && !tags.isEmpty()) {
                llTags.setVisibility(View.VISIBLE);
                List<TextView> tvTags = Arrays.asList(tvTag1, tvTag2);
                for (int i = 0; i < tags.size(); i++) {
                    TextView tvTag = tvTags.get(i);
                    tvTag.setText(tags.get(i));
                }
            }
            // Listeners
            listeners();
        }

        private void listeners() {
            ibtnPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        ibtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
                    } else {
                        videoView.start();
                        ibtnPlayVideo.setBackgroundResource(R.drawable.ic_pause_button);
                    }
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ibtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
                }
            });
        }
    }

    class CommentViewHolder extends ViewHolder {

        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvBody;
        public ImageButton ibtnLike;
        private TextView tvLikes;
        private ImageButton ibtnDislike;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBody = itemView.findViewById(R.id.tvBody);
            ibtnLike = itemView.findViewById(R.id.ibtnLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ibtnDislike = itemView.findViewById(R.id.ibtnDislike);
        }

        @Override
        public void bind(Object object) {
            Comment comment = (Comment) object;
            ParseFile profileImage = (ParseFile) comment.getUser().get(User.KEY_PROFILE_IMAGE);
            if (profileImage != null) {
                Glide.with(mContext).load(profileImage.getUrl()).placeholder(R.drawable.default_profile_image).centerCrop().into(ivProfileImage);
            } else {
                Glide.with(mContext).load(R.drawable.default_profile_image).centerCrop().into(ivProfileImage);
            }
            tvUsername.setText(comment.getUser().getUsername());
            tvBody.setText(comment.getBody());
            if (comment.getLikes() != null && !comment.getLikes().isEmpty()) {
                tvLikes.setText(String.valueOf(comment.getLikes().size()));
            } else {
                tvLikes.setText(R.string.Like);
            }
            // Change appearance of like button
            if (ParseQueries.userLikesComment(comment)) {
                ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#039BE5")));
            } else {
                ibtnLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            }
            // Listeners
            listeners(comment);
        }

        private void listeners(Comment comment) {
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Clicked on "+comment.getUser().getUsername()+"'s profile.");
                    Intent intent = new Intent(mContext, UserDetailsActivity.class);
                    User user = new User();
                    user.setParseUser(comment.getUser());
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    mContext.startActivity(intent);
                }
            });
            ibtnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ibtnLike.getBackgroundTintList().equals(ColorStateList.valueOf(Color.BLACK))) {
                        ParseQueries.likeComment(comment);
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
                        ParseQueries.unlikeComment(comment);
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
}
