package com.example.simpleleague.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateImageFragment extends CreateTextFragment {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 56;
    private ImageButton ibtnAddImage;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize Fields
        ibtnAddImage = view.findViewById(R.id.ibtnAddImage);
        imageView = view.findViewById(R.id.imageView);
        // Listeners
        listeners();
    }

    private void listeners() {
        ibtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFunctions.launchCamera(getContext(), CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void post() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String title = etTitle.getText().toString();
        // Check for empty fields
        if (title.isEmpty() || imageView.getTag().equals("Empty")) {
            Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Post post = new Post();
        post.setUser(currentUser);
        post.setTitle(title);
        post.setImage(new ParseFile(CameraFunctions.photoFile));
        ArrayList<String> tags = new ArrayList<>(Arrays.asList(tvTag1.getText().toString(), tvTag2.getText().toString()));
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);
            if (tag.isEmpty()) {
                tags.remove(i);
                i--;
            }
        }
        post.setTags(tags);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "Posted!", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    imageView.setTag("Empty");
                    Glide.with(getContext()).load(R.drawable.image_placeholder).into(imageView);
                    etTag.setText("");
                    tvTag1.setText("");
                    tvTag2.setText("");
                } else {
                    Toast.makeText(getContext(), "Error: Try Again!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error while posting for "+currentUser.getUsername()+".", e);
                }
            }
        });
    }
}