package com.example.simpleleague.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

public class PostsDetailsAdapter extends RecyclerView.Adapter<PostsDetailsAdapter.ViewHolder> {

    private Context mContext;
    private List<ParseObject> postsAndComments;

    public PostsDetailsAdapter(Context mContext, List<ParseObject> postsAndComments) {
        this.mContext = mContext;
        this.postsAndComments = postsAndComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsDetailsAdapter.ViewHolder holder, int position) {
        ParseObject object = postsAndComments.get(position);
        if (object instanceof Post) {
            holder.bind((Post) object);
        } else {
            holder.bind((Comment) object);
        }
    }

    @Override
    public int getItemCount() {
        return postsAndComments.size();
    }

    public void addAll(Post post, List<Comment> comments) {
        postsAndComments.add(post);
        postsAndComments.addAll(comments);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvTitle;
        private TextView tvBody;
        private TextView tvCommentsLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvCommentsLabel = itemView.findViewById(R.id.tvCommentsLabel);
        }

        public void bind(Post post) {
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
            tvTitle.setVisibility(View.VISIBLE);
            tvBody.setText(post.getBody());
            tvCommentsLabel.setVisibility(View.VISIBLE);
        }

        public void bind(Comment comment) {
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
            tvTitle.setVisibility(View.GONE);
            tvBody.setText(comment.getBody());
            tvCommentsLabel.setVisibility(View.GONE);
        }

    }
}
