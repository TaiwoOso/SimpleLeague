package com.example.simpleleague;

import android.app.Application;

import com.example.simpleleague.models.Champion;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Champion.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Follow.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.PARSE_APPLICATION_ID)
                .clientKey(BuildConfig.PARSE_CLIENT_KEY)
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}