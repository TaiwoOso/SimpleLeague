package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

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
    private SearchView svSearch;
    private Button btnSearch;

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
        svSearch = view.findViewById(R.id.svSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        // RecyclerView
        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(layout);
        // Get the parseUsers from Parse and notify adapter
        queryParseUsers(0, "");
        // Search for specific queries
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                String search = svSearch.getQuery().toString();
                queryParseUsers(0, search);
            }
        });
        // Load more users during scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                String search = svSearch.getQuery().toString();
                loadNextUsersFromParse(skips, search);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvUsers.addOnScrollListener(scrollListener);
    }

    // Append the next "page" of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private void loadNextUsersFromParse(int skips, String search) {
        // Send an API request to retrieve appropriate "paginated" data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()
        queryParseUsers(skips, search);
    }

    private void queryParseUsers(int skips, String search) {
        // Get the current user
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        // Search for substring
        query.whereContains(User.KEY_USERNAME, search);
        // limit query to [limit] champions
        int limit = 20;
        query.setLimit(limit);
        // skip this amount
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