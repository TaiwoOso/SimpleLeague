package com.example.simpleleague.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.ParseFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.PostsDetailsAdapter;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailsActivity";

    private Post mPost;
    private RecyclerView mRvPostDetails;
    private List<Comment> mComments;
    private PostsDetailsAdapter mAdapter;
    private EditText mEtComment;
    private ImageButton mIbBack;
    private ImageView mIvProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        mPost = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        mRvPostDetails = findViewById(R.id.rvPostDetails);
        mComments = new ArrayList<>();
        mAdapter = new PostsDetailsAdapter(this, mPost, mComments);
        mEtComment = findViewById(R.id.etComment);
        mIbBack = findViewById(R.id.ibBack);
        mIvProfileImage = findViewById(R.id.ivProfileImage);
        ParseFunctions.loadProfileImage(mIvProfileImage, this, ParseUser.getCurrentUser());
        LinearLayoutManager layout = new LinearLayoutManager(this);
        mRvPostDetails.setAdapter(mAdapter);
        mRvPostDetails.setLayoutManager(layout);
        queryPostDetails(0);
        addTagsToUser();
        addUserToView();
        ParseFunctions.savePostViewsCount(mPost);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryPostDetails(skips);
            }
        };
        mRvPostDetails.addOnScrollListener(scrollListener);
        mEtComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    comment();
                    return true;
                }
                return false;
            }
        });
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Queries comments with most likes
     * then sorts by custom algorithm in Comment's compareTo
     * @param skips - tells Parse how much data to skip
     */
    private void queryPostDetails(int skips) {
        List<String> commentsIds = mPost.getComments();
        if (commentsIds == null) {
            commentsIds = new ArrayList<>();
        }
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereContainedIn(Comment.KEY_OBJECT_ID, commentsIds);
        query.addDescendingOrder(Post.KEY_LIKES);
        query.include(Comment.KEY_USER);
        query.setLimit(20);
        query.setSkip(skips);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving comments for "+mPost.getUser().getUsername()+"'s post.", e);
                    return;
                }
                Collections.sort(comments);
                mAdapter.addAll(comments);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), comments.size());
            }
        });
    }

    /**
     * Adds post's tags to current user's tags
     * if tags exceed maxTagSize; rotate out oldest tags
     */
    private void addTagsToUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> postTags = mPost.getTags();
        if (postTags == null) return;
        int maxTagsSize = 10;
        List<String> userTags = (List<String>) currentUser.get(User.KEY_TAGS);
        if (userTags == null) {
            currentUser.addAllUnique(User.KEY_TAGS, postTags);
        } else if (userTags.size() + postTags.size() <= maxTagsSize) {
            currentUser.addAllUnique(User.KEY_TAGS, postTags);
        } else {
            ArrayList<String> newUserTags = new ArrayList<>(maxTagsSize);
            for (int i = postTags.size(); i < userTags.size(); i++) {
                newUserTags.add(userTags.get(i));
            }
            newUserTags.addAll(postTags);
            currentUser.put(User.KEY_TAGS, newUserTags);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Tags weren't added for "+currentUser.getUsername()+".");
                }
            }
        });
    }

    /**
     * Adds current user to post's list of users who have viewed it
     */
    private void addUserToView() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        mPost.addView(currentUser.getObjectId());
        mPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't add "+currentUser.getUsername()+" to "+mPost.getUser().getUsername()+"'s post's list of views.");
                }
            }
        });
    }

    /**
     * Adds new comment to post's list of comments
     */
    private void comment() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String body = mEtComment.getText().toString();
        if (body.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Comment comment = new Comment();
        comment.setUser(currentUser);
        comment.setBody(body.trim());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(PostDetailsActivity.this, "Error: Try Again!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error while commenting for "+currentUser.getUsername()+".", e);
                }
                mPost.addComment(comment.getObjectId());
                mPost.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(PostDetailsActivity.this, "Error: Try Again!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error while commenting for "+currentUser.getUsername()+".", e);
                        }
                        Toast.makeText(PostDetailsActivity.this, "Commented!", Toast.LENGTH_SHORT).show();
                        mEtComment.setText("");
                        mComments.add(comment);
                        mAdapter.notifyItemInserted(mAdapter.getItemCount());
                        mRvPostDetails.getLayoutManager().scrollToPosition(mAdapter.getItemCount()-1);
                    }
                });
            }
        });
    }
}