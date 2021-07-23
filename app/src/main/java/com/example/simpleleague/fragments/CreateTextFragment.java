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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.simpleleague.CameraFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateTextFragment extends Fragment {

    public static final String TAG = "CreateTextFragment";
    public EditText etTitle;
    private EditText etBody;
    private Button btnPost;
    public EditText etTag;
    public TextView tvTag1;
    public TextView tvTag2;
    private ImageButton ibtnAddTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize Fields
        etTitle = view.findViewById(R.id.etTitle);
        etBody = view.findViewById(R.id.etBody);
        btnPost = view.findViewById(R.id.btnPost);
        etTag = view.findViewById(R.id.etTag);
        tvTag1 = view.findViewById(R.id.tvTag1);
        tvTag2 = view.findViewById(R.id.tvTag2);
        ibtnAddTag = view.findViewById(R.id.ibtnAddTag);
        // Listeners
        ibtnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    public void addTag() {
        String tag = etTag.getText().toString();
        if (tag.isEmpty()) {
            Toast.makeText(getContext(), "Tag is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!tag.startsWith("#")) {
            Toast.makeText(getContext(), "Tags start with #!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tvTag1.getText().toString().isEmpty()) {
            tvTag1.setText(tag);
        } else if (tvTag2.getText().toString().isEmpty()) {
            tvTag2.setText(tag);
        } else {
            Toast.makeText(getContext(), "Maximum of 2 Tags!", Toast.LENGTH_SHORT).show();
        }
    }

    public void post() {
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
                    etBody.setText("");
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