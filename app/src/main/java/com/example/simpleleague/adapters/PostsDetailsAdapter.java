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
import com.example.simpleleague.R;
import com.example.simpleleague.UserDetailsActivity;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.example.simpleleague.viewholders.PostViewHolder;
import com.example.simpleleague.viewholders.ViewHolder;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostsDetailsAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final String TAG = "PostsDetailsAdapter";
    private Context mContext;
    List<Post> post;
    List<Comment> comments;

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

        public PostViewHolder(@NonNull View itemView) {
            super(itemView, mContext);
        }
    }

    class CommentViewHolder extends ViewHolder {

        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvBody;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBody = itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bind(Object object) {
            Comment comment = (Comment) object;
            ParseFile profileImage = (ParseFile) comment.getUser().get(User.KEY_PROFILE_IMAGE);
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
            tvUsername.setText(comment.getUser().getUsername());
            tvBody.setText(comment.getBody());
            // Listeners
            listeners(comment);
        }

        private void listeners(Comment comment) {
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Profile clicked!");
                    // Create an intent to display UserDetailsActivity
                    Intent intent = new Intent(mContext, UserDetailsActivity.class);
                    // Serialize the movie using parceler, use its short name as a key
                    User user = new User();
                    user.setParseUser(comment.getUser());
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    // Start the activity
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
