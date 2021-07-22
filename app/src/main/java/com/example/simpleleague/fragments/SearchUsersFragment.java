package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ChampionsAdapter;
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
    private RecyclerView rvUsers;
    private List<ParseUser> parseUsers;
    private UsersAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Search for specific queries
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.clear();
                String search = svSearch.getQuery().toString();
                queryParseUsers(0, search);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize fields
        rvUsers = view.findViewById(R.id.rvUsers);
        parseUsers = new ArrayList<>();
        adapter = new UsersAdapter(getContext(), parseUsers);
        // RecyclerView
        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(layout);
        // Get the parseUsers from Parse and notify adapter
        queryParseUsers(0, "");
        // Load more users during scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                String search = svSearch.getQuery().toString();
                queryParseUsers(skips, search);
            }
        };
        rvUsers.addOnScrollListener(scrollListener);
    }

    private void queryParseUsers(int skips, String search) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContains(User.KEY_USERNAME, search);
        int limit = 20;
        query.setLimit(limit);
        query.setSkip(skips);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving parse users.", e);
                    return;
                }
                Log.i(TAG, "Retrieved "+parseUsers.size()+" parse user(s) for "+currentUser.getUsername()+".");
                // save received posts to list and notify adapter of new data
                adapter.addAll(parseUsers);
                adapter.notifyItemRangeInserted(skips, skips+limit);
            }
        });
    }
}