package com.example.simpleleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.simpleleague.fragments.InfoFragment;
import com.example.simpleleague.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String RIOT_KEY = BuildConfig.RIOT_KEY;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize fields
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        // Log current user
        Log.i(TAG, "Current user: " + ParseUser.getCurrentUser().getUsername());
        // Define fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment infoFragment = new InfoFragment();
        final Fragment profileFragment = new ProfileFragment();
        // Navigate through fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                int id = item.getItemId();
                if (id == R.id.action_info) {
                    fragment = infoFragment;
                } else { // default to profileFragment
                    fragment = profileFragment;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        //bottomNavigationView.setSelectedItemId(R.id.action_info);
    }
}