package com.example.simpleleague;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.simpleleague.adapters.PostsDetailsAdapter;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
        Log.d(TAG, String.format("Showing post details for %s", post.getUser().getUsername())+".");
        // Recycler View
        rvPostDetails.setAdapter(adapter);
        rvPostDetails.setLayoutManager(new LinearLayoutManager(this));
        // Get comments from Parse
        queryPostDetails();
        // Add post's tags to current user
        addTagsToUser();
    }

    private void addTagsToUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> userTags = (List<String>) currentUser.get(User.KEY_TAGS);
        List<String> postTags = post.getTags();
        if (postTags == null || postTags.isEmpty()) return;
        if (userTags == null) {
            currentUser.addAllUnique(User.KEY_TAGS, postTags);
        } else if (userTags.containsAll(postTags)) {
            return;
        } else if (userTags.size() + postTags.size() <= 5) {
            currentUser.addAllUnique(User.KEY_TAGS, postTags);
        } else {
            ArrayList<String> newUserTags = new ArrayList<>(5);
            int start = userTags.size() - postTags.size() - 1;
            for (int i = start; i < userTags.size(); i++) {
                newUserTags.add(userTags.get(i));
            }
            newUserTags.addAll(postTags);
            currentUser.put(User.KEY_TAGS, newUserTags);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Tags weren't added for "+currentUser.getUsername()+".");
                    return;
                }
                Log.i(TAG, "Tags were added for "+currentUser.getUsername()+".");
            }
        });
    }

    private void queryPostDetails() {
        List<String> commentsId = post.getComments();
        // Error handling
        if (commentsId == null) {
            commentsId = new ArrayList<>();
        }
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereContainedIn(Comment.KEY_OBJECT_ID, commentsId);
        query.include(Comment.KEY_USER);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving comments for "+post.getUser().getUsername()+"'s post.", e);
                    return;
                }
                Log.i(TAG, "Retrieved "+comments.size()+" comment(s) for "+post.getUser().getUsername()+"'s post.");
                // save received post and comments to list and notify adapter of new data
                adapter.addAll(post, comments);
            }
        });
    }
}