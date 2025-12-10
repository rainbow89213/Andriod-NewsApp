package com.example.demo2.exposure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 曝光事件测试工具
 * 提供一个可拖动的悬浮窗，实时显示曝光事件
 */
public class ExposureTestTool implements CardExposureListener {
    
    private static final String TAG = "ExposureTestTool";
    private static final int MAX_EVENTS = 100;  // 最多保留100条事件
    
    private Context context;
    private View floatingView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    
    private TextView eventLogTextView;
    private TextView statsTextView;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private Button toggleButton;
    
    private boolean isExpanded = true;
    private boolean isShowing = false;
    
    // 事件统计
    private int appearCount = 0;
    private int halfVisibleCount = 0;
    private int fullyVisibleCount = 0;
    private int disappearCount = 0;
    
    // 事件日志
    private final List<String> eventLogs = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
    
    // 单例
    private static ExposureTestTool instance;
    
    public static ExposureTestTool getInstance(Context context) {
        if (instance == null) {
            instance = new ExposureTestTool(context.getApplicationContext());
        }
        return instance;
    }
    
    private ExposureTestTool(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
    
    /**
     * 显示测试工具
     */
    @SuppressLint("ClickableViewAccessibility")
    public void show() {
        if (isShowing) {
            return;
        }
        
        // 创建悬浮窗视图
        createFloatingView();
        
        // 设置窗口参数
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        
        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 200;
        
        try {
            windowManager.addView(floatingView, params);
            isShowing = true;
            Log.d(TAG, "测试工具已显示");
        } catch (Exception e) {
            Log.e(TAG, "显示测试工具失败: " + e.getMessage());
        }
    }
    
    /**
     * 隐藏测试工具
     */
    public void hide() {
        if (!isShowing || floatingView == null) {
            return;
        }
        
        try {
            windowManager.removeView(floatingView);
            isShowing = false;
            floatingView = null;
            Log.d(TAG, "测试工具已隐藏");
        } catch (Exception e) {
            Log.e(TAG, "隐藏测试工具失败: " + e.getMessage());
        }
    }
    
    /**
     * 切换显示/隐藏
     */
    public void toggle() {
        if (isShowing) {
            hide();
        } else {
            show();
        }
    }
    
    /**
     * 创建悬浮窗视图
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createFloatingView() {
        // 主容器
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#E0000000"));
        mainLayout.setPadding(16, 16, 16, 16);
        
        // 标题栏
        LinearLayout titleBar = new LinearLayout(context);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(context);
        titleText.setText("📊 曝光事件测试");
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(14);
        titleText.setPadding(0, 0, 16, 0);
        
        // 展开/收起按钮
        toggleButton = new Button(context);
        toggleButton.setText("收起");
        toggleButton.setTextSize(10);
        toggleButton.setPadding(8, 4, 8, 4);
        toggleButton.setOnClickListener(v -> toggleExpand());
        
        // 清除按钮
        Button clearButton = new Button(context);
        clearButton.setText("清除");
        clearButton.setTextSize(10);
        clearButton.setPadding(8, 4, 8, 4);
        clearButton.setOnClickListener(v -> clearLogs());
        
        // 关闭按钮
        Button closeButton = new Button(context);
        closeButton.setText("X");
        closeButton.setTextSize(10);
        closeButton.setPadding(8, 4, 8, 4);
        closeButton.setOnClickListener(v -> hide());
        
        titleBar.addView(titleText);
        titleBar.addView(toggleButton);
        titleBar.addView(clearButton);
        titleBar.addView(closeButton);
        
        // 统计信息
        statsTextView = new TextView(context);
        statsTextView.setTextColor(Color.parseColor("#00FF00"));
        statsTextView.setTextSize(11);
        statsTextView.setPadding(0, 8, 0, 8);
        updateStats();
        
        // 内容区域（可收起）
        contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        
        // 事件日志滚动视图
        scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
            dpToPx(280),
            dpToPx(200)
        ));
        
        eventLogTextView = new TextView(context);
        eventLogTextView.setTextColor(Color.WHITE);
        eventLogTextView.setTextSize(10);
        eventLogTextView.setText("等待事件...\n");
        
        scrollView.addView(eventLogTextView);
        contentLayout.addView(scrollView);
        
        // 组装视图
        mainLayout.addView(titleBar);
        mainLayout.addView(statsTextView);
        mainLayout.addView(contentLayout);
        
        floatingView = mainLayout;
        
        // 添加拖动功能
        setupDragListener(titleBar);
    }
    
    /**
     * 设置拖动监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setupDragListener(View dragHandle) {
        final float[] initialX = new float[1];
        final float[] initialY = new float[1];
        final float[] initialTouchX = new float[1];
        final float[] initialTouchY = new float[1];
        
        dragHandle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX[0] = params.x;
                    initialY[0] = params.y;
                    initialTouchX[0] = event.getRawX();
                    initialTouchY[0] = event.getRawY();
                    return true;
                    
                case MotionEvent.ACTION_MOVE:
                    params.x = (int) (initialX[0] + (event.getRawX() - initialTouchX[0]));
                    params.y = (int) (initialY[0] + (event.getRawY() - initialTouchY[0]));
                    windowManager.updateViewLayout(floatingView, params);
                    return true;
            }
            return false;
        });
    }
    
    /**
     * 切换展开/收起
     */
    private void toggleExpand() {
        isExpanded = !isExpanded;
        contentLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        toggleButton.setText(isExpanded ? "收起" : "展开");
    }
    
    /**
     * 清除日志
     */
    private void clearLogs() {
        eventLogs.clear();
        appearCount = 0;
        halfVisibleCount = 0;
        fullyVisibleCount = 0;
        disappearCount = 0;
        updateStats();
        eventLogTextView.setText("日志已清除\n");
    }
    
    /**
     * 更新统计信息
     */
    private void updateStats() {
        String stats = String.format(Locale.getDefault(),
            "露出:%d | 50%%:%d | 完整:%d | 消失:%d",
            appearCount, halfVisibleCount, fullyVisibleCount, disappearCount);
        statsTextView.setText(stats);
    }
    
    /**
     * 添加事件日志
     */
    private void addEventLog(CardExposureEvent event, String color) {
        String time = timeFormat.format(new Date(event.getTimestamp()));
        String log = String.format("[%s] %s", time, event.toString());
        
        eventLogs.add(0, log);  // 添加到开头
        
        // 限制日志数量
        while (eventLogs.size() > MAX_EVENTS) {
            eventLogs.remove(eventLogs.size() - 1);
        }
        
        // 更新显示
        StringBuilder sb = new StringBuilder();
        for (String l : eventLogs) {
            sb.append(l).append("\n");
        }
        eventLogTextView.setText(sb.toString());
        
        // 滚动到顶部
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_UP));
    }
    
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    
    // CardExposureListener 实现
    
    @Override
    public void onCardAppear(CardExposureEvent event) {
        appearCount++;
        updateStats();
        addEventLog(event, "#4CAF50");  // 绿色
    }
    
    @Override
    public void onCardHalfVisible(CardExposureEvent event) {
        halfVisibleCount++;
        updateStats();
        addEventLog(event, "#2196F3");  // 蓝色
    }
    
    @Override
    public void onCardFullyVisible(CardExposureEvent event) {
        fullyVisibleCount++;
        updateStats();
        addEventLog(event, "#FF9800");  // 橙色
    }
    
    @Override
    public void onCardDisappear(CardExposureEvent event) {
        disappearCount++;
        updateStats();
        addEventLog(event, "#F44336");  // 红色
    }
    
    /**
     * 检查是否正在显示
     */
    public boolean isShowing() {
        return isShowing;
    }
}
