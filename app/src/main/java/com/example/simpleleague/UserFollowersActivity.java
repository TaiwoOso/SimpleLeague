package com.example.simpleleague;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.simpleleague.adapters.ProfileAdapter;
import com.example.simpleleague.adapters.UsersAdapter;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.Post;
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
    private ParseUser user;
    private RecyclerView rvFollowers;
    private List<ParseUser> followers;
    private UsersAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_followers);
        // Initialize fields
        rvFollowers = findViewById(R.id.rvFollowers);
        followers = new ArrayList<>();
        adapter = new UsersAdapter(this, followers);
        // Unwrap the user passed via intent
        user = ((User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()))).getParseUser();
        Log.i(TAG, String.format("Showing followers for %s", user.getUsername())+".");
        // Recycler View
        GridLayoutManager layout = new GridLayoutManager(this, 2);
        rvFollowers.setAdapter(adapter);
        rvFollowers.setLayoutManager(layout);
        // Query user's followers from Parse
        queryFollowers(0);
        // Load more users during scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryFollowers(skips);
            }
        };
        rvFollowers.addOnScrollListener(scrollListener);
    }

    private void queryFollowers(int skips) {
        Follow follow = (Follow) user.get(User.KEY_FOLLOW);
        if (follow == null) return;
        List<String> followers = follow.getFollowers();
        if (followers == null) return;
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContainedIn(ParseUser.KEY_OBJECT_ID, followers);
        int limit = 20;
        query.setLimit(limit);
        query.setSkip(skips);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving followers for "+user.getUsername()+".", e);
                    return;
                }
                Log.i(TAG, "Retrieved "+parseUsers.size()+" follower(s) for "+user.getUsername()+".");
                // save received posts to list and notify adapter of new data
                adapter.addAll(parseUsers);
                adapter.notifyItemRangeInserted(skips, skips+limit);
            }
        });
    }
}