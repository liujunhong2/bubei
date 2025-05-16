package com.example.bubei.model;

import java.util.Arrays;
import java.util.List;

public class Word {
    private int id;
    private String word;
    private String phonetic;
    private String definition; // 正确释义
    private String choices;    // 误导释义字符串，分号分隔
    private String sentence;
    private int proficiency;

    public Word() {}

    public Word(String word, String phonetic, String definition, String choices, String sentence, int proficiency) {
        this.word = word;
        this.phonetic = phonetic;
        this.definition = definition;
        this.choices = choices;
        this.sentence = sentence;
        this.proficiency = proficiency;
    }
    private int isLearned;         // 是否已完成初学，进入复习计划
    private long lastReviewTime;   // 上次复习时间
    private int reviewCount;       // 已复习次数

    public int getIsLearned() { return isLearned; }
    public void setIsLearned(int isLearned) { this.isLearned = isLearned; }

    public long getLastReviewTime() { return lastReviewTime; }
    public void setLastReviewTime(long lastReviewTime) { this.lastReviewTime = lastReviewTime; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    // Getter & Setter 方法
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getWord() { return word; }

    public void setWord(String word) { this.word = word; }

    public String getPhonetic() { return phonetic; }

    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }

    public String getDefinition() { return definition; }

    public void setDefinition(String definition) { this.definition = definition; }

    public String getChoices() { return choices; }

    public void setChoices(String choices) { this.choices = choices; }

    public String getSentence() { return sentence; }

    public void setSentence(String sentence) { this.sentence = sentence; }

    public int getProficiency() { return proficiency; }

    public void setProficiency(int proficiency) { this.proficiency = proficiency; }

    // 将choices字段转换为List，便于界面展示
    public List<String> getChoiceList() {
        return Arrays.asList(choices.split(";"));
    }
}
