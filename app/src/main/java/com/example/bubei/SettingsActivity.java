package com.example.bubei;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class SettingsActivity extends AppCompatActivity {
    private Spinner spBg, spCount, spReview;
    private Button btnSave;
    private ImageButton btnBack;
    private final int[] bgImages = {0, R.drawable.bg1, R.drawable.bg2, R.drawable.bg3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 清除半透明状态栏标志
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 让布局显示在状态栏下方
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            // 让系统绘制状态栏背景并把状态栏背景设为透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spBg = findViewById(R.id.sp_background);
        spCount = findViewById(R.id.sp_count);
        spReview = findViewById(R.id.sp_review_count);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back_settings);
        setupBackgroundSpinner();
        setupCountSpinner();
        setupReviewSpinner();
        btnSave.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "设置已保存，背景设置需要重启生效", Toast.LENGTH_SHORT).show();
        });
        btnBack.setOnClickListener(v -> finish());
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        ImageView bg = findViewById(R.id.bg_settings);
        int bgId = prefs.getInt("background_id", R.drawable.bg1);
        bg.setImageResource(bgId);
    }
    private void setupBackgroundSpinner() {
        String[] options = {"随机", "背景 1", "背景 2", "背景 3"};
        spBg.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, options));
        SharedPreferences p = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean rand = p.getBoolean("bg_random", true);
        spBg.setSelection(rand ? 0 : p.getInt("bg_index", 1));
    }
    private void setupCountSpinner() {
        Integer[] counts = {5, 10, 15, 20};
        spCount.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, counts));
        int saved = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getInt("session_count", 10);
        spCount.setSelection(java.util.Arrays.asList(counts).indexOf(saved));
    }
    private void setupReviewSpinner() {
        Integer[] reviewCounts = {3, 5, 10, 15};
        spReview.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, reviewCounts));
        int saved = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getInt("review_session_limit", 5);
        spReview.setSelection(java.util.Arrays.asList(reviewCounts).indexOf(saved));
    }
    private void saveSettings() {
        SharedPreferences p = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        int bgIdx = spBg.getSelectedItemPosition();
        e.putBoolean("bg_random", bgIdx == 0);
        e.putInt("bg_index", bgIdx);
        if (bgIdx != 0) {
            e.putInt("background_id", bgImages[bgIdx]);
        }
        int cnt = (Integer) spCount.getSelectedItem();
        e.putInt("session_count", cnt);
        int reviewCnt = (Integer) spReview.getSelectedItem();
        e.putInt("review_session_limit", reviewCnt);
        e.apply();
    }
}
