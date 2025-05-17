package com.example.bubei.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class WordDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "word.db";
    public static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "Word";
    public WordDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    // 创建表时一次性声明所有字段
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT NOT NULL," +
                "phonetic TEXT," +
                "definition TEXT," +
                "choices TEXT," +
                "sentence TEXT," +
                "proficiency INTEGER DEFAULT 0," +
                "is_learned INTEGER DEFAULT 0," +
                "last_review_time INTEGER," +
                "review_count INTEGER DEFAULT 0" +
                ");";
        db.execSQL(createTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN is_learned INTEGER DEFAULT 0;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN last_review_time INTEGER;");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN review_count INTEGER DEFAULT 0;");
        }
    }
}