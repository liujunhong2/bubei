package com.example.bubei;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
public class MainActivity extends AppCompatActivity {
    private RelativeLayout mainLayout;
    private int[] bgImages = {
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.main_layout);

        int selectedBg = getRandomBackground();
        ImageView bgImage = findViewById(R.id.bg_image);
        bgImage.setImageResource(selectedBg);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("background_id", selectedBg).apply();
        // 其他界面
//        int bgId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("background_id", R.drawable.bg1);
//        bgImage.setImageResource(bgId);

        Button btnLearn = findViewById(R.id.btn_learn);
        Button btnReview = findViewById(R.id.btn_review);
        Button btnSettings = findViewById(R.id.btn_settings);
        btnLearn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LearnActivity.class)));
        btnReview.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReviewActivity.class)));
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

    }
    private int getRandomBackground() {
        Random random = new Random();
        return bgImages[random.nextInt(bgImages.length)];
    }

}
