package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.simpleleague.adapters.UsersAdapter;
import com.example.simpleleague.models.Champion;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersFragment extends SearchFragment {

    public static final String TAG = "SearchUsersFragment";

    private RecyclerView mRvUsers;
    private List<ParseUser> mUsers;
    private UsersAdapter mAdapter;

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.clear();
                String search = mSvSearch.getQuery().toString();
                queryUsers(0, search);
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
                queryUsers(0, "");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRvUsers = view.findViewById(R.id.rvUsers);
        mUsers = new ArrayList<>();
        mAdapter = new UsersAdapter(getContext(), mUsers);
        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        mRvUsers.setAdapter(mAdapter);
        mRvUsers.setLayoutManager(layout);
        queryUsers(0, "");
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                String search = mSvSearch.getQuery().toString();
                queryUsers(skips, search);
            }
        };
        mRvUsers.addOnScrollListener(scrollListener);
    }

    /**
     * Queries users with the most followers
     * @param skips - tells Parse how much data to skip
     * @param search - tells Parse to show only data with this param
     */
    private void queryUsers(int skips, String search) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereMatches(User.KEY_USERNAME, search, "i");
        query.setLimit(20);
        query.setSkip(skips);
        query.include(User.KEY_FOLLOW);
        query.addDescendingOrder(User.KEY_FOLLOWERS_COUNT);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving parse users for "+currentUser.getUsername()+".", e);
                    return;
                }
                mAdapter.addAll(users);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), users.size());
            }
        });
    }
}