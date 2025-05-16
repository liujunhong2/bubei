package com.example.bubei;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private int[] bgImages = {
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBackground();
        initButtons();
    }

    private void initBackground() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean useRandom = prefs.getBoolean("bg_random", true);
        int bgId;
        if (useRandom) {
            bgId = bgImages[new Random().nextInt(bgImages.length)];
        } else {
            bgId = prefs.getInt("background_id", bgImages[0]);
        }
        ImageView bg = findViewById(R.id.bg_image);
        bg.setImageResource(bgId);
    }

    private void initButtons() {
        findViewById(R.id.btn_learn)
                .setOnClickListener(v -> startActivity(new Intent(this, LearnActivity.class)));
        findViewById(R.id.btn_review)
                .setOnClickListener(v -> startActivity(new Intent(this, ReviewActivity.class)));
        findViewById(R.id.btn_settings)
                .setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        ((ImageButton)findViewById(R.id.btn_search))
                .setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }
}
