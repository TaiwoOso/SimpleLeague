package com.example.simpleleague;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnLogIn;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Check for Data passed through Intent
        String[] credentials = getIntent().getStringArrayExtra(LoginActivity.class.getSimpleName());
        if (credentials != null) {
            String username = credentials[0];
            String password = credentials[1];
            signup(username, password);
        }
        // Initialize fields
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        // Signup --> MainActivity
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPasswords();
            }
        });
        // Login --> LoginActivity
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPasswords() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String conformPassword = etConfirmPassword.getText().toString();
        // Check if passwords match
        if (!password.equals(conformPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        signup(username, password);
    }

    private void signup(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SignupActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
                    Follow follow = ParseQueries.createFollow(user);
                    user.put(User.KEY_FOLLOW, follow);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "Follow was set for "+user.getUsername()+".");
                            } else {
                                Log.i(TAG, "Follow was not set for "+user.getUsername()+".", e);
                            }
                        }
                    });
                    goMainActivity();
                } else {
                    Log.e(TAG, "Signup failed.", e);
                    Toast.makeText(SignupActivity.this, "Signup failed! Try different username.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}