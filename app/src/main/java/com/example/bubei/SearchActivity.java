package com.example.bubei;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.bubei.db.WordDao;
import com.example.bubei.model.Word;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etKeyword;
    private ImageButton btnBack, btnSearch;
    private ListView lvResults;

    private WordDao wordDao;
    private List<Word> results = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<String> displayList = new ArrayList<>();

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
        setContentView(R.layout.activity_search);
        wordDao = new WordDao(this);

        etKeyword = findViewById(R.id.et_keyword);
        btnSearch  = findViewById(R.id.btn_search_go);
        btnBack    = findViewById(R.id.btn_back_search);
        lvResults  = findViewById(R.id.lv_search_results);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayList);
        lvResults.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> doSearch());

        lvResults.setOnItemClickListener((parent, view, pos, id) -> {
            Word w = results.get(pos);
            Intent it = new Intent(SearchActivity.this, WordDetailActivity.class);
            it.putExtra("word_id", w.getId());
            startActivity(it);
        });
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        ImageView bg = findViewById(R.id.bg_search);
        int bgId = prefs.getInt("background_id", R.drawable.bg1);
        bg.setImageResource(bgId);
    }

    private void doSearch() {
        String kw = etKeyword.getText().toString().trim();
        results.clear();
        displayList.clear();
        if (!kw.isEmpty()) {
            results.addAll(wordDao.searchWords(kw));
            if (results.isEmpty()) {
                displayList.add("未找到相关单词");
            } else {
                // 使用 LinkedHashSet 去重，保留顺序
                LinkedHashSet<String> uniqueWords = new LinkedHashSet<>();
                for (Word w : results) {
                    String displayText = w.getWord() + "  —  " + w.getDefinition();
                    if (uniqueWords.add(displayText)) {
                        displayList.add(displayText);
                    }
                }
            }
        } else {
            displayList.add("请输入搜索关键词");
        }
        adapter.notifyDataSetChanged();
    }
}