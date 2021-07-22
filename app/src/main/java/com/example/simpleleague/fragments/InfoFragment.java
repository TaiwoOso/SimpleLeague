package com.example.simpleleague.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleleague.EndlessRecyclerViewScrollListener;
import com.example.simpleleague.R;
import com.example.simpleleague.adapters.ChampionsAdapter;
import com.example.simpleleague.models.Champion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class InfoFragment extends Fragment {

    public static final String TAG = "InfoFragment";
    public static final String CHAMPIONS_JSON = "https://ddragon.leagueoflegends.com/cdn/11.14.1/data/en_US/champion.json";
    public static final String CHAMPION_JSON = "https://ddragon.leagueoflegends.com/cdn/11.14.1/data/en_US/champion/";
    public static final String CHAMPION_IMAGE_URL = "https://ddragon.leagueoflegends.com/cdn/11.14.1/img/champion/";
    public static final String CHAMPION_SPELL_URL = "https://ddragon.leagueoflegends.com/cdn/11.15.1/img/spell/";
    public static final String CHAMPION_PASSIVE_URL = "https://ddragon.leagueoflegends.com/cdn/11.15.1/img/passive/";
    private RecyclerView rvChampions;
    private List<Champion> champions;
    private ChampionsAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SearchView svSearch;

    public InfoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        svSearch = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        svSearch.setQueryHint("Search");
        // Search for specific queries
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.clear();
                String search = svSearch.getQuery().toString();
                queryChampions(0, search);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

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
        queryChampions(0, "");
        // Load more champions during scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layout) {
            @Override
            public void onLoadMore(int skips, int totalItemsCount, RecyclerView view) {
                String search = svSearch.getQuery().toString();
                queryChampions(skips, search);
            }
        };
        rvChampions.addOnScrollListener(scrollListener);
    }

    private void queryChampions(int skips, String search) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Champion> query = ParseQuery.getQuery(Champion.class);
        query.whereContains(Champion.KEY_NAME, search);
        query.addAscendingOrder(Champion.KEY_NAME);
        int limit = 20;
        query.setLimit(limit);
        query.setSkip(skips);
        query.findInBackground(new FindCallback<Champion>() {
            @Override
            public void done(List<Champion> champions, ParseException e) {
                // error checking
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving champions for "+currentUser.getUsername()+".", e);
                    return;
                }
                Log.i(TAG, "Retrieved "+champions.size()+" champions(s) for "+currentUser.getUsername()+".");
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
//private void updateChampionAbilities(Champion champion) {
//    Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            String name = champion.getName();
//            // JSONObject containing champion's data
//            String champion_json_url = CHAMPION_JSON+name+".json";
//            JSONObject champion_data;
//            try {
//                champion_data = getJSONObjectFromURL(champion_json_url).getJSONObject("data").getJSONObject(name);
//            } catch (JSONException | IOException e) {
//                Log.i(TAG, name+" wasn't updated. - data", e);
//                return;
//            }
//            JSONArray spells;
//            try {
//                spells = champion_data.getJSONArray("spells");
//            } catch (JSONException e) {
//                Log.i(TAG, name+" wasn't updated. - spells", e);
//                return;
//            }
//            JSONObject passive;
//            try {
//                passive = champion_data.getJSONObject("passive");
//            } catch (JSONException e) {
//                Log.i(TAG, name+" wasn't updated. - passive", e);
//                return;
//            }
//            // Iterate through spells
//            for (int i = 0; i < spells.length(); i++) {
//                try {
//                    JSONObject spell = spells.getJSONObject(i);
//                    JSONObject image = spell.getJSONObject("image");
//                    switch (i) {
//                        case 0:
//                            champion.setQName(spell.getString("name"));
//                            champion.setQImageUrl(CHAMPION_SPELL_URL+image.getString("full"));
////                                Log.i(TAG, name+"Q: "+spell.getString("name")+" "+CHAMPION_SPELL_URL+image.getString("full"));
//                            break;
//                        case 1:
//                            champion.setWName(spell.getString("name"));
//                            champion.setWImageUrl(CHAMPION_SPELL_URL+image.getString("full"));
////                                Log.i(TAG, name+"W: "+spell.getString("name")+" "+CHAMPION_SPELL_URL+image.getString("full"));
//                            break;
//                        case 2:
//                            champion.setEName(spell.getString("name"));
//                            champion.setEImageUrl(CHAMPION_SPELL_URL+image.getString("full"));
////                                Log.i(TAG, name+"E: "+spell.getString("name")+" "+CHAMPION_SPELL_URL+image.getString("full"));
//                            break;
//                        case 3:
//                            champion.setRName(spell.getString("name"));
//                            champion.setRImageUrl(CHAMPION_SPELL_URL+image.getString("full"));
////                                Log.i(TAG, name+"R: "+spell.getString("name")+" "+CHAMPION_SPELL_URL+image.getString("full"));
//                            break;
//                    }
//                } catch (JSONException e) {
//                    Log.i(TAG, name+" wasn't updated. - spell", e);
//                    return;
//                }
//            }
//            // Passive
//            try {
//                JSONObject image = passive.getJSONObject("image");
//                champion.setPName(passive.getString("name"));
//                champion.setPImageUrl(CHAMPION_PASSIVE_URL+image.getString("full"));
////                    Log.i(TAG, name+"P: "+passive.getString("name")+" "+CHAMPION_PASSIVE_URL+image.getString("full"));
//            } catch (JSONException e) {
//                Log.i(TAG, name+" wasn't updated. - passive", e);
//                return;
//            }
////                Log.i(TAG,  name+" Loaded.");
//            champion.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e != null) {
//                        Log.i(TAG, name+" wasn't saved.", e);
//                        return;
//                    }
//                    Log.i(TAG, name+" was saved.");
//                }
//            });
//        }
//    });
//    thread.start();
//}
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
////        System.out.println("JSON: " + jsonString);
//
//        return new JSONObject(jsonString);
//    }

}