package com.example.demo2.exposure;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 曝光事件测试视图（内嵌式，不需要悬浮窗权限）
 * 可以直接添加到Activity的布局中
 */
public class ExposureTestView extends FrameLayout implements CardExposureListener {
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 检查触摸是否在可见的子视图上
        // 如果不在，则不拦截，让事件穿透
        return false;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 检查触摸点是否在触发按钮或主面板上
        float x = event.getX();
        float y = event.getY();
        
        // 检查触发按钮
        if (triggerButton != null && triggerButton.getVisibility() == VISIBLE) {
            if (isPointInsideView(x, y, triggerButton)) {
                return super.onTouchEvent(event);
            }
        }
        
        // 检查主面板
        if (mainContainer != null && mainContainer.getVisibility() == VISIBLE) {
            if (isPointInsideView(x, y, mainContainer)) {
                return super.onTouchEvent(event);
            }
        }
        
        // 不在任何子视图上，让事件穿透
        return false;
    }
    
    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        
        int[] parentLocation = new int[2];
        this.getLocationInWindow(parentLocation);
        
        float viewX = location[0] - parentLocation[0];
        float viewY = location[1] - parentLocation[1];
        
        return x >= viewX && x <= viewX + view.getWidth() &&
               y >= viewY && y <= viewY + view.getHeight();
    }
    
    private static final int MAX_EVENTS = 50;
    
    private LinearLayout mainContainer;
    private LinearLayout contentLayout;
    private TextView statsTextView;
    private TextView eventLogTextView;
    private ScrollView scrollView;
    private Button toggleButton;
    private View triggerButton;
    
    private boolean isExpanded = false;
    
    // 事件统计
    private int appearCount = 0;
    private int halfVisibleCount = 0;
    private int fullyVisibleCount = 0;
    private int disappearCount = 0;
    
    // 事件日志
    private final List<String> eventLogs = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
    
    public ExposureTestView(Context context) {
        super(context);
        init();
    }
    
    public ExposureTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ExposureTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        // 设置不拦截触摸事件，让事件穿透到下层视图
        setClickable(false);
        setFocusable(false);
        
        // 触发按钮（始终显示在右下角）
        triggerButton = createTriggerButton();
        FrameLayout.LayoutParams triggerParams = new FrameLayout.LayoutParams(
            dpToPx(48), dpToPx(48)
        );
        triggerParams.gravity = Gravity.BOTTOM | Gravity.END;
        triggerParams.setMargins(0, 0, dpToPx(16), dpToPx(80));
        addView(triggerButton, triggerParams);
        
        // 主面板
        mainContainer = createMainPanel();
        FrameLayout.LayoutParams mainParams = new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, dpToPx(280)
        );
        mainParams.gravity = Gravity.BOTTOM;
        mainContainer.setVisibility(GONE);
        addView(mainContainer, mainParams);
    }
    
    private View createTriggerButton() {
        TextView btn = new TextView(getContext());
        btn.setText("📊");
        btn.setTextSize(20);
        btn.setGravity(Gravity.CENTER);
        btn.setBackgroundColor(Color.parseColor("#E02196F3"));
        btn.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        btn.setOnClickListener(v -> togglePanel());
        return btn;
    }
    
    private LinearLayout createMainPanel() {
        LinearLayout panel = new LinearLayout(getContext());
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundColor(Color.parseColor("#F5F5F5"));  // 浅灰色背景
        panel.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        panel.setElevation(dpToPx(8));  // 添加阴影
        
        // 标题栏
        LinearLayout titleBar = new LinearLayout(getContext());
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView title = new TextView(getContext());
        title.setText("📊 卡片曝光事件测试工具");
        title.setTextColor(Color.parseColor("#333333"));  // 深灰色文字
        title.setTextSize(14);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        
        Button clearBtn = new Button(getContext());
        clearBtn.setText("清除");
        clearBtn.setTextSize(10);
        clearBtn.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        clearBtn.setOnClickListener(v -> clearLogs());
        
        Button closeBtn = new Button(getContext());
        closeBtn.setText("关闭");
        closeBtn.setTextSize(10);
        closeBtn.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        closeBtn.setOnClickListener(v -> togglePanel());
        
        titleBar.addView(title);
        titleBar.addView(clearBtn);
        titleBar.addView(closeBtn);
        
        // 统计信息
        statsTextView = new TextView(getContext());
        statsTextView.setTextColor(Color.parseColor("#1976D2"));  // 蓝色
        statsTextView.setTextSize(12);
        statsTextView.setPadding(0, dpToPx(8), 0, dpToPx(8));
        updateStats();
        
        // 事件类型图例
        LinearLayout legendLayout = createLegend();
        
        // 事件日志
        scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, 0, 1
        ));
        scrollView.setBackgroundColor(Color.WHITE);  // 白色背景
        scrollView.setPadding(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        
        eventLogTextView = new TextView(getContext());
        eventLogTextView.setTextColor(Color.parseColor("#333333"));  // 深灰色文字
        eventLogTextView.setTextSize(11);
        eventLogTextView.setText("等待曝光事件...\n滚动列表查看效果");
        eventLogTextView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        eventLogTextView.setBackgroundColor(Color.WHITE);
        
        scrollView.addView(eventLogTextView);
        
        panel.addView(titleBar);
        panel.addView(statsTextView);
        panel.addView(legendLayout);
        panel.addView(scrollView);
        
        return panel;
    }
    
    private LinearLayout createLegend() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(0, 0, 0, dpToPx(8));
        
        addLegendItem(layout, "🟢 露出", "#4CAF50");
        addLegendItem(layout, "🔵 50%", "#2196F3");
        addLegendItem(layout, "🟠 完整", "#FF9800");
        addLegendItem(layout, "🔴 消失", "#F44336");
        
        return layout;
    }
    
    private void addLegendItem(LinearLayout parent, String text, String color) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextColor(Color.parseColor(color));
        tv.setTextSize(10);
        tv.setPadding(0, 0, dpToPx(12), 0);
        parent.addView(tv);
    }
    
    private void togglePanel() {
        isExpanded = !isExpanded;
        mainContainer.setVisibility(isExpanded ? VISIBLE : GONE);
        triggerButton.setAlpha(isExpanded ? 0.5f : 1f);
    }
    
    private void clearLogs() {
        eventLogs.clear();
        appearCount = 0;
        halfVisibleCount = 0;
        fullyVisibleCount = 0;
        disappearCount = 0;
        updateStats();
        eventLogTextView.setText("日志已清除\n");
    }
    
    private void updateStats() {
        if (statsTextView != null) {
            String stats = String.format(Locale.getDefault(),
                "露出: %d  |  50%%: %d  |  完整: %d  |  消失: %d",
                appearCount, halfVisibleCount, fullyVisibleCount, disappearCount);
            statsTextView.setText(stats);
        }
    }
    
    private void addEventLog(CardExposureEvent event, String emoji) {
        String time = timeFormat.format(new Date(event.getTimestamp()));
        String shortTitle = event.getNewsTitle();
        if (shortTitle.length() > 12) {
            shortTitle = shortTitle.substring(0, 12) + "...";
        }
        String log = String.format("%s [%s] 位置:%d %s - %s (%.0f%%)",
            emoji, time, event.getPosition(), 
            event.getEventType().getDescription(),
            shortTitle, event.getVisibilityPercent());
        
        eventLogs.add(0, log);
        
        while (eventLogs.size() > MAX_EVENTS) {
            eventLogs.remove(eventLogs.size() - 1);
        }
        
        StringBuilder sb = new StringBuilder();
        for (String l : eventLogs) {
            sb.append(l).append("\n");
        }
        eventLogTextView.setText(sb.toString());
        
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_UP));
    }
    
    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    
    // CardExposureListener 实现
    
    @Override
    public void onCardAppear(CardExposureEvent event) {
        post(() -> {
            appearCount++;
            updateStats();
            addEventLog(event, "🟢");
        });
    }
    
    @Override
    public void onCardHalfVisible(CardExposureEvent event) {
        post(() -> {
            halfVisibleCount++;
            updateStats();
            addEventLog(event, "🔵");
        });
    }
    
    @Override
    public void onCardFullyVisible(CardExposureEvent event) {
        post(() -> {
            fullyVisibleCount++;
            updateStats();
            addEventLog(event, "🟠");
        });
    }
    
    @Override
    public void onCardDisappear(CardExposureEvent event) {
        post(() -> {
            disappearCount++;
            updateStats();
            addEventLog(event, "🔴");
        });
    }
    
    /**
     * 显示面板
     */
    public void showPanel() {
        if (!isExpanded) {
            togglePanel();
        }
    }
    
    /**
     * 隐藏面板
     */
    public void hidePanel() {
        if (isExpanded) {
            togglePanel();
        }
    }
    
    /**
     * 设置触发按钮可见性
     */
    public void setTriggerVisible(boolean visible) {
        triggerButton.setVisibility(visible ? VISIBLE : GONE);
    }
}
