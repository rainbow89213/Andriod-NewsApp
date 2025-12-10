package com.example.demo2.exposure;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo2.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡片曝光追踪器
 * 监听RecyclerView滚动，计算每个卡片的可见性并触发相应事件
 */
public class CardExposureTracker {
    
    private static final String TAG = "CardExposureTracker";
    private static final boolean DEBUG_ENABLED = false;  // 禁用调试日志
    
    // 曝光状态记录（position -> ExposureState）
    private final SparseArray<ExposureState> exposureStates = new SparseArray<>();
    
    // 监听器列表
    private final List<CardExposureListener> listeners = new ArrayList<>();
    
    // 数据提供者
    private DataProvider dataProvider;
    
    // RecyclerView引用
    private RecyclerView recyclerView;
    
    // 滚动监听器
    private RecyclerView.OnScrollListener scrollListener;
    
    /**
     * 曝光状态类
     */
    private static class ExposureState {
        boolean hasAppeared = false;      // 是否已触发露出事件
        boolean hasHalfVisible = false;   // 是否已触发50%事件
        boolean hasFullyVisible = false;  // 是否已触发完整露出事件
        boolean hasDisappeared = true;    // 是否已触发消失事件（初始为true，表示未显示过）
        float lastVisibility = 0f;        // 上次可见性
        
        void reset() {
            hasAppeared = false;
            hasHalfVisible = false;
            hasFullyVisible = false;
            hasDisappeared = true;
            lastVisibility = 0f;
        }
    }
    
    /**
     * 数据提供者接口
     */
    public interface DataProvider {
        NewsItem getNewsItem(int position);
        int getItemCount();
    }
    
