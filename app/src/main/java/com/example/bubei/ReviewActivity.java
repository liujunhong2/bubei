package com.example.bubei;

import android.content.SharedPreferences;
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

    private TextView tvWord, tvSentence, tvProgress, tvPhonetic, tvDefinition;
    private Button btnKnow, btnDont, btnNext;
    private ImageButton btnBack;
    private List<Word> reviewList;
    private int index = 0;
    private int sessionProgress = 0;
    private boolean justShowed = false;

    private WordDao dao;
    private int sessionMax = 5; // 默认值与 SettingsActivity 默认值一致

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        dao = new WordDao(this);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        sessionMax = prefs.getInt("review_session_limit", 5); // 使用 review_session_limit

        ImageView bg = findViewById(R.id.bg_image_review);
        int bgId = prefs.getInt("background_id", R.drawable.bg1);
        bg.setImageResource(bgId);

        tvWord       = findViewById(R.id.review_word);
        tvSentence   = findViewById(R.id.review_sentence);
        tvProgress   = findViewById(R.id.review_progress);
        tvPhonetic   = findViewById(R.id.review_phonetic);
        tvDefinition = findViewById(R.id.review_definition);
        btnKnow      = findViewById(R.id.btn_review_know);
        btnDont      = findViewById(R.id.btn_review_dont);
        btnNext      = findViewById(R.id.btn_review_next);
        btnBack      = findViewById(R.id.btn_back_review);

        btnBack.setOnClickListener(v -> finish());

        reviewList = dao.getWordsForReview();

        if (reviewList.isEmpty()) {
            int tomorrow = dao.countReviewTomorrow();
            Toast.makeText(this, "今日无复习单词，明天预计将有 " + tomorrow + " 个", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnKnow.setOnClickListener(v -> showDetail(true));
        btnDont.setOnClickListener(v -> showDetail(false));
        btnNext.setOnClickListener(v -> {
            index++;
            sessionProgress++;
            if (sessionProgress >= sessionMax || index >= reviewList.size()) {
                Toast.makeText(this, "本组复习完成！", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                showCurrent();
            }
        });

        showCurrent();
    }

    private void showCurrent() {
        if (index >= reviewList.size()) {
            Toast.makeText(this, "复习完成！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Word w = reviewList.get(index);
        tvWord.setText(w.getWord());
        tvSentence.setText(w.getSentence().replace(w.getWord(), "_______"));
        tvPhonetic.setText("");
        tvDefinition.setText("");

        tvProgress.setText("复习进度：" + (sessionProgress + 1) + " / " + sessionMax);

        btnKnow.setVisibility(View.VISIBLE);
        btnDont.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
        justShowed = false;
    }

    private void showDetail(boolean known) {
        if (justShowed) return;

        Word w = reviewList.get(index);
        w.setReviewCount(known ? w.getReviewCount() + 1 : 0);
        w.setLastReviewTime(System.currentTimeMillis());
        dao.updateReviewState(w);

        tvWord.setText(w.getWord());
        tvSentence.setText(w.getSentence());
        tvPhonetic.setText(w.getPhonetic());
        tvDefinition.setText(w.getDefinition());

        btnKnow.setVisibility(View.GONE);
        btnDont.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        justShowed = true;
    }
}