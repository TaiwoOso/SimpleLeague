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
    private List<ParseObject> postsAndComments;
    private PostsDetailsAdapter adapter;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        // Initialize fields
        rvPostDetails = findViewById(R.id.rvPostDetails);
        postsAndComments = new ArrayList<>();
        adapter = new PostsDetailsAdapter(this, postsAndComments);

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
        List<String> commentsId = post.getComments();
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereContainedIn(Comment.KEY_OBJECT_ID, commentsId);
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