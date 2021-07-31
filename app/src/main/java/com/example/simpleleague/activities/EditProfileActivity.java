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
import com.example.simpleleague.ParseFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ProfileAdapter;
import com.example.simpleleague.models.User;
import com.parse.ParseUser;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "EditProfileActivity";

    private ParseUser mCurrentUser = ParseUser.getCurrentUser();
    private ImageView mIvProfileImage;
    private TextView mTvChangeProfile;
    private TextView mTvCancel;
    private TextView mTvDone;
    private EditText mEtUsername;
    private EditText mEtBio;
    private boolean mProfileImageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mIvProfileImage = findViewById(R.id.ivProfileImage);
        mTvChangeProfile = findViewById(R.id.tvChangeProfile);
        mTvCancel = findViewById(R.id.tvCancel);
        mTvDone = findViewById(R.id.tvDone);
        mEtUsername = findViewById(R.id.etUsername);
        mEtBio = findViewById(R.id.etBio);
        ParseFunctions.loadProfileImage(mIvProfileImage, this, mCurrentUser);
        mEtUsername.setText(mCurrentUser.getUsername());
        ParseFunctions.setBio(mEtBio, mCurrentUser);
        mTvChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the photo
                CameraFunctions.launchCamera(EditProfileActivity.this, ProfileAdapter.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                // onActivityResult() takes care of the rest
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProfileImageChanged) {
                    ParseFunctions.saveProfileImage(EditProfileActivity.this, CameraFunctions.photoFile, mIvProfileImage);
                }
                if (!mEtUsername.getText().toString().equals(mCurrentUser.getUsername())) {
                    ParseFunctions.saveUsername(EditProfileActivity.this, mEtUsername.getText().toString());
                }
                if (!mEtBio.getText().toString().equals(mCurrentUser.getString(User.KEY_BIO))) {
                    ParseFunctions.saveBio(EditProfileActivity.this, mEtBio.getText().toString());
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
                Bitmap takenImage = BitmapFactory.decodeFile(CameraFunctions.photoFile.getAbsolutePath());
                try {
                    takenImage = CameraFunctions.rotateImage(takenImage, CameraFunctions.photoFile.getAbsolutePath());
                } catch (IOException ignored) {}
                ImageView ivProfileImage = findViewById(R.id.ivProfileImage);
                Glide.with(this).load(takenImage).centerCrop().into(ivProfileImage);
                mProfileImageChanged = true;
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}