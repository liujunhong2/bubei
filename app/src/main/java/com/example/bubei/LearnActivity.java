package com.example.bubei;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bubei.db.WordDao;
import com.example.bubei.model.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LearnActivity extends AppCompatActivity {
    private TextView tvWord, tvPhonetic, tvSentence;
    private TextView tvProgress, tvProficiencyIndicator;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnKnow, btnDontKnow;
    private View level0Layout, btnGroup;

    private WordDao wordDao;
    private Word currentWord;
    private List<String> allOptions = new ArrayList<>();

    private int sessionLimit = 10;
    private int sessionProgress = 0;
    private int lastWordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        wordDao = new WordDao(this);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        sessionLimit = prefs.getInt("session_count", 10);

        ImageButton btnBack = findViewById(R.id.btn_back_to_main);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        ImageView bgImage = findViewById(R.id.bg_image_learn);
        int bgId = prefs.getInt("background_id", R.drawable.bg1);
        bgImage.setImageResource(bgId);

        bindViews();

        int searchId = getIntent().getIntExtra("word_id", -1);
        if (searchId != -1) {
            currentWord = wordDao.getWordById(searchId);
            showWordDirectly();
        } else {
            loadNextWord();
        }
    }

    private void bindViews() {
        tvWord = findViewById(R.id.tv_word);
        tvPhonetic = findViewById(R.id.tv_phonetic);
        tvSentence = findViewById(R.id.tv_sentence);
        tvProgress = findViewById(R.id.tv_progress);
        tvProficiencyIndicator = findViewById(R.id.tv_proficiency_indicator);

        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);

        btnKnow = findViewById(R.id.btn_know);
        btnDontKnow = findViewById(R.id.btn_dont_know);

        level0Layout = findViewById(R.id.level0_layout);
        btnGroup = findViewById(R.id.level_btn_group);

        btnKnow.setOnClickListener(v -> updateProficiency(true));
        btnDontKnow.setOnClickListener(v -> updateProficiency(false));

    }

    private void loadNextWord() {
        if (sessionProgress >= sessionLimit) {
            Toast.makeText(this, "本组学习完成！太棒了！", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Word candidate = null;
        int threshold = 2;
        for (int level = 0; level < 3; level++) {
            if (level <= 1) {
                int countNext = wordDao.countWordsByProficiency(level + 1);
                if (countNext < threshold) {
                    candidate = wordDao.getWordByProficiency(level, lastWordId);
                }
            } else {
                candidate = wordDao.getWordByProficiency(level, lastWordId);
            }
            if (candidate != null) break;
        }
        if (candidate == null) {
            Toast.makeText(this, "已无待学习单词。", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        currentWord = candidate;
        lastWordId = currentWord.getId();

        resetViews();
        showProgressInfo();
        showLevelByProficiency(currentWord.getProficiency());
    }


    private void showProgressInfo() {
        tvWord.setText(currentWord.getWord());
        tvPhonetic.setText(currentWord.getPhonetic());
        tvSentence.setText(currentWord.getSentence());

        tvProgress.setText("学习进度：" + (sessionProgress) + " / " + sessionLimit);
        updateProficiencyIndicator(currentWord.getProficiency());
    }

    private void resetViews() {
        tvWord.setVisibility(View.GONE);
        tvPhonetic.setVisibility(View.GONE);
        tvSentence.setVisibility(View.GONE);
        level0Layout.setVisibility(View.GONE);
        btnGroup.setVisibility(View.GONE);
    }

    private void showLevelByProficiency(int level) {
        switch (level) {
            case 0: showLevel0(); break;
            case 1: showLevel1(); break;
            default: showLevelFinal(); break;
        }
    }

    private void showLevel0() {
        tvWord.setVisibility(View.VISIBLE);
        tvPhonetic.setVisibility(View.VISIBLE);
        level0Layout.setVisibility(View.VISIBLE);

        allOptions.clear();
        allOptions.add(currentWord.getDefinition());
        allOptions.addAll(currentWord.getChoiceList());
        Collections.shuffle(allOptions);

        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        for (int i = 0; i < 4; i++) {
            String text = allOptions.get(i);
            buttons[i].setText(text);
            buttons[i].setOnClickListener(v -> {
                if (text.equals(currentWord.getDefinition())) {
                    Toast.makeText(this, "正确！", Toast.LENGTH_SHORT).show();
                    updateProficiency(true);
                } else {
                    Toast.makeText(this, "错误", Toast.LENGTH_SHORT).show();
                    updateProficiency(false);
                }
            });
        }
    }

    private void showLevel1() {
        tvWord.setVisibility(View.VISIBLE);
        tvSentence.setVisibility(View.VISIBLE);
        btnGroup.setVisibility(View.VISIBLE);
    }

    private void showLevelFinal() {
        tvWord.setVisibility(View.VISIBLE);
        btnGroup.setVisibility(View.VISIBLE);
    }

    private void updateProficiency(boolean isKnown) {
        int level = currentWord.getProficiency();
        if (isKnown && level < 3) level++;
        else if (!isKnown) level = 0;

        currentWord.setProficiency(level);
        wordDao.updateProficiency(currentWord.getId(), level);

        if (level == 3) {
            sessionProgress++;
            wordDao.markAsLearned(currentWord.getId());
        }
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra("word_id", currentWord.getId());
        startActivity(intent);

        loadNextWord();
    }


    private void updateProficiencyIndicator(int level) {
        String[] symbols = {"❌", "⭐", "⭐⭐", "⭐⭐⭐"};
        if (level >= 0 && level <= 3) {
            tvProficiencyIndicator.setText("记忆进度：" + symbols[level]);
        }
    }

    private void showWordDirectly() {
        resetViews();
        showProgressInfo();
        showLevelByProficiency(currentWord.getProficiency());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
