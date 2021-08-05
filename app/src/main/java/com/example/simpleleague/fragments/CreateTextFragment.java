package com.example.simpleleague.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.simpleleague.R;
import com.example.simpleleague.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateTextFragment extends Fragment {

    public static final String TAG = "CreateTextFragment";
    public EditText mEtTitle;
    public EditText mEtTag;
    public TextView mTvTag1;
    public TextView mTvTag2;
    public ProgressBar mProgressBar;

    private EditText mEtBody;
    private Button mBtnPost;
    private ImageButton mIbtnAddTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEtTitle = view.findViewById(R.id.etTitle);
        mEtBody = view.findViewById(R.id.etBody);
        mBtnPost = view.findViewById(R.id.btnPost);
        mEtTag = view.findViewById(R.id.etTag);
        mTvTag1 = view.findViewById(R.id.tvTag1);
        mTvTag2 = view.findViewById(R.id.tvTag2);
        mProgressBar = view.findViewById(R.id.progressBar);
        mIbtnAddTag = view.findViewById(R.id.ibtnAddTag);
        mIbtnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });
        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    public void addTag() {
        String tag = mEtTag.getText().toString();
        if (tag.isEmpty()) {
            Toast.makeText(getContext(), "Tag is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!tag.startsWith("#")) {
            Toast.makeText(getContext(), "Tags start with #!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mTvTag1.getText().toString().isEmpty()) {
            mTvTag1.setText(tag);
        } else if (mTvTag2.getText().toString().isEmpty()) {
            mTvTag2.setText(tag);
        } else {
            Toast.makeText(getContext(), "Maximum of 2 Tags!", Toast.LENGTH_SHORT).show();
        }
    }

    public void post() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String title = mEtTitle.getText().toString();
        String body = mEtBody.getText().toString();
        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        Post post = new Post();
        post.setUser(currentUser);
        post.setTitle(title.trim());
        post.setBody(body.trim());
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
                mEtBody.setText("");
                mEtTag.setText("");
                mTvTag1.setText("");
                mTvTag2.setText("");
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}