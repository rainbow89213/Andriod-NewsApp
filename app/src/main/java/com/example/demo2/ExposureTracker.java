package com.example.demo2;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡片曝光事件追踪器
 * 
 * 监听RecyclerView的滚动，计算每个卡片的可见比例，触发相应的曝光事件
 */
public class ExposureTracker extends RecyclerView.OnScrollListener {
    
    private static final String TAG = "ExposureTracker";
    
    // 曝光阈值
    private static final float THRESHOLD_HALF = 0.5f;    // 50%
    private static final float THRESHOLD_FULL = 1.0f;    // 100%
    
    // RecyclerView引用
    private final RecyclerView recyclerView;
    
    // 新闻列表
    private final List<NewsItem> newsList;
    
    // 曝光事件监听器
    private ExposureEventListener listener;
    
    // 每个位置的曝光状态
    private final Map<Integer, ExposureState> exposureStateMap = new HashMap<>();
    
    // 是否正在追踪
    private boolean isTracking = false;
    
    public ExposureTracker(RecyclerView recyclerView, List<NewsItem> newsList) {
        this.recyclerView = recyclerView;
        this.newsList = newsList;
    }
    
    /**
     * 设置曝光事件监听器
     */
    public void setExposureEventListener(ExposureEventListener listener) {
        this.listener = listener;
    }
    
    /**
     * 开始追踪
     */
    public void startTracking() {
        if (!isTracking) {
            recyclerView.addOnScrollListener(this);
            isTracking = true;
            // 初始检查一次（处理已经可见的卡片）
            checkExposure();
        }
    }
    
    /**
     * 暂停追踪
     */
    public void pauseTracking() {
        isTracking = false;
    }
    
    /**
     * 恢复追踪
     */
    public void resumeTracking() {
        if (!isTracking) {
            isTracking = true;
            // 恢复时检查一次
            checkExposure();
        }
    }
    
    /**
     * 停止追踪
     */
    public void stopTracking() {
        recyclerView.removeOnScrollListener(this);
        exposureStateMap.clear();
        isTracking = false;
    }
    
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        checkExposure();
    }
    
    /**
     * 检查所有可见卡片的曝光情况
     */
    private void checkExposure() {
        if (!isTracking || listener == null || newsList == null || newsList.isEmpty()) {
            return;
        }
        
        // 遍历所有子视图
        int childCount = recyclerView.getChildCount();
        
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            int position = recyclerView.getChildAdapterPosition(child);
            
            // 跳过加载更多卡片和无效位置
            if (position < 0 || position >= newsList.size()) {
                continue;
            }
            
            // 获取新闻项
            NewsItem newsItem = newsList.get(position);
            
            // 计算可见比例
            float visiblePercent = calculateVisiblePercent(child);
            
            // 获取或创建曝光状态
            ExposureState state = exposureStateMap.get(position);
            if (state == null) {
                state = new ExposureState();
                exposureStateMap.put(position, state);
            }
            
            // 处理曝光事件
            handleExposureEvent(position, newsItem, visiblePercent, state);
        }
        
        // 检查已消失的卡片
        checkDisappearedCards();
    }
    
    /**
     * 计算卡片的可见比例
     * 
     * @param view 卡片视图
     * @return 可见比例（0.0-1.0）
     */
    private float calculateVisiblePercent(View view) {
        // 获取RecyclerView的可见区域
        Rect recyclerRect = new Rect();
        recyclerView.getGlobalVisibleRect(recyclerRect);
        
        // 获取卡片的位置
        Rect cardRect = new Rect();
        view.getGlobalVisibleRect(cardRect);
        
        // 计算卡片高度
        int cardHeight = view.getHeight();
        if (cardHeight == 0) {
            return 0f;
        }
        
        // 计算可见区域
        int visibleTop = Math.max(cardRect.top, recyclerRect.top);
        int visibleBottom = Math.min(cardRect.bottom, recyclerRect.bottom);
        int visibleHeight = Math.max(0, visibleBottom - visibleTop);
        
        // 计算可见比例
        float percent = (float) visibleHeight / cardHeight;
        
        return Math.max(0f, Math.min(1f, percent));
    }
    
    /**
     * 处理曝光事件
     */
    private void handleExposureEvent(int position, NewsItem newsItem, float visiblePercent, ExposureState state) {
        
        // 1. 卡片开始露出（任意像素可见）
        if (visiblePercent > 0 && !state.hasAppeared()) {
            Log.d(TAG, String.format("📍 卡片露出 - 位置: %d, 标题: %s, 可见度: %.2f%%", 
                position, newsItem.getTitle(), visiblePercent * 100));
            state.setAppeared(true);
            state.setCurrentlyVisible(true);
            listener.onCardAppear(position, newsItem);
        }
        
        // 2. 卡片露出超过50%
        if (visiblePercent >= THRESHOLD_HALF && !state.hasHalfVisible()) {
            Log.d(TAG, String.format("📊 卡片50%%可见 - 位置: %d, 标题: %s, 可见度: %.2f%%", 
                position, newsItem.getTitle(), visiblePercent * 100));
            state.setHalfVisible(true);
            listener.onCardHalfVisible(position, newsItem, visiblePercent);
        }
        
        // 3. 卡片完整露出（100%可见）
        if (visiblePercent >= THRESHOLD_FULL && !state.hasFullyVisible()) {
            Log.d(TAG, String.format("✅ 卡片完整露出 - 位置: %d, 标题: %s", 
                position, newsItem.getTitle()));
            state.setFullyVisible(true);
            listener.onCardFullyVisible(position, newsItem);
        }
        
        // 更新状态
        state.setLastVisiblePercent(visiblePercent);
        if (visiblePercent > 0) {
            state.setCurrentlyVisible(true);
        }
    }
    
    /**
     * 检查已消失的卡片
     */
    private void checkDisappearedCards() {
        // 收集当前可见的位置
        HashMap<Integer, Boolean> currentVisiblePositions = new HashMap<>();
        int childCount = recyclerView.getChildCount();
        
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            int position = recyclerView.getChildAdapterPosition(child);
            if (position >= 0 && position < newsList.size()) {
                float visiblePercent = calculateVisiblePercent(child);
                if (visiblePercent > 0) {
                    currentVisiblePositions.put(position, true);
                }
            }
        }
        
        // 检查之前可见但现在不可见的卡片
        for (Map.Entry<Integer, ExposureState> entry : exposureStateMap.entrySet()) {
            int position = entry.getKey();
            ExposureState state = entry.getValue();
            
            // 如果之前可见，现在不可见，触发消失事件
            if (state.isCurrentlyVisible() && !currentVisiblePositions.containsKey(position)) {
                if (position < newsList.size()) {
                    NewsItem newsItem = newsList.get(position);
                    Log.d(TAG, String.format("👋 卡片消失 - 位置: %d, 标题: %s", 
                        position, newsItem.getTitle()));
                    listener.onCardDisappear(position, newsItem);
                }
                // 重置状态
                state.reset();
            }
        }
    }
    
    /**
     * 清除指定位置的曝光状态（用于数据刷新）
     */
    public void clearExposureState(int position) {
        exposureStateMap.remove(position);
    }
    
    /**
     * 清除所有曝光状态
     */
    public void clearAllExposureStates() {
        exposureStateMap.clear();
    }
}
