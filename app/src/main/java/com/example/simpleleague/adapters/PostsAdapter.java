package com.example.simpleleague.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.simpleleague.ParseFunctions;
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
    private List<Post> mPosts;

    public PostsAdapter(Context mContext, List<Post> posts) {
        this.mContext = mContext;
        this.mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void addAll(List<Post> list) {
        mPosts.addAll(list);
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends PostViewHolder implements DoubleClickListener {

        private ImageView mIvLike;
        private AnimatedVectorDrawableCompat mAvdc;
        private AnimatedVectorDrawable mAvd;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView, context);
            mIvLike = itemView.findViewById(R.id.ivLike);
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
                mTvBody.setText(blurb);
            }
        }

        @Override
        public void onSingleClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = mPosts.get(position);
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
                    Post post = mPosts.get(position);
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
