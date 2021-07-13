package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ChampionsAdapter;
import com.example.simpleleague.models.Champion;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        queryChampions();
    }

    private void queryChampions() {
        // Specify which class to query
        ParseQuery<Champion> query = ParseQuery.getQuery(Champion.class);
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