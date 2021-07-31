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

    private ImageButton mIbtnAddVideo;
    private VideoView mVideoView;
    private ImageButton mIbtnPlayVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIbtnAddVideo = view.findViewById(R.id.ibtnAddVideo);
        mVideoView = view.findViewById(R.id.videoView);
        mIbtnPlayVideo = view.findViewById(R.id.ibtnPlayVideo);
        mIbtnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFunctions.dispatchTakeVideoIntent(getContext());
                mIbtnPlayVideo.setVisibility(View.VISIBLE);
            }
        });
        mIbtnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mIbtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
                } else {
                    mVideoView.start();
                    mIbtnPlayVideo.setBackgroundResource(R.drawable.ic_pause_button);
                }
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mIbtnPlayVideo.setBackgroundResource(R.drawable.ic_play_button);
            }
        });
    }

    @Override
    public void post() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String title = mEtTitle.getText().toString();
        if (title.isEmpty() || mVideoView.getDuration() == -1) {
            Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Post post = new Post();
        post.setUser(currentUser);
        post.setTitle(title);
        post.setVideo(new ParseFile(CameraFunctions.videoFile));
        ArrayList<String> tags = new ArrayList<>(Arrays.asList(mTvTag1.getText().toString(), mTvTag2.getText().toString()));
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
                if (e != null) {
                    Log.e(TAG, "Error while posting for "+currentUser.getUsername()+".", e);
                    Toast.makeText(getContext(), "Error: Try Again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Posted!", Toast.LENGTH_SHORT).show();
                mEtTitle.setText("");
                mVideoView.stopPlayback();
                mVideoView.setBackgroundColor(Color.BLACK);
                mVideoView.start();
                mEtTag.setText("");
                mTvTag1.setText("");
                mTvTag2.setText("");
            }
        });
    }
}