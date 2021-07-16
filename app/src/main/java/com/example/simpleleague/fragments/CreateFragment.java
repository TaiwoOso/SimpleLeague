package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateFragment extends Fragment {

    public static final String TAG = "CreateFragment";
    EditText etTitle;
    EditText etBody;
    Button btnPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize Fields
        etTitle = view.findViewById(R.id.etTitle);
        etBody = view.findViewById(R.id.etBody);
        btnPost = view.findViewById(R.id.btnPost);
        // Listeners
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    private void post() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String title = etTitle.getText().toString();
        String body = etBody.getText().toString();
        // Check for empty fields
        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Post post = new Post();
        post.setUser(currentUser);
        post.setTitle(title);
        post.setBody(body);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "Posted!", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    etBody.setText("");
                } else {
                    Toast.makeText(getContext(), "Error: Try Again!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error while posting for "+currentUser.getUsername(), e);
                }
            }
        });
    }
}