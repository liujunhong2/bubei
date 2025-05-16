package com.example.bubei;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bubei.db.WordDao;
import com.example.bubei.model.Word;

import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private TextView tvWord, tvSentence;
    private Button btnKnow, btnDont;
    private List<Word> reviewList;
    private int currentIndex = 0;
    private WordDao wordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        wordDao = new WordDao(this);

        ImageView bg = findViewById(R.id.bg_image_review);
        int bgId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("background_id", R.drawable.bg1);
        bg.setImageResource(bgId);

        tvWord = findViewById(R.id.review_word);
        tvSentence = findViewById(R.id.review_sentence);
        btnKnow = findViewById(R.id.btn_review_know);
        btnDont = findViewById(R.id.btn_review_dont);

        reviewList = wordDao.getWordsForReview();

        if (reviewList.isEmpty()) {
            Toast.makeText(this, "暂无需要复习的单词", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        showWord();

        btnKnow.setOnClickListener(v -> handleAnswer(true));
        btnDont.setOnClickListener(v -> handleAnswer(false));
        ImageButton btnBack = findViewById(R.id.btn_back_review);
        btnBack.setOnClickListener(v -> {
            finish();  // 直接返回上一页（MainActivity）
        });
    }

    private void showWord() {
        if (currentIndex >= reviewList.size()) {
            Toast.makeText(this, "复习完成", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Word word = reviewList.get(currentIndex);
        tvWord.setText(word.getWord());
        tvSentence.setText(word.getSentence().replace(word.getWord(), "_______"));
    }

    private void handleAnswer(boolean isKnown) {
        Word word = reviewList.get(currentIndex);

        if (isKnown) {
            word.setReviewCount(word.getReviewCount() + 1);
            word.setLastReviewTime(System.currentTimeMillis());
        } else {
            word.setReviewCount(0); // 重置复习次数
            word.setLastReviewTime(System.currentTimeMillis());
        }

        wordDao.updateReviewState(word);  // 自定义方法，更新字段
        currentIndex++;
        showWord();
    }
}
