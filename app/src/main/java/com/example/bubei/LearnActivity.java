package com.example.bubei;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bubei.db.WordDao;
import com.example.bubei.model.Word;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class LearnActivity extends AppCompatActivity {
    private TextView tvWord, tvPhonetic, tvSentence;
    private TextView tvProgress, tvProficiencyIndicator;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnKnow, btnDontKnow;
    private View level0Layout, btnGroup;
    private WordDao wordDao;
    private Word currentWord;
    private List<Word> sessionWords = new ArrayList<>();
    private List<String> allOptions = new ArrayList<>();
    private int sessionLimit = 10;
    private int sessionProgress = 0;
    private int currentIndex = 0;
    private int currentProficiency = 0;
    private static final int THRESHOLD = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        wordDao = new WordDao(this);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        sessionLimit = prefs.getInt("session_count", 10);
        sessionProgress = prefs.getInt("learn_session_progress", 0);
        currentIndex = prefs.getInt("learn_current_index", 0);
        currentProficiency = prefs.getInt("learn_current_proficiency", 0);
        int currentWordId = prefs.getInt("learn_current_word_id", -1);
        if (currentWordId != -1) {
            currentWord = wordDao.getWordById(currentWordId);
        }
        if (sessionProgress >= sessionLimit || currentWord == null) {
            clearSessionState();
            loadSessionWords();
            sessionProgress = 0;
            currentIndex = 0;
            if (!sessionWords.isEmpty()) {
                currentWord = sessionWords.get(currentIndex);
            } else {
                Toast.makeText(this, "当前暂无可学习单词，请检查词库或重新导入。", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        ImageButton btnBack = findViewById(R.id.btn_back_to_main);
        btnBack.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.exit_learning_title))
                    .setMessage(getString(R.string.exit_learning_message))
                    .setPositiveButton(getString(R.string.exit), (dialog, which) -> {
                        saveSessionState();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });
        ImageView bgImage = findViewById(R.id.bg_image_learn);
        int bgId = prefs.getInt("background_id", R.drawable.bg1);
        bgImage.setImageResource(bgId);
        bindViews();
        showWordDirectly();
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveSessionState();
    }
    private void saveSessionState() {
        SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putInt("learn_session_progress", sessionProgress);
        editor.putInt("learn_current_index", currentIndex);
        editor.putInt("learn_current_proficiency", currentProficiency);
        if (currentWord != null) {
            editor.putInt("learn_current_word_id", currentWord.getId());
        }
        editor.apply();
    }
    private void clearSessionState() {
        SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.remove("learn_session_progress");
        editor.remove("learn_current_index");
        editor.remove("learn_current_proficiency");
        editor.remove("learn_current_word_id");
        editor.apply();
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
    private void loadSessionWords() {
        sessionWords.clear();
        Set<Integer> usedWordIds = new HashSet<>();
        int tryCount = 0;
        int maxTry = 100;
        while (sessionWords.size() < sessionLimit && tryCount < maxTry) {
            Word nextWord = getNextWordByProficiency(currentProficiency);
            tryCount++;
            if (nextWord == null) {
                currentProficiency = (currentProficiency + 1) % 3;
                continue;
            }
            if (!usedWordIds.contains(nextWord.getId())) {
                sessionWords.add(nextWord);
                usedWordIds.add(nextWord.getId());
                currentProficiency = (currentProficiency + 1) % 3;
            }
        }
    }
    private Word getNextWordByProficiency(int level) {
        return wordDao.getWordByProficiency(level, -1);
    }
    private void loadNextWord() {
        if (sessionProgress >= sessionLimit) {
            Toast.makeText(this, getString(R.string.session_complete), Toast.LENGTH_LONG).show();
            clearSessionState();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        if (currentIndex >= sessionWords.size()) {
            loadSessionWords();
            currentIndex = 0;
        }
        currentWord = sessionWords.get(currentIndex);
        resetViews();
        showProgressInfo();
        showLevelByProficiency(currentWord.getProficiency());
    }
    private void showProgressInfo() {
        tvWord.setText(currentWord.getWord());
        tvPhonetic.setText(currentWord.getPhonetic());
        tvSentence.setText(currentWord.getSentence());
        tvProgress.setText(getString(R.string.progress, sessionProgress, sessionLimit));
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
            case 2:
            default: showLevelFinal(); break;
        }
    }
    private void showLevel0() {
        tvWord.setVisibility(View.VISIBLE);
        tvPhonetic.setVisibility(View.VISIBLE);
        level0Layout.setVisibility(View.VISIBLE);
        allOptions.clear();
        allOptions.add(currentWord.getDefinition());
        List<String> choices = currentWord.getChoiceList();
        if (choices.size() < 3) {
            Toast.makeText(this, "此单词选项不足", Toast.LENGTH_SHORT).show();
            updateProficiency(false);
            return;
        }
        allOptions.addAll(choices.subList(0, Math.min(3, choices.size())));
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
        if (isKnown && level < 3) {
            level++;
        } else if (!isKnown) {
            level = 0;
            sessionWords.remove(currentIndex);
            Word newWord = wordDao.getWordByProficiency(0, currentWord.getId());
            if (newWord != null && !sessionWords.contains(newWord)) {
                sessionWords.add(currentIndex, newWord);
            }
        }
        currentWord.setProficiency(level);
        wordDao.updateProficiency(currentWord.getId(), level);
        if (level == 3) {
            wordDao.markAsLearned(currentWord.getId());
            sessionProgress++;
            sessionWords.remove(currentIndex);
        }
        if (isKnown) {
            currentIndex++;
        }
        saveSessionState();
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
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.exit_learning_title))
                    .setMessage(getString(R.string.exit_learning_message))
                    .setPositiveButton(getString(R.string.exit), (dialog, which) -> {
                        saveSessionState();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}