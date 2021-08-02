package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.PostsAdapter;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchPostsFragment extends SearchFragment {

    public static final String TAG = "SearchPostsFragment";

    private RecyclerView mRvPosts;
    private PostsAdapter mAdapter;
    private List<Post> mPosts;

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.clear();
                String search = mSvSearch.getQuery().toString();
                queryPosts(0, search);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        ImageView closeButton = (ImageView)mSvSearch.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) mSvSearch.findViewById(R.id.search_src_text);
                et.setText("");
                mSvSearch.setQuery("", false);
                mSvSearch.onActionViewCollapsed();
                menu.findItem(R.id.action_search).collapseActionView();
                mAdapter.clear();
                queryPosts(0, "");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRvPosts = view.findViewById(R.id.rvPosts);
        mPosts = new ArrayList<Post>();
        mAdapter = new PostsAdapter(getContext(), mPosts);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRvPosts.setAdapter(mAdapter);
        mRvPosts.setLayoutManager(layout);
        queryPosts(0, "");
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                String search = mSvSearch.getQuery().toString();
                queryPosts(skips, search);
            }
        };
        mRvPosts.addOnScrollListener(scrollListener);
    }

    /**
     * Queries posts with most views
     * @param skips - tells Parse how much data to skip
     * @param search - tells Parse to show only data with this param
     */
    private void queryPosts(int skips, String search) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Post> titleQuery = ParseQuery.getQuery(Post.class);
        titleQuery.whereMatches(Post.KEY_TITLE, search, "i");
        ParseQuery<Post> bodyQuery = ParseQuery.getQuery(Post.class);
        bodyQuery.whereMatches(Post.KEY_BODY, search, "i");
        List<ParseQuery<Post>> queries = new ArrayList<>();
        queries.add(titleQuery);
        queries.add(bodyQuery);
        ParseQuery<Post> mainQuery = ParseQuery.or(queries);
        mainQuery.setLimit(20);
        mainQuery.setSkip(skips);
        mainQuery.include(Post.KEY_USER);
        mainQuery.addDescendingOrder(Post.KEY_VIEWS);
        mainQuery.findInBackground(new FindCallback<Post>() {
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
}