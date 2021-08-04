package com.example.simpleleague.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.simpleleague.R;
import com.google.android.material.tabs.TabLayout;

public class SearchFragment extends Fragment {

    public SearchView mSvSearch;

    public SearchFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tlSearch = view.findViewById(R.id.tlSearch);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment searchPostsFragment = new SearchPostsFragment();
        Fragment searchUsersFragment = new SearchUsersFragment();
        tlSearch.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String text = tab.getText().toString();
                Fragment fragment;
                if (text.equals("Posts")) {
                    fragment = searchPostsFragment;
                } else {
                    fragment = searchUsersFragment;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        fragmentManager.beginTransaction().replace(R.id.flContainer, searchPostsFragment).commit();
    }
}