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
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.PostDetailsActivity;
import com.example.simpleleague.R;
import com.example.simpleleague.UserDetailsActivity;
import com.example.simpleleague.models.Champion;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG ="UsersAdapter";
    private Context mContext;
    private List<ParseUser> parseUsers;

    public UsersAdapter(Context mContext, List<ParseUser> parseUsers) {
        this.mContext = mContext;
        this.parseUsers = parseUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        ParseUser parseUser = parseUsers.get(position);
        holder.bind(parseUser);
    }

    @Override
    public int getItemCount() {
        return parseUsers.size();
    }

    public void addAll(List<ParseUser> list) {
        parseUsers.addAll(list);
    }

    public void clear() {
        parseUsers.clear();
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View rootView;
        private ImageView ivProfileImage;
        private TextView tvUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUserName = itemView.findViewById(R.id.tvUsername);
            rootView.setOnClickListener(this);
        }

        public void bind(ParseUser parseUser) {
            rootView.setTag(parseUser.getUsername());
            ParseQueries.loadProfileImage(ivProfileImage, mContext, parseUser);
            tvUserName.setText(parseUser.getUsername());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ParseUser parseUser = parseUsers.get(position);
                Intent intent = new Intent(mContext, UserDetailsActivity.class);
                User user = new User();
                user.setParseUser(parseUser);
                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                Log.i(TAG, "Clicked on " + parseUser.getUsername() + "'s profile.");
                mContext.startActivity(intent);
            }
        }
    }
}
