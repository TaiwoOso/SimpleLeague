package com.example.simpleleague;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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
    private ParseUser user;
    private RecyclerView rvUserDetails;
    private List<Post> posts;
    private ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        // Initialize fields
        rvUserDetails = findViewById(R.id.rvUserDetails);
        posts = new ArrayList<>();
        adapter = new ProfileAdapter(this, posts);
        // Unwrap the user passed via intent
        user = ((User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()))).getParseUser();
        Log.d(TAG, String.format("Showing user details for '%s'", user.getUsername()));
        // Recycler View
        rvUserDetails.setAdapter(adapter);
        rvUserDetails.setLayoutManager(new LinearLayoutManager(this));
        // Query user's posts from Parse
        queryPosts();
    }

    private void queryPosts() {
        // Query from Post class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Query posts with current user
        query.whereEqualTo(Post.KEY_USER, user);
        // Include User class
        query.include(Post.KEY_USER);
        // Send query to Parse
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                    return;
                }
                // log posts retrieved
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getBody() +", username: " + post.getUser().getUsername());
                }
                // save received posts to list and notify adapter of new data
                adapter.addAll(user, posts);
            }
        });
    }
}