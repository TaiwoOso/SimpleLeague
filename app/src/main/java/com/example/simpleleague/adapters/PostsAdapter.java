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
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.example.simpleleague.viewholders.PostViewHolder;
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
        return new ViewHolder(itemView, mContext);
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

    class ViewHolder extends PostViewHolder implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bind(Object object) {
            super.bind(object);
            Post post = (Post) object;
            String blurb;
            int blurbCount = 125;
            String body = post.getBody();
            if (body.length() > blurbCount) {
                blurb = body.substring(0, blurbCount)+"...";
                tvBody.setText(blurb);
            }
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
