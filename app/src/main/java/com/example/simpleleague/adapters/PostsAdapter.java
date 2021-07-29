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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.activities.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.viewholders.PostViewHolder;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import org.parceler.Parcels;

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
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends PostViewHolder implements DoubleClickListener {

        private ImageView ivLike;
        private AnimatedVectorDrawableCompat avdc;
        private AnimatedVectorDrawable avd;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            ivLike = itemView.findViewById(R.id.ivLike);
            itemView.setOnClickListener(new DoubleClick(this));
        }

        @Override
        public void bind(Object object) {
            super.bind(object);
            Post post = (Post) object;
            String blurb;
            int blurbCount = 125;
            String body = post.getBody();
            if (body == null) return;
            if (body.length() > blurbCount) {
                blurb = body.substring(0, blurbCount).trim()+"...";
                tvBody.setText(blurb);
            }
        }

        @Override
        public void onSingleClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
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
                    Post post = posts.get(position);
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
