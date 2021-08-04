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
import com.example.simpleleague.activities.ChampionDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Champion;

import org.parceler.Parcels;

import java.util.List;

public class ChampionsAdapter extends RecyclerView.Adapter<ChampionsAdapter.ViewHolder> {

    private Context mContext;
    private List<Champion> mChampions;

    public ChampionsAdapter(Context mContext, List<Champion> champions) {
        this.mContext = mContext;
        this.mChampions = champions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_champion, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChampionsAdapter.ViewHolder holder, int position) {
        Champion champion = mChampions.get(position);
        holder.bind(champion);
    }

    @Override
    public int getItemCount() {
        return mChampions.size();
    }

    public void addAll(List<Champion> list) {
        mChampions.addAll(list);
    }

    public void clear() {
        mChampions.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvProfile;
        private TextView mTvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvProfile = itemView.findViewById(R.id.ivProfile);
            mTvName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(this);
        }

        public void bind(Champion champion) {
            mTvName.setText(champion.getName());
            Glide.with(mContext).load(champion.getImage().getUrl()).centerCrop().into(mIvProfile);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Champion champion = mChampions.get(position);
                Intent intent = new Intent(mContext, ChampionDetailsActivity.class);
                intent.putExtra(Champion.class.getSimpleName(), Parcels.wrap(champion));
                mContext.startActivity(intent);
            }
        }
    }
}