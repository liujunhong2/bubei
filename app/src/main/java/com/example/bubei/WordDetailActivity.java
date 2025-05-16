package com.example.bubei;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bubei.db.WordDao;
import com.example.bubei.model.Word;

public class WordDetailActivity extends AppCompatActivity {

    private TextView tvWord, tvPhonetic, tvDefinition, tvSentence;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        tvWord = findViewById(R.id.tv_detail_word);
        tvPhonetic = findViewById(R.id.tv_detail_phonetic);
        tvDefinition = findViewById(R.id.tv_detail_definition);
        tvSentence = findViewById(R.id.tv_detail_sentence);
        btnBack = findViewById(R.id.btn_back_word_detail);

        int wordId = getIntent().getIntExtra("word_id", -1);
        if (wordId != -1) {
            WordDao dao = new WordDao(this);
            Word w = dao.getWordById(wordId);
            if (w != null) {
                tvWord.setText(w.getWord());
                tvPhonetic.setText(w.getPhonetic());
                tvDefinition.setText(w.getDefinition());
                tvSentence.setText(w.getSentence());
            }
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
