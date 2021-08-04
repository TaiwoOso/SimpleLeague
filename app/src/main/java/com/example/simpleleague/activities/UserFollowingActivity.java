package com.example.simpleleague.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.UsersAdapter;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserFollowingActivity extends AppCompatActivity {

    public static final String TAG = "UserFollowingActivity";

    private ParseUser mUser;
    private RecyclerView mRvFollowing;
    private List<ParseUser> mFollowingUsers;
    private UsersAdapter mAdapter;
    private ImageButton mIbBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_following);
        mUser = ((User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()))).getParseUser();
        mRvFollowing = findViewById(R.id.rvFollowing);
        mFollowingUsers = new ArrayList<>();
        mAdapter = new UsersAdapter(this, mFollowingUsers);
        mIbBack = findViewById(R.id.ibBack);
        GridLayoutManager layout = new GridLayoutManager(this, 2);
        mRvFollowing.setAdapter(mAdapter);
        mRvFollowing.setLayoutManager(layout);
        queryFollowing(0);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryFollowing(skips);
            }
        };
        mRvFollowing.addOnScrollListener(scrollListener);
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Queries list of users that current user follows
     * @param skips - tells Parse how much data to skip
     */
    private void queryFollowing(int skips) {
        Follow follow = (Follow) mUser.get(User.KEY_FOLLOW);
        if (follow == null) return;
        List<String> followingIds = follow.getFollowing();
        if (followingIds == null) return;
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContainedIn(ParseUser.KEY_OBJECT_ID, followingIds);
        query.setLimit(20);
        query.setSkip(skips);
        query.include(User.KEY_FOLLOW);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving following users for "+mUser.getUsername()+".", e);
                    return;
                }
                mAdapter.addAll(users);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), users.size());
            }
        });
    }
}