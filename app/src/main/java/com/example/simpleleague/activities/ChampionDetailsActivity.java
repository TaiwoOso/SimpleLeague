package com.example.simpleleague.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simpleleague.R;
import com.example.simpleleague.models.Champion;
import com.example.simpleleague.models.Post;

import org.parceler.Parcels;

public class ChampionDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ChampionDetailsActivity";

    private ImageView mIvChampionImage;
    private TextView mTvChampionName;
    private TextView mTvChampionLore;
    private Champion mChampion;
    private TextView mTvQName;
    private TextView mTvWName;
    private TextView mTvEName;
    private TextView mTvRName;
    private TextView mTvPName;
    private ImageView mIvQImage;
    private ImageView mIvWImage;
    private ImageView mIvEImage;
    private ImageView mIvRImage;
    private ImageView mIvPImage;
    private ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_details);
        mIvChampionImage = findViewById(R.id.ivChampionImage);
        mTvChampionName = findViewById(R.id.tvName);
        mTvChampionLore = findViewById(R.id.tvLore);
        mTvQName = findViewById(R.id.tvQName);
        mTvWName = findViewById(R.id.tvWName);
        mTvEName = findViewById(R.id.tvEName);
        mTvRName = findViewById(R.id.tvRName);
        mTvPName = findViewById(R.id.tvPName);
        mIvQImage = findViewById(R.id.ivChampionQImage);
        mIvWImage = findViewById(R.id.ivChampionWImage);
        mIvEImage = findViewById(R.id.ivChampionEImage);
        mIvRImage = findViewById(R.id.ivChampionRImage);
        mIvPImage = findViewById(R.id.ivChampionPImage);
        ibBack = findViewById(R.id.ibBack);
        mChampion = Parcels.unwrap(getIntent().getParcelableExtra(Champion.class.getSimpleName()));
        Glide.with(this).load(mChampion.getImage().getUrl()).placeholder(R.drawable.default_profile_image).centerCrop().into(mIvChampionImage);
        mTvChampionName.setText(mChampion.getName());
        mTvChampionLore.setText(mChampion.getLore());
        mTvQName.setText(mChampion.getQName());
        mTvWName.setText(mChampion.getWName());
        mTvEName.setText(mChampion.getEName());
        mTvRName.setText(mChampion.getRName());
        mTvPName.setText(mChampion.getPName());
        Glide.with(this).load(mChampion.getQImageUrl()).centerCrop().into(mIvQImage);
        Glide.with(this).load(mChampion.getWImageUrl()).centerCrop().into(mIvWImage);
        Glide.with(this).load(mChampion.getEImageUrl()).centerCrop().into(mIvEImage);
        Glide.with(this).load(mChampion.getRImageUrl()).centerCrop().into(mIvRImage);
        Glide.with(this).load(mChampion.getPImageUrl()).centerCrop().into(mIvPImage);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}