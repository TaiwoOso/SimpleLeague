package com.example.simpleleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.simpleleague.fragments.CreateFragment;
import com.example.simpleleague.fragments.FeedFragment;
import com.example.simpleleague.fragments.InfoFragment;
import com.example.simpleleague.fragments.ProfileFragment;
import com.example.simpleleague.fragments.SearchFragment;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String RIOT_KEY = BuildConfig.RIOT_KEY;
    private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize fields
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        // Log current user
        Log.i(TAG, "Current user is " + ParseUser.getCurrentUser().getUsername()+".");
        // Check if logged in user has Follow and proceed accordingly
        validateFollow();
        // Define fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment infoFragment = new InfoFragment();
        final Fragment feedFragment = new FeedFragment();
        final Fragment createFragment = new CreateFragment();
        final Fragment searchFragment = new SearchFragment();
        final Fragment profileFragment = new ProfileFragment();
        // Navigate through fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                int id = item.getItemId();
                if (id == R.id.action_info) {
                    fragment = infoFragment;
                    mToolbar.setVisibility(View.GONE);
                } else if (id == R.id.action_home) {
                    fragment = feedFragment;
                    mToolbar.setVisibility(View.VISIBLE);
                } else if (id == R.id.action_create) {
                    fragment = createFragment;
                    mToolbar.setVisibility(View.VISIBLE);
                }else if (id == R.id.action_search) {
                    fragment = searchFragment;
                    mToolbar.setVisibility(View.GONE);
                } else { // default to profileFragment
                    fragment = profileFragment;
                    mToolbar.setVisibility(View.VISIBLE);
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
    }

    private void validateFollow() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Follow follow = (Follow) currentUser.get(User.KEY_FOLLOW);
        if (follow != null) return;
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_USER, currentUser);
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> objects, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Follow retrieved for "+currentUser.getUsername());
                    Follow follow;
                    if (objects.isEmpty()) {
                        follow = ParseQueries.createFollow(currentUser);
                    } else {
                        follow = objects.get(0);
                    }
                    currentUser.put(User.KEY_FOLLOW, follow);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "Follow set for "+currentUser.getUsername());
                            } else {
                                Log.i(TAG, "Follow not set for "+currentUser.getUsername(), e);
                            }
                        }
                    });
                } else {
                    Log.i(TAG, "Follow not retrieved for "+currentUser.getUsername(), e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity, menu);
        return true;
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
        logoutGoogle();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logoutGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(this,gso);
        googleSignInClient.signOut();
    }
}