package com.example.simpleleague;

import com.parse.Parse;
import android.app.Application;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fa9c9CXfrCUaoWNaTeNVH5xHHmJ0SsqWuYwKndZd")
                .clientKey("o3RWfrwKcAKMvtmOZNLPFnjMLxU0xNfWWYX74CzP")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}