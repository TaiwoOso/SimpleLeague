package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateFragment extends Fragment {

    public static final String TAG = "CreateFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tlCreate = view.findViewById(R.id.tlCreate);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment textFragment = new CreateTextFragment();
        Fragment imageFragment = new CreateImageFragment();
        Fragment videoFragment = new CreateVideoFragment();
        tlCreate.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String text = tab.getText().toString();
                Fragment fragment;
                if (text.equals("Text")) {
                    fragment = textFragment;
                } else if (text.equals("Image")) {
                    fragment = imageFragment;
                } else {
                    fragment = videoFragment;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        fragmentManager.beginTransaction().replace(R.id.flContainer, textFragment).commit();
    }
}