package com.example.simpleleague;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.models.Champion;
import com.example.simpleleague.models.Post;

import org.parceler.Parcels;

public class ChampionDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ChampionDetailsActivity";
    private ImageView ivChampionImage;
    private TextView tvChampionName;
    private TextView tvChampionLore;
    private Champion champion;
    private TextView tvQName;
    private TextView tvWName;
    private TextView tvEName;
    private TextView tvRName;
    private TextView tvPName;
    private ImageView ivQImage;
    private ImageView ivWImage;
    private ImageView ivEImage;
    private ImageView ivRImage;
    private ImageView ivPImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_details);
        // Initialize fields
        ivChampionImage = findViewById(R.id.ivChampionImage);
        tvChampionName = findViewById(R.id.tvName);
        tvChampionLore = findViewById(R.id.tvLore);
        tvQName = findViewById(R.id.tvQName);
        tvWName = findViewById(R.id.tvWName);
        tvEName = findViewById(R.id.tvEName);
        tvRName = findViewById(R.id.tvRName);
        tvPName = findViewById(R.id.tvPName);
        ivQImage = findViewById(R.id.ivChampionQImage);
        ivWImage = findViewById(R.id.ivChampionWImage);
        ivEImage = findViewById(R.id.ivChampionEImage);
        ivRImage = findViewById(R.id.ivChampionRImage);
        ivPImage = findViewById(R.id.ivChampionPImage);
        // Unwrap the champion passed via intent
        champion = (Champion) Parcels.unwrap(getIntent().getParcelableExtra(Champion.class.getSimpleName()));
        Log.d(TAG, String.format("Showing champion details for %s", champion.getName())+".");
        // Set the fields
        Glide.with(this).load(champion.getImage().getUrl()).placeholder(R.drawable.default_profile_image).centerCrop().into(ivChampionImage);
        tvChampionName.setText(champion.getName());
        tvChampionLore.setText(champion.getLore());
        tvQName.setText(champion.getQName());
        tvWName.setText(champion.getWName());
        tvEName.setText(champion.getEName());
        tvRName.setText(champion.getRName());
        tvPName.setText(champion.getPName());
        Glide.with(this).load(champion.getQImageUrl()).centerCrop().into(ivQImage);
        Glide.with(this).load(champion.getWImageUrl()).centerCrop().into(ivWImage);
        Glide.with(this).load(champion.getEImageUrl()).centerCrop().into(ivEImage);
        Glide.with(this).load(champion.getRImageUrl()).centerCrop().into(ivRImage);
        Glide.with(this).load(champion.getPImageUrl()).centerCrop().into(ivPImage);
    }
}