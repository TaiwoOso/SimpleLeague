package com.example.simpleleague.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.ParseQueries;
import com.example.simpleleague.R;
import com.example.simpleleague.SignupActivity;
import com.example.simpleleague.adapters.ChampionsAdapter;
import com.example.simpleleague.models.Champion;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.User;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InfoFragment extends Fragment {

    public static final String TAG = "InfoFragment";
    public static final String CHAMPIONS_JSON = "https://ddragon.leagueoflegends.com/cdn/11.14.1/data/en_US/champion.json";
    public static final String CHAMPION_JSON = "https://ddragon.leagueoflegends.com/cdn/11.14.1/data/en_US/champion/";
    public static final String CHAMPION_IMAGE_URL = "https://ddragon.leagueoflegends.com/cdn/11.14.1/img/champion/";
    private RecyclerView rvChampions;
    private List<Champion> champions;
    private ChampionsAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize fields
        rvChampions = view.findViewById(R.id.rvChampions);
        champions = new ArrayList<>();
        adapter = new ChampionsAdapter(getContext(), champions);
        // RecyclerView
        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        rvChampions.setAdapter(adapter);
        rvChampions.setLayoutManager(layout);
        // Get the champions from Parse and notify adapter
        queryChampions(0);
        // Load more champions during scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextChampionsFromParse(skips);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvChampions.addOnScrollListener(scrollListener);
    }


    // Append the next "page" of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private void loadNextChampionsFromParse(int skips) {
        // Send an API request to retrieve appropriate "paginated" data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()
        queryChampions(skips);
    }

    private void queryChampions(int skips) {
        // Get the current user
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Specify which class to query
        ParseQuery<Champion> query = ParseQuery.getQuery(Champion.class);
        // limit query to [limit] champions
        int limit = 20;
        query.setLimit(limit);
        // skip this amount
        query.setSkip(skips);
        query.findInBackground(new FindCallback<Champion>() {
            @Override
            public void done(List<Champion> champions, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving champions for "+currentUser.getUsername()+".", e);
                    return;
                }
                // log champions retrieved
//                for (Champion champion : champions) {
//                    Log.i(TAG, champion.getName()+" retrieved!");
//                }
                // save received posts to list and notify adapter of new data
                adapter.addAll(champions);
                adapter.notifyItemRangeInserted(skips, skips+limit);
            }
        });
    }

//    // Gets the champions from Riot's DataDragon -- Clear Parse Database first
//    private void getChampionsFromDataDragon() {
//        Thread thread = new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    // Gets data containing champions from url
//                    JSONObject data = getJSONObjectFromURL(CHAMPIONS_JSON).getJSONObject("data");
//                    Iterator<String> iter = data.keys();
//                    // Gets data for individual champions
//                    while (iter.hasNext()) {
//                        // Champion name
//                        String key = iter.next();
//                        // JSONObject containing champion's data
//                        String champion_json_url = CHAMPION_JSON+key+".json";
//                        JSONObject champion_data = getJSONObjectFromURL(champion_json_url).getJSONObject("data").getJSONObject(key);
//                        // Champion's image url
//                        String champion_image_url = CHAMPION_IMAGE_URL+key+".png";
//                        // Champion's lore/description
//                        String lore = champion_data.getString("lore");
//                        // Save champion to Parse database
//                        saveChampion(key, champion_image_url, lore);
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, e.getMessage(), e);
//                }
//                Log.i(TAG, "Finished saving champions!");
//            }
//        });
//        thread.start();
//    }
//
//    // Saves champions to Parse Database
//    private void saveChampion(String name, String imageUrl, String lore) {
//        Champion champion = new Champion();
//        champion.setName(name);
//        champion.setImageUrl(imageUrl);
//        champion.setLore(lore);
//        champion.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, "Error while saving champion: "+ name, e);
//                } else {
//                    Log.i(TAG, "Champion saved: "+ name);
//                }
//            }
//        });
//    }
//
//    private void createAccountForChampion(Champion champion) {
//        ParseUser parseUser = new ParseUser();
//        parseUser.setUsername(champion.getName());
//        parseUser.setPassword(champion.getName());
//        parseUser.signUpInBackground(new SignUpCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.i(TAG, "Account created for "+parseUser.getUsername()+".");
//                    Follow follow = ParseQueries.createFollow(parseUser);
//                    parseUser.put(User.KEY_FOLLOW, follow);
//                    parseUser.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                Log.i(TAG, "Follow was set for " + parseUser.getUsername() + ".");
//                            } else {
//                                Log.i(TAG, "Follow was not set for " + parseUser.getUsername() + ".", e);
//                            }
//                        }
//                    });
//                } else {
//                    Log.i(TAG, "Account not created for "+parseUser.getUsername()+".");
//                }
//            }
//        });
//    }
//
//    private void loginChampionUser(Champion champion) {
//        ParseUser.logInInBackground(champion.getName(), champion.getName(), new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (e == null) {
//                    updateChampion(champion, user);
//                } else {
//                    Log.i(TAG, "Unable to retrieve account for "+champion.getName());
//                }
//            }
//        });
//    }
//
//    private void updateChampionUser(Champion champion, ParseUser parseUser) {
//        parseUser.put(User.KEY_BIO, champion.getLore());
//        parseUser.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.i(TAG, parseUser.getUsername()+" updated.");
//                } else {
//                    Log.e(TAG, parseUser.getUsername()+" not updated.", e);
//                }
//            }
//        });
//    }
//
//    private void updateChampionImage(Champion champion) {
//        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
//        query.whereEqualTo(User.KEY_USERNAME, champion.getName());
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> parseUsers, ParseException e) {
//                if (e == null) {
//                    ParseUser parseUser = parseUsers.get(0);
//                    champion.put(Champion.KEY_IMAGE, parseUser.getParseFile(User.KEY_PROFILE_IMAGE));
//                    champion.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                Log.i(TAG, champion.getName()+" updated.");
//                            } else {
//                                Log.i(TAG, champion.getName()+" not updated.", e);
//                            }
//                        }
//                    });
//                } else {
//                    Log.i(TAG, "Couldn't retrieve user for champion.", e);
//                }
//            }
//        });
//    }
//
//    // Creates json objects from url
//    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
//        HttpURLConnection urlConnection = null;
//        URL url = new URL(urlString);
//        urlConnection = (HttpURLConnection) url.openConnection();
//        urlConnection.setRequestMethod("GET");
//        urlConnection.setReadTimeout(10000 /* milliseconds */ );
//        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
//        urlConnection.connect();
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//        StringBuilder sb = new StringBuilder();
//
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line + "\n");
//        }
//        br.close();
//
//        String jsonString = sb.toString();
//        System.out.println("JSON: " + jsonString);
//
//        return new JSONObject(jsonString);
//    }

}