package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.ParseFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.PostsAdapter;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";
    public RecyclerView mRvPosts;
    public PostsAdapter mAdapter;
    public List<Post> mPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvPosts = view.findViewById(R.id.rvPosts);
        mPosts = new ArrayList<>();
        mAdapter = new PostsAdapter(getContext(), mPosts);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRvPosts.setAdapter(mAdapter);
        mRvPosts.setLayoutManager(layout);
        queryPosts(0);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                queryPosts(skips);
            }
        };
        mRvPosts.addOnScrollListener(scrollListener);
        SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clear();
                queryPosts(0);
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * Queries posts for the user's Feed page
     * @param skips - tells Parse how much data to skip
     */
    public void queryPosts(int skips) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Follow follow = (Follow) currentUser.get(User.KEY_FOLLOW);
        if (follow == null) {
            follow = ParseFunctions.createFollow(currentUser);
        }
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        List<String> followingIds = follow.getFollowing();
        if (followingIds == null || followingIds.isEmpty()) {
            queryPostsWithoutFollowing(skips/4);
            return;
        }
        queryPostsWithFollowing(skips, followingIds);
    }

    /**
     * Queries posts by users who current user follows
     * and sorts by custom algorithm - Comparator
     * @param skips - tells Parse how much data to skip
     * @param followingIds - list of user ids who current user follows
     */
    private void queryPostsWithFollowing(int skips, List<String> followingIds) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereContainedIn(Post.KEY_USER, followingIds);
        query.setLimit(20);
        query.setSkip(skips);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts for "+currentUser.getUsername()+".", e);
                    return;
                }
                Collections.sort(posts, new Comparator<Post>() {
                    /* Sorts post where posts that are created earlier,
                     * have more views, more likes, less dislikes, and more comments
                     * are placed closer to the top of the list
                     */
                    @Override
                    public int compare(Post o1, Post o2) {
                        double score = -(o1.getCreatedAt().compareTo(o2.getCreatedAt())*2);
                        score += o1.compareViews(o2)*1.5;
                        score += o1.compareLikes(o2)*1.5;
                        score -= o1.compareDislikes(o2)*1.2;
                        score += o1.compareComments(o2)*1.2;
                        score *= 100;
                        return (int)score;
                    }
                });
                mAdapter.addAll(posts);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), posts.size());
            }
        });
    }

    /**
     * Queries posts with most views from users with most followers
     * for users who don't follow any other users
     * @param skips - tells Parse how much data to skip
     */
    private void queryPostsWithoutFollowing(int skips) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> usersQuery = ParseQuery.getQuery(ParseUser.class);
        usersQuery.setLimit(5);
        usersQuery.setSkip(skips);
        usersQuery.include(User.KEY_FOLLOW);
        usersQuery.addDescendingOrder(User.KEY_FOLLOWERS_COUNT);
        usersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving parse users for "+currentUser.getUsername()+".", e);
                    return;
                }
                ParseQuery<Post> postsQuery = ParseQuery.getQuery(Post.class);
                postsQuery.whereContainedIn(Post.KEY_USER, users);
                postsQuery.setLimit(20);
                postsQuery.include(Post.KEY_USER);
                postsQuery.addDescendingOrder(Post.KEY_VIEWS);
                postsQuery.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with retrieving posts for "+currentUser.getUsername()+".", e);
                            return;
                        }
                        mAdapter.addAll(posts);
                        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), posts.size());
                    }
                });
            }
        });
    }
}