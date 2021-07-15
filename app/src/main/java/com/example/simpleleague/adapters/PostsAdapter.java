package com.example.simpleleague.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.ParseFile;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> posts;

    public PostsAdapter(Context mContext, List<Post> posts) {
        this.mContext = mContext;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivProfileImage;
        private TextView tvUsername;
        private TextView tvTitle;
        private TextView tvBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            itemView.setOnClickListener(this);
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
            tvBody.setText(post.getBody());
        }

        @Override
        public void onClick(View v) {
            // Get the position
            int position = getAdapterPosition();
            // Make sure position is valid
            if (position != RecyclerView.NO_POSITION) {
                // Get the post at position
                Post post = posts.get(position);
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
