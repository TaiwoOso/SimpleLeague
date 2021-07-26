package com.example.simpleleague;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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
    private ParseUser user;
    private RecyclerView rvFollowing;
    private List<ParseUser> following;
    private UsersAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_following);
        // Initialize fields
        rvFollowing = findViewById(R.id.rvFollowing);
        following = new ArrayList<>();
        adapter = new UsersAdapter(this, following);
        // Unwrap the user passed via intent
        user = ((User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()))).getParseUser();
        Log.i(TAG, String.format("Showing following for %s", user.getUsername())+".");
        // Recycler View
        GridLayoutManager layout = new GridLayoutManager(this, 2);
        rvFollowing.setAdapter(adapter);
        rvFollowing.setLayoutManager(layout);
        // Query user's followers from Parse
        queryFollowing(0);
        // Load more users during scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryFollowing(skips);
            }
        };
        rvFollowing.addOnScrollListener(scrollListener);
    }

    private void queryFollowing(int skips) {
        Follow follow = (Follow) user.get(User.KEY_FOLLOW);
        if (follow == null) return;
        List<String> following = follow.getFollowing();
        if (following == null) return;
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContainedIn(ParseUser.KEY_OBJECT_ID, following);
        int limit = 20;
        query.setLimit(limit);
        query.setSkip(skips);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving following users for "+user.getUsername()+".", e);
                    return;
                }
                Log.i(TAG, "Retrieved "+parseUsers.size()+" following user(s) for "+user.getUsername()+".");
                // save received posts to list and notify adapter of new data
                adapter.addAll(parseUsers);
                adapter.notifyItemRangeInserted(skips, skips+limit);
            }
        });
    }
}