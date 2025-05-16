package com.example.bubei;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
//        initButtons();
        initButtonsWithCounts();
    }

    private void insertSampleWords() {
        WordDao dao = new WordDao(this);
        String[][] data = {
                {"abandon", "əˈbændən", "放弃;抛弃", "丰富的;减轻的;遗赠", "He had to abandon the car in the snow."},
                {"benefit", "ˈbenɪfɪt", "好处;利益", "在下方;仁慈的;适合的", "He couldn't see the benefit of arguing anymore."},
                {"capture", "ˈkæptʃə", "捕获;捕捉", "胶囊;俘虏;章节", "The soldiers captured the enemy."},
                {"demand", "dɪˈmɑːnd", "需求;要求", "贬低;依靠;降级", "There is a high demand for new products."},
                {"effort", "ˈefət", "努力;尝试", "影响;虚弱的;堡垒", "He made a great effort to finish the work."},
                {"frequent", "ˈfriːkwənt", "频繁的;经常的", "频率;片段;流畅的", "He is a frequent visitor to the library."},
                {"gather", "ˈɡæðə", "收集;聚集", "门闩;编织;分散", "The students gathered in the hall."},
                {"highlight", "ˈhaɪlaɪt", "强调;突出", "灯光;点燃;高地", "The teacher highlighted key points."},
                {"ignore", "ɪɡˈnɔː", "忽略;忽视", "无知的;崇拜;再来一次", "You can't ignore the facts."},
                {"justice", "ˈdʒʌstɪs", "正义;公平", "公正地;调整;玩笑", "They want justice for the victims."},
                {"keen", "kiːn", "渴望的;热衷的", "亲属;干净;女王", "He's very keen on football."},
                {"limit", "ˈlɪmɪt", "限制;限度", "清澈的;发射;点燃", "You need to limit your sugar intake."},
                {"major", "ˈmeɪdʒə", "主要的;重要的", "管理者;多数;次要的", "Pollution is a major problem."},
                {"navigate", "ˈnævɪɡeɪt", "导航;引导", "否定;刺激;航行者", "He navigated through the crowd."},
                {"observe", "əbˈzɜːv", "观察;注意", "相反的;吸收;保留", "She observed the stars through a telescope."},
                {"participate", "pɑːˈtɪsɪpeɪt", "参与;参加", "分词;预期;沉淀", "Everyone is encouraged to participate."},
                {"question", "ˈkwestʃən", "问题;疑问", "探索;请求;客人", "Can I ask a question?"},
                {"rescue", "ˈreskjuː", "营救;救援", "恢复;简历;发行", "They rescued the trapped miner."},
                {"support", "səˈpɔːt", "支持;支撑", "假设;港口;提议", "Thank you for your support."},
                {"target", "ˈtɑːɡɪt", "目标;对象", "切线;遗憾;标记", "The company hit its sales target."},
                {"urge", "ɜːdʒ", "敦促;催促", "激增;净化;出现", "He urged them to work faster."},
                {"value", "ˈvæljuː", "价值;重要性", "阀门;有效的;估价", "She values honesty."},
                {"warn", "wɔːn", "警告;告诫", "磨损;温暖;货车", "I warned him not to be late."},
                {"yield", "jiːld", "产出;生产", "田野;挥舞;喊叫", "This crop yields twice as much."},
                {"zone", "zəʊn", "区域;地带", "圆锥;石头;区域性", "This is a school zone."},
                {"adapt", "əˈdæpt", "适应;改编", "收养;熟练的;恰当的", "You need to adapt to new situations."},
                {"balance", "ˈbæləns", "平衡;均衡", "价态;跳跃;平衡术", "She lost her balance on the ice."},
                {"commit", "kəˈmɪt", "承诺;委托", "承认;发射;提交", "He committed a serious error."},
                {"defend", "dɪˈfend", "防守;保护", "依赖;冒犯;防御", "They defended their land."},
                {"explore", "ɪkˈsplɔː", "探索;研究", "恳求;爆炸;出口", "The scientist explored the new theory."},
                {"achieve", "əˈtʃiːv", "实现;达成", "存档;活跃的;实现者", "She worked hard to achieve her goals."},
                {"brief", "briːf", "简短的;简要的", "悲伤;信仰;简讯", "The meeting was brief but productive."},
                {"conduct", "kənˈdʌkt", "进行;引导", "管道;接触;冲突", "They conducted a survey."},
                {"declare", "dɪˈkleə", "声明;宣布", "法令;绝望;宣称者", "She declared her intentions."},
                {"emerge", "ɪˈmɜːdʒ", "出现;浮现", "合并;紧急情况;沉浸", "A new leader emerged."},
                {"fault", "fɔːlt", "错误;缺陷", "更改;拱顶;盐", "It’s not my fault."},
                {"generate", "ˈdʒenəreɪt", "产生;生成", "再生;将军;普遍的", "The project generated interest."},
                {"impact", "ˈɪmpækt", "影响;冲击", "紧实;传授;影响者", "The decision had a huge impact."},
                {"journey", "ˈdʒɜːni", "旅行;旅程", "锦标赛;日记;旅途", "The journey took three days."},
                {"maintain", "meɪnˈteɪn", "维持;保持", "包含;支持;维持者", "She maintained her composure."},
                {"option", "ˈɒpʃn", "选择;选项", "行动;部分;选择权", "You have several options."},
                {"proceed", "prəˈsiːd", "继续;前进", "在先;过程;继续者", "Let’s proceed with the plan."},
                {"reflect", "rɪˈflekt", "反映;思考", "偏转;拒绝;反射物", "The lake reflected the mountains."},
                {"secure", "sɪˈkjʊə", "安全的;确保", "肯定;秘密;安全", "They secured the building."},
                {"trend", "trend", "趋势;潮流", "倾向;趋势的;时尚", "This is a new fashion trend."},
                {"unique", "juːˈniːk", "独特的;唯一的", "联合;工会;统一", "Her style is unique."},
                {"vast", "vɑːst", "广阔的;巨大的", "花瓶;快速的;投掷", "The desert is vast."},
                {"weigh", "weɪ", "称重;衡量", "方式;重量;工资", "She weighed the options carefully."},
                {"exceed", "ɪkˈsiːd", "超过;超越", "同意;继续;种子", "The costs may exceed the budget."},
                {"yearn", "jɜːn", "渴望;向往", "纱线;学习;赚取", "She yearned for adventure."},
                {"zeal", "ziːl", "热情;热忱", "密封;交易;感觉", "He worked with great zeal."}
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
    private void initButtonsWithCounts() {
        WordDao wordDao = new WordDao(this);

        // 获取未学习的单词数 (Learn)
        int learnCount = wordDao.countWordsByProficiency(0); // 假设 proficiency=0 表示未学

        // 获取需要复习的单词数 (Review)
        int reviewCount = wordDao.getWordsForReview().size();

        // 设置 "Learn" 按钮文字
        Button btnLearn = findViewById(R.id.btn_learn);
        btnLearn.setText(getString(R.string.learn) + " " + learnCount);

        // 设置 "Review" 按钮文字
        Button btnReview = findViewById(R.id.btn_review);
        btnReview.setText(getString(R.string.review) + " " + reviewCount);
        btnLearn.setBackgroundColor(Color.parseColor("#A8D5BA"));
        btnReview.setBackgroundColor(Color.parseColor("#A8D5BA"));
        // 设置按钮点击事件
        btnLearn.setOnClickListener(v -> startActivity(new Intent(this, LearnActivity.class)));
        btnReview.setOnClickListener(v -> startActivity(new Intent(this, ReviewActivity.class)));
        findViewById(R.id.btn_settings)
                .setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        ((ImageButton) findViewById(R.id.btn_search))
                .setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }
}