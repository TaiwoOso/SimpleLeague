package com.example.simpleleague.fragments;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateVideoFragment extends CreateTextFragment {

    private ImageButton ibtnAddVideo;
    private VideoView videoView;
    private ImageButton ibtnPlayVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize Fields
        ibtnAddVideo = view.findViewById(R.id.ibtnAddVideo);
        videoView = view.findViewById(R.id.videoView);
        ibtnPlayVideo = view.findViewById(R.id.ibtnPlayVideo);
        // Listeners
        listeners();
    }

    private void listeners() {
        ibtnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFunctions.dispatchTakeVideoIntent(getContext());
                ibtnPlayVideo.setVisibility(View.VISIBLE);
            }
        });
        ibtnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    ibtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
                } else {
                    videoView.start();
                    ibtnPlayVideo.setBackgroundResource(R.drawable.ic_pause_button);
                }
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ibtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
            }
        });
    }

    @Override
    public void post() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String title = etTitle.getText().toString();
        // Check for empty fields
        if (title.isEmpty() || videoView.getDuration() == -1) {
            Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Post post = new Post();
        post.setUser(currentUser);
        post.setTitle(title);
        post.setVideo(new ParseFile(CameraFunctions.videoFile));
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
                    videoView.stopPlayback();
                    videoView.setBackgroundColor(Color.BLACK);
                    videoView.start();
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