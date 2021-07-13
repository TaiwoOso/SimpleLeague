package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ChampionsAdapter;
import com.example.simpleleague.models.Champion;

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

        // Get the champions and notify adapter
        queryChampions();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }, 20000);

//        for (Champion champion : champions) {
//            Log.i(TAG, champion.getName()+": "+champion.getImageUrl());
//        }
    }

    private void queryChampions() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    // Gets data containing champions from url
                    JSONObject data = getJSONObjectFromURL(CHAMPIONS_JSON).getJSONObject("data");
                    Iterator<String> iter = data.keys();
                    // Gets data for individual champions
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String champion_json_url = CHAMPION_JSON+key+".json";
                        String champion_image_url = CHAMPION_IMAGE_URL+key+".png";
                        JSONObject champion_data = getJSONObjectFromURL(champion_json_url).getJSONObject("data").getJSONObject(key);
                        Champion champion = new Champion();
                        champion.setName(key);
                        champion.setImageUrl(champion_image_url);
                        champions.add(champion);

//                        Log.i(TAG, key+": "+champion_image_url);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                Log.i(TAG, "Finished!");
//                adapter.notifyDataSetChanged();
//                for (Champion champion : champions) {
//                    Log.i(TAG, champion.getName()+": "+champion.getImageUrl());
//                }
            }
        });
        thread.start();
    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
//        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

}