package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleleague.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchFragment extends Fragment {

    BottomNavigationView topNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize Fields
        topNavigationView = view.findViewById(R.id.topNavigation);
        // Define fragments
        final FragmentManager fragmentManager = getChildFragmentManager();
        final Fragment searchPostsFragment = new SearchPostsFragment();
        final Fragment searchUsersFragment = new SearchUsersFragment();
        topNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment fragment;
                if (id == R.id.action_users) {
                    fragment = searchUsersFragment;
                } else { // default to searchPostsFragment
                    fragment = searchPostsFragment;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        topNavigationView.setSelectedItemId(R.id.action_posts);
    }
}