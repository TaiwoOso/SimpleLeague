package com.example.simpleleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.adapters.ProfileAdapter;
import com.example.simpleleague.fragments.CreateFragment;
import com.example.simpleleague.fragments.CreateImageFragment;
import com.example.simpleleague.fragments.FeedFragment;
import com.example.simpleleague.fragments.HomeFragment;
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

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
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
        final Fragment homeFragment = new HomeFragment();
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
                } else if (id == R.id.action_home) {
                    fragment = homeFragment;
                } else if (id == R.id.action_create) {
                    fragment = createFragment;
                }else if (id == R.id.action_search) {
                    fragment = searchFragment;
                } else { // default to profileFragment
                    fragment = profileFragment;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProfileAdapter.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Load the taken image into a preview
                Bitmap takenImage = BitmapFactory.decodeFile(CameraFunctions.photoFile.getAbsolutePath());
                try {
                    takenImage = CameraFunctions.rotateImage(takenImage, CameraFunctions.photoFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.i(TAG, "Image could not be rotated for "+ParseUser.getCurrentUser().getUsername()+".");
                }
                ImageView ivProfileImage = findViewById(R.id.ivProfileImage);
                Glide.with(this).load(takenImage).centerCrop().into(ivProfileImage);
                // Save the photo to Parse database
                ParseQueries.saveProfileImage(this, CameraFunctions.photoFile, ivProfileImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CreateImageFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Load the taken image into a preview
                Bitmap takenImage = BitmapFactory.decodeFile(CameraFunctions.photoFile.getAbsolutePath());
                try {
                    takenImage = CameraFunctions.rotateImage(takenImage, CameraFunctions.photoFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.i(TAG, "Image could not be rotated for "+ParseUser.getCurrentUser().getUsername()+".");
                }
                ImageView imageView = findViewById(R.id.imageView);
                Glide.with(this).load(takenImage).centerCrop().into(imageView);
                imageView.setTag("NonEmpty");
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CameraFunctions.REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                VideoView videoView = findViewById(R.id.videoView);
                videoView.setVideoURI(videoUri);
                videoView.setBackgroundColor(Color.TRANSPARENT);
                videoView.seekTo(1);
                Log.i(TAG, "Video was set.");
            } else {
                Toast.makeText(this, "Video wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}