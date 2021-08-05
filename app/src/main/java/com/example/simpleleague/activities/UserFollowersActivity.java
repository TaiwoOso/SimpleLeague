package com.example.simpleleague.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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

public class UserFollowersActivity extends AppCompatActivity {

    public static final String TAG = "UserFollowersActivity";

    private ParseUser mUser;
    private RecyclerView mRvFollowers;
    private List<ParseUser> mFollowersUsers;
    private UsersAdapter mAdapter;
    private ImageButton mIbBack;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_followers);
        mUser = ((User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()))).getParseUser();
        mRvFollowers = findViewById(R.id.rvFollowers);
        mFollowersUsers = new ArrayList<>();
        mAdapter = new UsersAdapter(this, mFollowersUsers);
        mIbBack = findViewById(R.id.ibBack);
        mProgressBar = findViewById(R.id.progressBar);
        GridLayoutManager layout = new GridLayoutManager(this, 2);
        mRvFollowers.setAdapter(mAdapter);
        mRvFollowers.setLayoutManager(layout);
        queryFollowers(0);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryFollowers(skips);
            }
        };
        mRvFollowers.addOnScrollListener(scrollListener);
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Queries list of users that follow current user
     * @param skips - tells Parse how much data to skip
     */
    private void queryFollowers(int skips) {
        mProgressBar.setVisibility(View.VISIBLE);
        Follow follow = (Follow) mUser.get(User.KEY_FOLLOW);
        if (follow == null) return;
        List<String> followersIds = follow.getFollowers();
        if (followersIds == null) return;
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContainedIn(ParseUser.KEY_OBJECT_ID, followersIds);
        query.setLimit(20);
        query.setSkip(skips);
        query.include(User.KEY_FOLLOW);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving followers for "+mUser.getUsername()+".", e);
                    return;
                }
                mAdapter.addAll(users);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), users.size());
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}