package com.example.simpleleague.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simpleleague.ParseFunctions;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";

    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private TextView mTvLogIn;
    private Button mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Check for Data passed through Intent via Google SignUp
        String[] credentials = getIntent().getStringArrayExtra(LoginActivity.class.getSimpleName());
        if (credentials != null) {
            String username = credentials[0];
            String password = credentials[1];
            signup(username, password);
            return;
        }
        mEtUsername = findViewById(R.id.etUsername);
        mEtPassword = findViewById(R.id.etPassword);
        mEtConfirmPassword = findViewById(R.id.etConfirmPassword);
        mTvLogIn = findViewById(R.id.tvLogIn);
        mBtnSignUp = findViewById(R.id.btnSignUp);
        // Signup --> MainActivity
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPasswords();
            }
        });
        // Login --> LoginActivity
        mTvLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        finish();
    }

    private void checkPasswords() {
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String conformPassword = mEtConfirmPassword.getText().toString();
        // Check if passwords match
        if (!password.equals(conformPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        signup(username, password);
    }

    /**
     * Creates a new account with the passed in credentials
     * and assigns a Follow object to the new account
     * @param username credential
     * @param password credential
     */
    private void signup(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Signup failed.", e);
                    Toast.makeText(SignupActivity.this, "Signup failed! Try different username.", Toast.LENGTH_LONG).show();
                    return;
                }
                Follow follow = ParseFunctions.createFollow(user);
                user.put(User.KEY_FOLLOW, follow);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Follow was not set for "+user.getUsername()+".", e);
                        }
                    }
                });
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}