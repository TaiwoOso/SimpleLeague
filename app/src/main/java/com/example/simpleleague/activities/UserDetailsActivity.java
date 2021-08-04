package com.example.simpleleague.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ProfileAdapter;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsActivity extends AppCompatActivity {

    public static final String TAG = "UserDetailsActivity";

    private ParseUser mUser;
    private RecyclerView mRvUserDetails;
    private List<Post> mPosts;
    private ProfileAdapter mAdapter;
    private ImageButton mIbBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        mUser = ((User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()))).getParseUser();
        mRvUserDetails = findViewById(R.id.rvUserDetails);
        mPosts = new ArrayList<>();
        mAdapter = new ProfileAdapter(this, mUser, mPosts);
        mIbBack = findViewById(R.id.ibBack);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        mRvUserDetails.setAdapter(mAdapter);
        mRvUserDetails.setLayoutManager(layout);
        queryUserPosts(0);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryUserPosts(skips);
            }
        };
        mRvUserDetails.addOnScrollListener(scrollListener);
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Queries posts by the current user; newer posts on top
     * @param skips - tells Parse how much data to skip
     */
    private void queryUserPosts(int skips) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_USER, mUser);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.setSkip(skips);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts by "+mUser.getUsername()+".", e);
                    return;
                }
                mAdapter.addAll(posts);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), posts.size());
            }
        });
    }
}