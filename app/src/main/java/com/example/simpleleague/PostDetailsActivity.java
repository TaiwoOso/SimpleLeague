package com.example.simpleleague;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.simpleleague.adapters.PostsDetailsAdapter;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailsActivity";
    private RecyclerView rvPostDetails;
    private Post post;
    private List<Comment> comments;
    private PostsDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        // Initialize fields
        rvPostDetails = findViewById(R.id.rvPostDetails);
        comments = new ArrayList<>();
        adapter = new PostsDetailsAdapter(this, comments);
        // Unwrap the post passed via intent
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", post.getUser().getUsername()));
        // Recycler View
        rvPostDetails.setAdapter(adapter);
        rvPostDetails.setLayoutManager(new LinearLayoutManager(this));
        // Get comments from Parse
        queryPostDetails();
    }

    private void queryPostDetails() {
        // Get list of comment IDs of the given post
        List<String> commentsId = post.getComments();
        // Error handling
        if (commentsId == null) {
            commentsId = new ArrayList<>();
        }
        // Query from Comment class
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Query comments from the list of IDs
        query.whereContainedIn(Comment.KEY_OBJECT_ID, commentsId);
        // Include User class
        query.include(Comment.KEY_USER);
        // Send query to Parse
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving comments", e);
                    return;
                }
                // log comments retrieved
                for (Comment comment : comments) {
                    Log.i(TAG, "Comment: " + comment.getBody() +", username: " + comment.getUser().getUsername());
                }
                // save received post and comments to list and notify adapter of new data
                adapter.addAll(post, comments);
            }
        });
    }
}