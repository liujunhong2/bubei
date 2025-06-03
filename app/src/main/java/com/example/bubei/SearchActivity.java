package com.example.bubei;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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