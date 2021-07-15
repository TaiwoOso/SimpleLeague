package com.example.simpleleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.simpleleague.fragments.FeedFragment;
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
        final Fragment feedFragment = new FeedFragment();
        final Fragment profileFragment = new ProfileFragment();
        // Navigate through fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                int id = item.getItemId();
                if (id == R.id.action_info) {
                    fragment = infoFragment;
                } else if (id == R.id.action_home) {
                    fragment = feedFragment;
                } else { // default to profileFragment
                    fragment = profileFragment;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        //bottomNavigationView.setSelectedItemId(R.id.action_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Log Out -> LoginActivity
        if (item.getItemId() == R.id.miLogout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        Log.i(TAG, "Logged Out. User should be null: " + currentUser);
        Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}