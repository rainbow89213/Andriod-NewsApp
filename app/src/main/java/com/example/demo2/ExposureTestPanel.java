package com.example.demo2;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 曝光事件测试面板
 * 
 * 用于在APP内实时显示和测试曝光事件的准确性
 */
public class ExposureTestPanel extends LinearLayout {
    
    private static final String TAG = "ExposureTestPanel";
    
    // UI组件
    private TextView statsText;        // 统计信息
    private TextView logText;          // 日志文本
    private ScrollView logScrollView;  // 日志滚动视图
    private Button toggleButton;       // 展开/收起按钮
    private Button clearButton;        // 清除日志按钮
    private LinearLayout contentPanel; // 内容面板
    
    // 统计数据
    private int appearCount = 0;       // 露出次数
    private int halfVisibleCount = 0;  // 50%可见次数
    private int fullyVisibleCount = 0; // 完整露出次数
    private int disappearCount = 0;    // 消失次数
    
    // 状态
    private boolean isExpanded = true; // 是否展开
    
    // 时间格式化
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
    
    public ExposureTestPanel(Context context) {
        super(context);
        init();
    }
    
    /**
     * 初始化面板
     */
    private void init() {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#E8F5E9")); // 浅绿色背景
        setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
        
        // 设置布局参数
        LayoutParams params = new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(params);
        
        // 创建顶部栏（标题 + 按钮）
        createTopBar();
        
        // 创建内容面板
        createContentPanel();
    }
    
    /**
     * 创建顶部栏
     */
    private void createTopBar() {
        LinearLayout topBar = new LinearLayout(getContext());
        topBar.setOrientation(HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        
        // 标题
        TextView title = new TextView(getContext());
        title.setText("📊 曝光事件测试工具");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        title.setTextColor(Color.parseColor("#2E7D32"));
        title.setPadding(0, 0, dpToPx(8), 0);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f
        );
        topBar.addView(title, titleParams);
        
        // 清除按钮
        clearButton = new Button(getContext());
        clearButton.setText("清除");
        clearButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        clearButton.setBackgroundColor(Color.parseColor("#FF9800"));
        clearButton.setTextColor(Color.WHITE);
        clearButton.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        clearButton.setOnClickListener(v -> clearLogs());
        LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(32)
        );
        clearParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        topBar.addView(clearButton, clearParams);
        
        // 展开/收起按钮
        toggleButton = new Button(getContext());
        toggleButton.setText("收起");
        toggleButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        toggleButton.setBackgroundColor(Color.parseColor("#4CAF50"));
        toggleButton.setTextColor(Color.WHITE);
        toggleButton.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        toggleButton.setOnClickListener(v -> togglePanel());
        LinearLayout.LayoutParams toggleParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(32)
        );
        topBar.addView(toggleButton, toggleParams);
        
        addView(topBar);
    }
    
    /**
     * 创建内容面板
     */
    private void createContentPanel() {
        contentPanel = new LinearLayout(getContext());
        contentPanel.setOrientation(VERTICAL);
        
        // 统计信息
        statsText = new TextView(getContext());
        statsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        statsText.setTextColor(Color.parseColor("#1B5E20"));
        statsText.setPadding(0, dpToPx(8), 0, dpToPx(8));
        updateStats();
        contentPanel.addView(statsText);
        
        // 分割线
        View divider = new View(getContext());
        divider.setBackgroundColor(Color.parseColor("#81C784"));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1)
        );
        dividerParams.setMargins(0, dpToPx(4), 0, dpToPx(8));
        contentPanel.addView(divider, dividerParams);
        
        // 日志标题
        TextView logTitle = new TextView(getContext());
        logTitle.setText("📝 实时日志");
        logTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        logTitle.setTextColor(Color.parseColor("#2E7D32"));
        logTitle.setPadding(0, 0, 0, dpToPx(4));
        contentPanel.addView(logTitle);
        
        // 日志文本
        logText = new TextView(getContext());
        logText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        logText.setTextColor(Color.parseColor("#424242"));
        logText.setBackgroundColor(Color.WHITE);
        logText.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        logText.setText("等待曝光事件...\n");
        
        // 日志滚动视图
        logScrollView = new ScrollView(getContext());
        logScrollView.addView(logText);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(200)
        );
        contentPanel.addView(logScrollView, scrollParams);
        
        addView(contentPanel);
    }
    
    /**
     * 记录"卡片露出"事件
     */
    public void logAppear(int position, String title) {
        appearCount++;
        updateStats();
        String time = timeFormat.format(new Date());
        String log = String.format("📍 [%s] 卡片露出 - 位置:%d, 标题:%s\n", 
            time, position, truncate(title, 20));
        appendLog(log, "#4CAF50");
    }
    
    /**
     * 记录"卡片50%可见"事件
     */
    public void logHalfVisible(int position, String title, float percent) {
        halfVisibleCount++;
        updateStats();
        String time = timeFormat.format(new Date());
        String log = String.format("📊 [%s] 卡片50%%可见 - 位置:%d, 可见度:%.1f%%, 标题:%s\n", 
            time, position, percent * 100, truncate(title, 15));
        appendLog(log, "#FF9800");
    }
    
    /**
     * 记录"卡片完整露出"事件
     */
    public void logFullyVisible(int position, String title) {
        fullyVisibleCount++;
        updateStats();
        String time = timeFormat.format(new Date());
        String log = String.format("✅ [%s] 卡片完整露出 - 位置:%d, 标题:%s\n", 
            time, position, truncate(title, 20));
        appendLog(log, "#2196F3");
    }
    
    /**
     * 记录"卡片消失"事件
     */
    public void logDisappear(int position, String title) {
        disappearCount++;
        updateStats();
        String time = timeFormat.format(new Date());
        String log = String.format("👋 [%s] 卡片消失 - 位置:%d, 标题:%s\n", 
            time, position, truncate(title, 20));
        appendLog(log, "#9E9E9E");
    }
    
    /**
     * 添加日志
     */
    private void appendLog(String log, String colorHex) {
        logText.append(log);
        
        // 自动滚动到底部
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
        
        // 限制日志长度（保留最后100行）
        String currentLog = logText.getText().toString();
        String[] lines = currentLog.split("\n");
        if (lines.length > 100) {
            StringBuilder sb = new StringBuilder();
            for (int i = lines.length - 100; i < lines.length; i++) {
                sb.append(lines[i]).append("\n");
            }
            logText.setText(sb.toString());
        }
    }
    
    /**
     * 更新统计信息
     */
    private void updateStats() {
        String stats = String.format(
            "📊 统计: 露出:%d | 50%%可见:%d | 完整:%d | 消失:%d",
            appearCount, halfVisibleCount, fullyVisibleCount, disappearCount
        );
        if (statsText != null) {
            statsText.setText(stats);
        }
    }
    
    /**
     * 清除日志
     */
    private void clearLogs() {
        appearCount = 0;
        halfVisibleCount = 0;
        fullyVisibleCount = 0;
        disappearCount = 0;
        updateStats();
        logText.setText("日志已清除\n");
    }
    
    /**
     * 切换面板展开/收起
     */
    private void togglePanel() {
        isExpanded = !isExpanded;
        contentPanel.setVisibility(isExpanded ? VISIBLE : GONE);
        toggleButton.setText(isExpanded ? "收起" : "展开");
    }
    
    /**
     * 截断字符串
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
    
    /**
     * dp转px
     */
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, 
            getContext().getResources().getDisplayMetrics()
        );
    }
}
