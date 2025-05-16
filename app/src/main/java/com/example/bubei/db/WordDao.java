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
    private WordDBHelper dbHelper;

    public WordDao(Context context) {
        dbHelper = new WordDBHelper(context);
    }
    public int countWordsByProficiency(int level) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"COUNT(*) AS cnt"},
                "proficiency = ?", new String[]{String.valueOf(level)}, null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow("cnt"));
        }
        cursor.close();
        db.close();
        return count;
    }
    public Word getWordByProficiency(int level, int excludeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null,
                "proficiency = ? AND id != ? AND proficiency < 3", // ← 添加限制条件
                new String[]{String.valueOf(level), String.valueOf(excludeId)},
                null, null, "RANDOM()", "1");

        Word word = null;
        if (cursor.moveToFirst()) {
            word = extractWord(cursor);
        }
        cursor.close();
        db.close();
        return word;
    }


    private Word extractWord(Cursor cursor) {
        Word word = new Word();
        word.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        word.setWord(cursor.getString(cursor.getColumnIndexOrThrow("word")));
        word.setPhonetic(cursor.getString(cursor.getColumnIndexOrThrow("phonetic")));
        word.setDefinition(cursor.getString(cursor.getColumnIndexOrThrow("definition")));
        word.setChoices(cursor.getString(cursor.getColumnIndexOrThrow("choices")));
        word.setSentence(cursor.getString(cursor.getColumnIndexOrThrow("sentence")));
        word.setProficiency(cursor.getInt(cursor.getColumnIndexOrThrow("proficiency")));
        word.setIsLearned(cursor.getInt(cursor.getColumnIndexOrThrow("is_learned")));
        word.setLastReviewTime(cursor.getLong(cursor.getColumnIndexOrThrow("last_review_time")));
        word.setReviewCount(cursor.getInt(cursor.getColumnIndexOrThrow("review_count")));
        return word;
    }


    public void insertWord(Word word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_learned", word.getIsLearned());
        values.put("last_review_time", word.getLastReviewTime());
        values.put("review_count", word.getReviewCount());

        values.put("word", word.getWord());
        values.put("phonetic", word.getPhonetic());
        values.put("definition", word.getDefinition());
        values.put("choices", word.getChoices());
        values.put("sentence", word.getSentence());
        values.put("proficiency", word.getProficiency());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Word> getAllWords() {

        List<Word> wordList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Word word = new Word();
            word.setIsLearned(cursor.getInt(cursor.getColumnIndexOrThrow("is_learned")));
            word.setLastReviewTime(cursor.getLong(cursor.getColumnIndexOrThrow("last_review_time")));
            word.setReviewCount(cursor.getInt(cursor.getColumnIndexOrThrow("review_count")));

            word.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            word.setWord(cursor.getString(cursor.getColumnIndexOrThrow("word")));
            word.setPhonetic(cursor.getString(cursor.getColumnIndexOrThrow("phonetic")));
            word.setDefinition(cursor.getString(cursor.getColumnIndexOrThrow("definition")));
            word.setChoices(cursor.getString(cursor.getColumnIndexOrThrow("choices")));
            word.setSentence(cursor.getString(cursor.getColumnIndexOrThrow("sentence")));
            word.setProficiency(cursor.getInt(cursor.getColumnIndexOrThrow("proficiency")));
            wordList.add(word);
        }
        cursor.close();
        db.close();
        return wordList;
    }

    public Word getNextWord() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "is_learned = 0", null, null, null, "proficiency ASC", "1");
//        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "proficiency ASC", "1");
        Word word = null;
        if (cursor.moveToFirst()) {
            word = new Word();
            word.setIsLearned(cursor.getInt(cursor.getColumnIndexOrThrow("is_learned")));
            word.setLastReviewTime(cursor.getLong(cursor.getColumnIndexOrThrow("last_review_time")));
            word.setReviewCount(cursor.getInt(cursor.getColumnIndexOrThrow("review_count")));

            word.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            word.setWord(cursor.getString(cursor.getColumnIndexOrThrow("word")));
            word.setPhonetic(cursor.getString(cursor.getColumnIndexOrThrow("phonetic")));
            word.setDefinition(cursor.getString(cursor.getColumnIndexOrThrow("definition")));
            word.setChoices(cursor.getString(cursor.getColumnIndexOrThrow("choices")));
            word.setSentence(cursor.getString(cursor.getColumnIndexOrThrow("sentence")));
            word.setProficiency(cursor.getInt(cursor.getColumnIndexOrThrow("proficiency")));
        }
        cursor.close();
        db.close();
        return word;
    }

    public void updateProficiency(int wordId, int newLevel) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("proficiency", newLevel);
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(wordId)});
        db.close();
    }

    public void markAsLearned(int wordId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_learned", 1);
        values.put("last_review_time", System.currentTimeMillis());
        values.put("review_count", 0);
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(wordId)});
        db.close();
    }
    public List<Word> getWordsForReview() {
        List<Word> reviewList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long now = System.currentTimeMillis();
        long[] intervals = {0, 1, 2, 4, 7};  // 单位：天
        long millisInDay = 86400000L;

        Cursor cursor = db.query(TABLE_NAME, null,
                "is_learned != 0", null, null, null, null);

        while (cursor.moveToNext()) {
            int reviewCount = cursor.getInt(cursor.getColumnIndexOrThrow("review_count"));
            long lastTime = cursor.getLong(cursor.getColumnIndexOrThrow("last_review_time"));

            if (reviewCount < intervals.length) {
                long requiredInterval = intervals[reviewCount] * millisInDay;
                if (now - lastTime >= requiredInterval) {
                    Word word = new Word();
                    word.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    word.setWord(cursor.getString(cursor.getColumnIndexOrThrow("word")));
                    word.setPhonetic(cursor.getString(cursor.getColumnIndexOrThrow("phonetic")));
                    word.setDefinition(cursor.getString(cursor.getColumnIndexOrThrow("definition")));
                    word.setChoices(cursor.getString(cursor.getColumnIndexOrThrow("choices")));
                    word.setSentence(cursor.getString(cursor.getColumnIndexOrThrow("sentence")));
                    word.setProficiency(cursor.getInt(cursor.getColumnIndexOrThrow("proficiency")));
                    word.setIsLearned(1);
                    word.setLastReviewTime(lastTime);
                    word.setReviewCount(reviewCount);

                    reviewList.add(word);
                }
            }
        }


        cursor.close();
        db.close();
        return reviewList;
    }
    public void updateReviewState(Word word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_review_time", word.getLastReviewTime());
        values.put("review_count", word.getReviewCount());
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(word.getId())});
        db.close();
    }

}
