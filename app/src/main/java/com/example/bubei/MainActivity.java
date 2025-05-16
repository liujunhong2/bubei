package com.example.bubei;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bubei.db.WordDao;
import com.example.bubei.model.Word;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private int[] bgImages = {
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        insertSampleWords();
        initBackground();
        initButtons();
    }
    private void insertSampleWords() {
        WordDao dao = new WordDao(this);
        String[][] data = {
                {"abandon", "əˈbændən", "放弃", "离开;遗弃;取消", "He had to abandon the car in the snow."},
                {"benefit", "ˈbenɪfɪt", "好处", "优势;福利;利益", "He couldn't see the benefit of arguing anymore."},
                {"capture", "ˈkæptʃə", "捕获", "获取;拍摄;抓住", "The soldiers captured the enemy."},
                {"demand", "dɪˈmɑːnd", "需求", "请求;命令;要求", "There is a high demand for new products."},
                {"effort", "ˈefət", "努力", "尝试;艰难;辛勤", "He made a great effort to finish the work."},
                {"frequent", "ˈfriːkwənt", "频繁的", "常见的;重复的;多次的", "He is a frequent visitor to the library."},
                {"gather", "ˈɡæðə", "收集", "聚集;召集;采集", "The students gathered in the hall."},
                {"highlight", "ˈhaɪlaɪt", "强调", "突出;点亮;高亮", "The teacher highlighted key points."},
                {"ignore", "ɪɡˈnɔː", "忽略", "不理会;避开;回避", "You can't ignore the facts."},
                {"justice", "ˈdʒʌstɪs", "正义", "公平;司法;审判", "They want justice for the victims."},
                {"keen", "kiːn", "渴望的", "敏锐的;热衷的;锋利的", "He's very keen on football."},
                {"limit", "ˈlɪmɪt", "限制", "边界;范围;极限", "You need to limit your sugar intake."},
                {"major", "ˈmeɪdʒə", "主要的", "重要的;专业;少校", "Pollution is a major problem."},
                {"navigate", "ˈnævɪɡeɪt", "导航", "操控;驾驶;引导", "He navigated through the crowd."},
                {"observe", "əbˈzɜːv", "观察", "注意;遵守;评论", "She observed the stars through a telescope."},
                {"participate", "pɑːˈtɪsɪpeɪt", "参与", "参加;加入;出席", "Everyone is encouraged to participate."},
                {"question", "ˈkwestʃən", "问题", "疑问;询问;审问", "Can I ask a question?"},
                {"rescue", "ˈreskjuː", "营救", "拯救;救助;救援", "They rescued the trapped miner."},
                {"support", "səˈpɔːt", "支持", "支撑;帮助;扶持", "Thank you for your support."},
                {"target", "ˈtɑːɡɪt", "目标", "对象;靶子;瞄准", "The company hit its sales target."},
                {"urge", "ɜːdʒ", "敦促", "推动;强烈希望;力劝", "He urged them to work faster."},
                {"value", "ˈvæljuː", "价值", "重要性;价格;评估", "She values honesty."},
                {"warn", "wɔːn", "警告", "提醒;告诫;通知", "I warned him not to be late."},
                {"yield", "jiːld", "产出", "产生;屈服;让步", "This crop yields twice as much."},
                {"zone", "zəʊn", "区域", "地带;范围;分区", "This is a school zone."},
                {"adapt", "əˈdæpt", "适应", "改编;调整;改造", "You need to adapt to new situations."},
                {"balance", "ˈbæləns", "平衡", "均衡;结余;稳定", "She lost her balance on the ice."},
                {"commit", "kəˈmɪt", "承诺", "犯罪;投入;委托", "He committed a serious error."},
                {"defend", "dɪˈfend", "防守", "保护;辩护;防卫", "They defended their land."},
                {"explore", "ɪkˈsplɔː", "探索", "研究;冒险;挖掘", "The scientist explored the new theory."}
        };

        for (String[] row : data) {
            Word w = new Word(row[0], row[1], row[2], row[3], row[4], 0);
            dao.insertWord(w);
        }
    }

    private void initBackground() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean useRandom = prefs.getBoolean("bg_random", true);
        int bgId;
        if (useRandom) {
            bgId = bgImages[new Random().nextInt(bgImages.length)];
        } else {
            bgId = prefs.getInt("background_id", bgImages[0]);
        }
        ImageView bg = findViewById(R.id.bg_image);
        bg.setImageResource(bgId);
    }

    private void initButtons() {
        findViewById(R.id.btn_learn)
                .setOnClickListener(v -> startActivity(new Intent(this, LearnActivity.class)));
        findViewById(R.id.btn_review)
                .setOnClickListener(v -> startActivity(new Intent(this, ReviewActivity.class)));
        findViewById(R.id.btn_settings)
                .setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        ((ImageButton)findViewById(R.id.btn_search))
                .setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }
}
