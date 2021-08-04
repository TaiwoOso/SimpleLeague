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

import com.example.simpleleague.ParseFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.activities.UserDetailsActivity;
import com.example.simpleleague.models.User;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG ="UsersAdapter";

    private Context mContext;
    private List<ParseUser> mUsers;

    public UsersAdapter(Context mContext, List<ParseUser> users) {
        this.mContext = mContext;
        this.mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        holder.bind(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void addAll(List<ParseUser> list) {
        mUsers.addAll(list);
    }

    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvProfileImage;
        private TextView mTvUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvProfileImage = itemView.findViewById(R.id.ivProfileImage);
            mTvUserName = itemView.findViewById(R.id.tvUsername);
            itemView.setOnClickListener(this);
        }

        public void bind(ParseUser parseUser) {
            ParseFunctions.loadProfileImage(mIvProfileImage, mContext, parseUser);
            mTvUserName.setText(parseUser.getUsername());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ParseUser user = mUsers.get(position);
                Intent intent = new Intent(mContext, UserDetailsActivity.class);
                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(new User(user)));
                mContext.startActivity(intent);
            }
        }
    }
}