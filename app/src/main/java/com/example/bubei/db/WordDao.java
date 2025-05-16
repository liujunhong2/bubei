package com.example.bubei.db;

import static com.example.bubei.db.WordDBHelper.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bubei.model.Word;

import java.util.ArrayList;
import java.util.List;

public class WordDao {
    private final WordDBHelper dbHelper;

    public WordDao(Context context) {
        dbHelper = new WordDBHelper(context);
    }

    /** 通用：从 Cursor 构造 Word 对象 */
    private Word extractWord(Cursor c) {
        Word w = new Word();
        w.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        w.setWord(c.getString(c.getColumnIndexOrThrow("word")));
        w.setPhonetic(c.getString(c.getColumnIndexOrThrow("phonetic")));
        w.setDefinition(c.getString(c.getColumnIndexOrThrow("definition")));
        w.setChoices(c.getString(c.getColumnIndexOrThrow("choices")));
        w.setSentence(c.getString(c.getColumnIndexOrThrow("sentence")));
        w.setProficiency(c.getInt(c.getColumnIndexOrThrow("proficiency")));
        w.setIsLearned(c.getInt(c.getColumnIndexOrThrow("is_learned")));
        w.setLastReviewTime(c.getLong(c.getColumnIndexOrThrow("last_review_time")));
        w.setReviewCount(c.getInt(c.getColumnIndexOrThrow("review_count")));
        return w;
    }

    /** 0. 插入新词 */
    public void insertWord(Word word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("word", word.getWord());
        v.put("phonetic", word.getPhonetic());
        v.put("definition", word.getDefinition());
        v.put("choices", word.getChoices());
        v.put("sentence", word.getSentence());
        v.put("proficiency", word.getProficiency());
        v.put("is_learned", word.getIsLearned());
        v.put("last_review_time", word.getLastReviewTime());
        v.put("review_count", word.getReviewCount());
        db.insert(TABLE_NAME, null, v);
        db.close();
    }

    /** 1. 按熟练度查询下一个待学单词（初学阶段 is_learned=0） */
    public Word getNextWord() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME, null,
                "is_learned = 0",
                null, null, null,
                "proficiency ASC", "1"
        );
        Word w = c.moveToFirst() ? extractWord(c) : null;
        c.close(); db.close();
        return w;
    }

    /** 2. 更新熟练度 */
    public void updateProficiency(int id, int newLevel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("proficiency", newLevel);
        db.update(TABLE_NAME, v, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /** 3. 标记为已学，进入复习队列 */
    public void markAsLearned(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("is_learned", 1);
        v.put("last_review_time", System.currentTimeMillis());
        v.put("review_count", 0);
        db.update(TABLE_NAME, v, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /** 4. 模糊搜索：word LIKE %keyword% */
    public List<Word> searchWords(String keyword) {
        List<Word> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME, null,
                "word LIKE ?",
                new String[]{"%" + keyword + "%"},
                null, null,
                "word ASC"
        );
        while (c.moveToNext()) {
            list.add(extractWord(c));
        }
        c.close(); db.close();
        return list;
    }

    /** 5. 根据 ID 查单词 */
    public Word getWordById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME, null,
                "id=?", new String[]{String.valueOf(id)},
                null, null, null
        );
        Word w = c.moveToFirst() ? extractWord(c) : null;
        c.close(); db.close();
        return w;
    }

    /** 6. 获取需要复习的单词：is_learned=1 且符合艾宾浩斯时间 */
    public List<Word> getWordsForReview() {
        List<Word> rv = new ArrayList<>();
        long now = System.currentTimeMillis();
        long[] days = {0,1,2,4,7};
        long dayMs = 86_400_000L;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME, null,
                "is_learned = 1", null, null, null, null
        );
        while (c.moveToNext()) {
            int cnt = c.getInt(c.getColumnIndexOrThrow("review_count"));
            long last = c.getLong(c.getColumnIndexOrThrow("last_review_time"));
            if (cnt < days.length && now - last >= days[cnt]*dayMs) {
                rv.add(extractWord(c));
            }
        }
        c.close(); db.close();
        return rv;
    }

    /** 7. 更新复习状态 */
    public void updateReviewState(Word w) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("review_count", w.getReviewCount());
        v.put("last_review_time", w.getLastReviewTime());
        db.update(TABLE_NAME, v, "id=?", new String[]{String.valueOf(w.getId())});
        db.close();
    }

    /** 统计指定熟练度的单词个数 */
    /** 统计指定熟练度的未学单词个数 */
    public int countWordsByProficiency(int level) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{"COUNT(*) AS cnt"},
                "proficiency = ? AND is_learned = 0", // 添加 is_learned = 0 条件
                new String[]{String.valueOf(level)},
                null, null, null
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow("cnt"));
        }
        cursor.close();
        db.close();
        return count;
    }

    /** 获取指定熟练度的未学单词 */
    public List<Word> getWordsByProficiency(int level) {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME, null,
                "proficiency = ? AND is_learned = 0",
                new String[]{String.valueOf(level)},
                null, null, "RANDOM()"
        );
        while (c.moveToNext()) {
            words.add(extractWord(c));
        }
        c.close();
        db.close();
        return words;
    }

    /** 获取指定熟练度和排除 ID 的单词 */
    public Word getWordByProficiency(int level, int excludeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME, null,
                "proficiency = ? AND is_learned = 0 AND id != ?",
                new String[]{String.valueOf(level), String.valueOf(excludeId)},
                null, null, "RANDOM()", "1"
        );
        Word w = c.moveToFirst() ? extractWord(c) : null;
        c.close(); db.close();
        return w;
    }

    /** 统计明天需要复习的单词数 */
    public int countReviewTomorrow() {
        long now = System.currentTimeMillis();
        long tomorrowMs = now + 86400000L;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME, null,
                "is_learned = 1", null, null, null, null
        );

        int count = 0;
        long[] days = {0,1,2,4,7};
        long dayMs = 86400000L;

        while (c.moveToNext()) {
            int rc = c.getInt(c.getColumnIndexOrThrow("review_count"));
            long last = c.getLong(c.getColumnIndexOrThrow("last_review_time"));
            if (rc < days.length) {
                long due = last + days[rc] * dayMs;
                if (due >= now && due <= tomorrowMs) count++;
            }
        }
        c.close();
        db.close();
        return count;
    }
}