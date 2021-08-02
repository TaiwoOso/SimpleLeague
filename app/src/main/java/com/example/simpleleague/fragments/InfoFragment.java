package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ChampionsAdapter;
import com.example.simpleleague.models.Champion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class InfoFragment extends Fragment {

    public static final String TAG = "InfoFragment";

    private RecyclerView mRvChampions;
    private List<Champion> mChampions;
    private ChampionsAdapter mAdapter;
    private SearchView mSvSearch;

    public InfoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSvSearch = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSvSearch.setQueryHint("Search");
        mSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.clear();
                String search = mSvSearch.getQuery().toString();
                queryChampions(0, search);
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
                queryChampions(0, "");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvChampions = view.findViewById(R.id.rvChampions);
        mChampions = new ArrayList<>();
        mAdapter = new ChampionsAdapter(getContext(), mChampions);
        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        mRvChampions.setAdapter(mAdapter);
        mRvChampions.setLayoutManager(layout);
        queryChampions(0, "");
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                String search = mSvSearch.getQuery().toString();
                queryChampions(skips, search);
            }
        };
        mRvChampions.addOnScrollListener(scrollListener);
    }

    /**
     * Queries champions by alphabetical order
     * @param skips - tells Parse how much data to skip
     * @param search - tells Parse to show only data with this param
     */
    private void queryChampions(int skips, String search) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Champion> query = ParseQuery.getQuery(Champion.class);
        query.whereMatches(Champion.KEY_NAME, search, "i");
        query.addAscendingOrder(Champion.KEY_NAME);
        query.setLimit(20);
        query.setSkip(skips);
        query.findInBackground(new FindCallback<Champion>() {
            @Override
            public void done(List<Champion> champions, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving champions for "+currentUser.getUsername()+".", e);
                    return;
                }
                mAdapter.addAll(champions);
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), champions.size());
            }
        });
    }
}