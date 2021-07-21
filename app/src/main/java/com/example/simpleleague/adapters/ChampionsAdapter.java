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
import com.example.simpleleague.ChampionDetailsActivity;
import com.example.simpleleague.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Champion;
import com.example.simpleleague.models.Post;

import org.parceler.Parcels;

import java.util.List;

public class ChampionsAdapter extends RecyclerView.Adapter<ChampionsAdapter.ViewHolder> {

    private Context mContext;
    private List<Champion> champions;

    public ChampionsAdapter(Context mContext, List<Champion> champions) {
        this.mContext = mContext;
        this.champions = champions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_champion, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChampionsAdapter.ViewHolder holder, int position) {
        Champion champion = champions.get(position);
        holder.bind(champion);
    }

    @Override
    public int getItemCount() {
        return champions.size();
    }

    public void addAll(List<Champion> list) {
        champions.addAll(list);
    }

    public void clear() {
        champions.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View rootView;
        private ImageView ivProfile;
        private TextView tvName;
        private View vPalette;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);
            itemView.setOnClickListener(this);
        }

        public void bind(Champion champion) {
            rootView.setTag(champion.getName());
            tvName.setText(champion.getName());
            Glide.with(mContext)
                    .load(champion.getImage().getUrl())
                    .centerCrop()
                    .into(ivProfile);
        }

        @Override
        public void onClick(View v) {
            // Get the position
            int position = getAdapterPosition();
            // Make sure position is valid
            if (position != RecyclerView.NO_POSITION) {
                // Get the post at position
                Champion champion = champions.get(position);
                // Create an intent to display PostDetailsActivity
                Intent intent = new Intent(mContext, ChampionDetailsActivity.class);
                // Serialize the movie using parceler, use its short name as a key
                intent.putExtra(Champion.class.getSimpleName(), Parcels.wrap(champion));
                // Show the activity
                mContext.startActivity(intent);
            }
        }
    }
}
