package com.example.bubei;

import static com.example.bubei.db.WordDBHelper.TABLE_NAME;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnKnow, btnDontKnow;
    private View level0Layout, btnGroup;

    private WordDao wordDao;
    private Word currentWord;
    private List<String> allOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        ImageButton btnBack = findViewById(R.id.btn_back_to_main);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        ImageView bgImage = findViewById(R.id.bg_image_learn);
        int bgId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("background_id", R.drawable.bg1);
        bgImage.setImageResource(bgId);

        wordDao = new WordDao(this);
        bindViews();
        loadNextWord();
    }

    private void bindViews() {
        tvWord = findViewById(R.id.tv_word);
        tvPhonetic = findViewById(R.id.tv_phonetic);
        tvSentence = findViewById(R.id.tv_sentence);

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

    private int lastWordId = -1; // 避免同词重复
    private void loadNextWord() {
        Word candidate = null;

        // 优先从熟练度 0 的库里拿一个单词
        // 然后如果 0→1 阶段的数量 < 阈值，则一直留在 0
        // 再尝试 1 阶段：1→2 阶段也检查阈值
        // 最后阶段（2 和 3）不检查阈值，直接拿
        int threshold = 2; // 0→1 和 1→2 需要达到的最小数量
        for (int level = 0; level < 3; level++) {
            if (level <= 1) {
                // 只在级别 0 和 1 时，检查“下一阶段”数量是否已满
                int countNext = wordDao.countWordsByProficiency(level + 1);
                if (countNext < threshold) {
                    candidate = wordDao.getWordByProficiency(level, lastWordId);
                }
            } else {
                // 级别 2 和 3 时，直接拿一个
                candidate = wordDao.getWordByProficiency(level, lastWordId);
            }
            if (candidate != null) break;
        }

        if (candidate == null) {
            Toast.makeText(this, "今日学习已完成！", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        currentWord = candidate;
        lastWordId = currentWord.getId();

        // 显示逻辑不变
        int level = currentWord.getProficiency();
        resetViews();
        tvPhonetic.setText(currentWord.getPhonetic());
        tvSentence.setText(currentWord.getSentence());
        switch (level) {
            case 0: showLevel0(); break;
            case 1: showLevel1(); break;
            default: showLevelFinal(); break;
        }
    }

    private void resetViews() {
        tvWord.setVisibility(View.GONE);
        tvPhonetic.setVisibility(View.GONE);
        tvSentence.setVisibility(View.GONE);
        level0Layout.setVisibility(View.GONE);
        btnGroup.setVisibility(View.GONE);
    }

    private void showLevel0() {
        tvWord.setText(currentWord.getWord());
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
                    Toast.makeText(this, "选择正确！", Toast.LENGTH_SHORT).show();
                    updateProficiency(true);
                } else {
                    Toast.makeText(this, "答错了", Toast.LENGTH_SHORT).show();
                    updateProficiency(false);
                }
            });
        }
    }

    private void showLevel1() {
        tvWord.setText(currentWord.getWord());
        tvWord.setVisibility(View.VISIBLE);
        tvSentence.setVisibility(View.VISIBLE);
        btnGroup.setVisibility(View.VISIBLE);
    }

    private void showLevelFinal() {
        tvWord.setText(currentWord.getWord());
        tvWord.setVisibility(View.VISIBLE);
        btnGroup.setVisibility(View.VISIBLE);
    }


    private void updateProficiency(boolean isKnown) {
        int level = currentWord.getProficiency();
        Log.d("LearnActivity", "当前单词熟练度1：" + level + "，id=" + currentWord.getId());
        if (isKnown && level < 3) {
            level++;
        } else if (!isKnown && level > 0) {
            level--;
        }
        Log.d("LearnActivity", "当前单词熟练度2：" + level + "，id=" + currentWord.getId());

        currentWord.setProficiency(level);
        wordDao.updateProficiency(currentWord.getId(), level);

        if (level == 3) {
            wordDao.markAsLearned(currentWord.getId());  // 进入复习队列
        }

        loadNextWord();
    }



    // 顶部导航返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class)); // ← 点击左上角返回主页面
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
