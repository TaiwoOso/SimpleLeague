package com.example.simpleleague.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ProfileAdapter;
import com.example.simpleleague.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseUser;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ParseUser currentUser = ParseUser.getCurrentUser();
    private ImageView ivProfileImage;
    private TextView tvChangeProfile;
    private TextView tvCancel;
    private TextView tvDone;
    private EditText etUsername;
    private EditText etBio;
    private boolean profileImageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        // Initialize Fields
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvChangeProfile = findViewById(R.id.tvChangeProfile);
        tvCancel = findViewById(R.id.tvCancel);
        tvDone = findViewById(R.id.tvDone);
        etUsername = findViewById(R.id.etUsername);
        etBio = findViewById(R.id.etBio);
        // Load user information
        ParseQueries.loadProfileImage(ivProfileImage, this, currentUser);
        etUsername.setText(currentUser.getUsername());
        ParseQueries.setBio(etBio, currentUser);
        // Listeners
        listeners();
    }

    private void listeners() {
        tvChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the photo
                CameraFunctions.launchCamera(EditProfileActivity.this, ProfileAdapter.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                // onActivityResult() takes care of the rest
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileImageChanged) {
                    ParseQueries.saveProfileImage(EditProfileActivity.this, CameraFunctions.photoFile, ivProfileImage);
                }
                if (!etUsername.getText().toString().equals(currentUser.getUsername())) {
                    ParseQueries.saveUsername(EditProfileActivity.this, etUsername.getText().toString());
                }
                if (!etBio.getText().toString().equals(currentUser.getString(User.KEY_BIO))) {
                    ParseQueries.saveBio(EditProfileActivity.this, etBio.getText().toString());
                }
                Toast.makeText(EditProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProfileAdapter.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Load the taken image into a preview
                Bitmap takenImage = BitmapFactory.decodeFile(CameraFunctions.photoFile.getAbsolutePath());
                try {
                    takenImage = CameraFunctions.rotateImage(takenImage, CameraFunctions.photoFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.i(TAG, "Image could not be rotated for "+ ParseUser.getCurrentUser().getUsername()+".");
                }
                ImageView ivProfileImage = findViewById(R.id.ivProfileImage);
                Glide.with(this).load(takenImage).centerCrop().into(ivProfileImage);
                profileImageChanged = true;
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}