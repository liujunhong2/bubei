package com.example.bubei.model;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Word implements Parcelable {
    private int id;
    private String word;
    private String phonetic;
    private String definition;
    private String choices;
    private String sentence;
    private int proficiency;
    private int isLearned;
    private long lastReviewTime;
    private int reviewCount;
    public Word() {}
    public Word(String word, String phonetic, String definition, String choices, String sentence, int proficiency) {
        this.word = word;
        this.phonetic = phonetic;
        this.definition = definition;
        this.choices = choices;
        this.sentence = sentence;
        this.proficiency = proficiency;
        this.isLearned = 0;
        this.lastReviewTime = 0;
        this.reviewCount = 0;
    }
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
    public List<String> getChoiceList() {
        if (choices == null || choices.isEmpty()) return new ArrayList<>();
        return Arrays.asList(choices.split(";"));
    }
    public String getSentence() { return sentence; }
    public void setSentence(String sentence) { this.sentence = sentence; }
    public int getProficiency() { return proficiency; }
    public void setProficiency(int proficiency) { this.proficiency = proficiency; }
    public int getIsLearned() { return isLearned; }
    public void setIsLearned(int isLearned) { this.isLearned = isLearned; }
    public long getLastReviewTime() { return lastReviewTime; }
    public void setLastReviewTime(long lastReviewTime) { this.lastReviewTime = lastReviewTime; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    protected Word(Parcel in) {
        id = in.readInt();
        word = in.readString();
        phonetic = in.readString();
        definition = in.readString();
        choices = in.readString();
        sentence = in.readString();
        proficiency = in.readInt();
        isLearned = in.readInt();
        lastReviewTime = in.readLong();
        reviewCount = in.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(word);
        dest.writeString(phonetic);
        dest.writeString(definition);
        dest.writeString(choices);
        dest.writeString(sentence);
        dest.writeInt(proficiency);
        dest.writeInt(isLearned);
        dest.writeLong(lastReviewTime);
        dest.writeInt(reviewCount);
    }
    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }
        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}