    /**
     * 添加监听器
     */
    public void addListener(CardExposureListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            if (DEBUG_ENABLED) Log.d(TAG, "✅ 添加监听器，当前监听器数量: " + listeners.size());
        }
    }
    
    /**
     * 移除监听器
     */
    public void removeListener(CardExposureListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * 设置数据提供者
     */
    public void setDataProvider(DataProvider provider) {
        this.dataProvider = provider;
    }
    
    /**
     * 绑定到RecyclerView
     */
    public void attachToRecyclerView(RecyclerView recyclerView) {
        // 先解绑之前的
        detachFromRecyclerView();
        
        this.recyclerView = recyclerView;
        
        // 创建滚动监听器
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkVisibility();
            }
            
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 滚动停止时也检查一次
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkVisibility();
                }
            }
        };
        
        recyclerView.addOnScrollListener(scrollListener);
        
        // 初始检查
        recyclerView.post(this::checkVisibility);
    }
    
    /**
     * 从RecyclerView解绑
     */
    public void detachFromRecyclerView() {
        if (recyclerView != null && scrollListener != null) {
            recyclerView.removeOnScrollListener(scrollListener);
        }
        recyclerView = null;
        scrollListener = null;
    }
    
    /**
     * 清除所有状态
     */
    public void clearStates() {
        exposureStates.clear();
    }
    
    /**
     * 检查所有可见项的可见性
     */
    public void checkVisibility() {
        if (recyclerView == null || dataProvider == null) {
            if (DEBUG_ENABLED) Log.d(TAG, "checkVisibility: recyclerView或dataProvider为null");
            return;
        }
        
        if (listeners.isEmpty()) {
            if (DEBUG_ENABLED) Log.d(TAG, "checkVisibility: 没有监听器");
            return;
        }
        
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        
        if (firstVisible == RecyclerView.NO_POSITION) {
            return;
        }
        
        if (DEBUG_ENABLED) Log.d(TAG, "checkVisibility: 可见范围 " + firstVisible + " - " + lastVisible + ", 监听器数量: " + listeners.size());
        
        // 检查所有已记录的状态，处理消失的卡片
        for (int i = 0; i < exposureStates.size(); i++) {
            int position = exposureStates.keyAt(i);
            if (position < firstVisible || position > lastVisible) {
                // 卡片不在可见范围内
                ExposureState state = exposureStates.get(position);
                if (state != null && state.hasAppeared && !state.hasDisappeared) {
                    // 触发消失事件
                    triggerDisappearEvent(position, state);
                }
            }
        }
        
        // 检查可见范围内的卡片
        for (int position = firstVisible; position <= lastVisible; position++) {
            if (position >= dataProvider.getItemCount()) {
                continue;
            }
            
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
            if (holder == null) {
                continue;
            }
            
            float visibility = calculateVisibility(holder.itemView);
            processVisibilityChange(position, visibility);
        }
    }
    
    /**
     * 计算View的可见性百分比
     */
    private float calculateVisibility(View view) {
        if (view == null || recyclerView == null) {
            return 0f;
        }
        
        Rect itemRect = new Rect();
        boolean isVisible = view.getLocalVisibleRect(itemRect);
        
        if (!isVisible) {
            return 0f;
        }
        
        int viewHeight = view.getHeight();
        if (viewHeight == 0) {
            return 0f;
        }
        
        int visibleHeight = itemRect.height();
        float visibility = (float) visibleHeight / viewHeight * 100f;
        
        return Math.min(100f, Math.max(0f, visibility));
    }
    
    /**
     * 处理可见性变化
     */
    private void processVisibilityChange(int position, float visibility) {
        ExposureState state = exposureStates.get(position);
        if (state == null) {
            state = new ExposureState();
            exposureStates.put(position, state);
        }
        
        NewsItem newsItem = dataProvider.getNewsItem(position);
        if (newsItem == null) {
            return;
        }
        
        // 检查各种事件条件
        
        // 1. 卡片露出（可见性 > 0%）
        if (visibility > 0 && !state.hasAppeared) {
            state.hasAppeared = true;
            state.hasDisappeared = false;
            CardExposureEvent event = new CardExposureEvent(
                position, 
                String.valueOf(position),
                newsItem.getTitle(),
                CardExposureEvent.EventType.CARD_APPEAR,
                visibility
            );
            notifyCardAppear(event);
        }
        
        // 2. 卡片露出超过50%
        if (visibility > 50 && !state.hasHalfVisible) {
            state.hasHalfVisible = true;
            CardExposureEvent event = new CardExposureEvent(
                position,
                String.valueOf(position),
                newsItem.getTitle(),
                CardExposureEvent.EventType.CARD_HALF_VISIBLE,
                visibility
            );
            notifyCardHalfVisible(event);
        }
        
        // 3. 卡片完整露出（可见性 >= 99%，考虑浮点误差）
        if (visibility >= 99 && !state.hasFullyVisible) {
            state.hasFullyVisible = true;
            CardExposureEvent event = new CardExposureEvent(
                position,
                String.valueOf(position),
                newsItem.getTitle(),
                CardExposureEvent.EventType.CARD_FULLY_VISIBLE,
                visibility
            );
            notifyCardFullyVisible(event);
        }
        
        // 4. 可见性下降时重置状态（允许重复触发）
        if (visibility < 50 && state.hasHalfVisible) {
            state.hasHalfVisible = false;
        }
        if (visibility < 99 && state.hasFullyVisible) {
            state.hasFullyVisible = false;
        }
        
        // 5. 卡片消失
        if (visibility == 0 && state.hasAppeared && !state.hasDisappeared) {
            triggerDisappearEvent(position, state);
        }
        
        state.lastVisibility = visibility;
    }
    
    /**
     * 触发消失事件
     */
    private void triggerDisappearEvent(int position, ExposureState state) {
        NewsItem newsItem = dataProvider.getNewsItem(position);
        if (newsItem == null) {
            return;
        }
        
        state.hasDisappeared = true;
        state.hasAppeared = false;
        state.hasHalfVisible = false;
        state.hasFullyVisible = false;
        
        CardExposureEvent event = new CardExposureEvent(
            position,
            String.valueOf(position),
            newsItem.getTitle(),
            CardExposureEvent.EventType.CARD_DISAPPEAR,
            0f
        );
        notifyCardDisappear(event);
    }
    
    // 通知方法
    private void notifyCardAppear(CardExposureEvent event) {
        if (DEBUG_ENABLED) Log.d(TAG, "📍 " + event.toString());
        for (CardExposureListener listener : listeners) {
            listener.onCardAppear(event);
        }
    }
    
    private void notifyCardHalfVisible(CardExposureEvent event) {
        if (DEBUG_ENABLED) Log.d(TAG, "📍 " + event.toString());
        for (CardExposureListener listener : listeners) {
            listener.onCardHalfVisible(event);
        }
    }
    
    private void notifyCardFullyVisible(CardExposureEvent event) {
        if (DEBUG_ENABLED) Log.d(TAG, "📍 " + event.toString());
        for (CardExposureListener listener : listeners) {
            listener.onCardFullyVisible(event);
        }
    }
    
    private void notifyCardDisappear(CardExposureEvent event) {
        if (DEBUG_ENABLED) Log.d(TAG, "📍 " + event.toString());
        for (CardExposureListener listener : listeners) {
            listener.onCardDisappear(event);
        }
    }
}
