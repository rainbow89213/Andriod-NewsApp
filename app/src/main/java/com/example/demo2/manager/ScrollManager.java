package com.example.demo2.manager;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ScrollManager - 滚动管理器
 * 
 * 职责：
 * - 管理下拉刷新逻辑
 * - 管理自定义滚动条
 * - 处理滚动事件监听
 */
public class ScrollManager {
    
    private static final String TAG = "ScrollManager";
    
    // 下拉阈值
    private static final int PULL_THRESHOLD = 200;
    
    // 上下文
    private Context context;
    
    // RecyclerView
    private RecyclerView recyclerView;
    
    // 自定义滚动条
    private View customScrollbar;
    
    // 下拉刷新相关
    private float pullDownStartY = 0;
    private boolean isPullingDown = false;
    
    // 滚动条拖动相关
    private boolean isDraggingScrollbar = false;
    private float scrollbarDragStartY = 0;
    private int scrollbarInitialTop = 0;
    
    // 下拉刷新监听器
    private OnPullRefreshListener pullRefreshListener;
    
    /**
     * 下拉刷新监听接口
     */
    public interface OnPullRefreshListener {
        void onPullRefresh();
    }
    
    /**
     * 构造函数
     */
    public ScrollManager(Context context, RecyclerView recyclerView, View customScrollbar) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.customScrollbar = customScrollbar;
    }
    
    /**
     * 初始化下拉刷新
     */
    public void initPullToRefresh() {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 只有在列表顶部时才记录起始位置
                        if (isAtTop()) {
                            pullDownStartY = event.getY();
                            isPullingDown = true;
                            Log.d(TAG, "👆 ACTION_DOWN - 在顶部，准备下拉刷新");
                        } else {
                            isPullingDown = false;
                        }
                        break;
                        
                    case MotionEvent.ACTION_MOVE:
                        if (isPullingDown && isAtTop()) {
                            float currentY = event.getY();
                            float deltaY = currentY - pullDownStartY;
                            
                            if (deltaY > PULL_THRESHOLD) {
                                Log.d(TAG, "🔄 下拉超过阈值，触发刷新");
                                isPullingDown = false;
                                
                                if (pullRefreshListener != null) {
                                    pullRefreshListener.onPullRefresh();
                                }
                            }
                        }
                        break;
                        
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isPullingDown = false;
                        break;
                }
                
                return false;
            }
        });
        
        Log.d(TAG, "✅ 下拉刷新初始化完成");
    }
    
    /**
     * 初始化自定义滚动条
     */
    public void initCustomScrollbar() {
        if (customScrollbar == null) {
            Log.w(TAG, "⚠️ customScrollbar为null，跳过初始化");
            return;
        }
        
        // 设置滚动条触摸监听
        customScrollbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDraggingScrollbar = true;
                        scrollbarDragStartY = event.getRawY();
                        scrollbarInitialTop = v.getTop();
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        if (isDraggingScrollbar) {
                            float deltaY = event.getRawY() - scrollbarDragStartY;
                            int newTop = (int) (scrollbarInitialTop + deltaY);
                            
                            // 限制滚动条范围
                            int maxTop = recyclerView.getHeight() - v.getHeight();
                            newTop = Math.max(0, Math.min(newTop, maxTop));
                            
                            // 计算对应的列表滚动位置
                            float scrollPercent = (float) newTop / maxTop;
                            int totalScrollRange = recyclerView.computeVerticalScrollRange();
                            int scrollTo = (int) (scrollPercent * totalScrollRange);
                            
                            recyclerView.scrollTo(0, scrollTo);
                        }
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isDraggingScrollbar = false;
                        return true;
                }
                return false;
            }
        });
        
        // 监听RecyclerView滚动，同步更新滚动条位置
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateScrollbarPosition();
            }
        });
        
        Log.d(TAG, "✅ 自定义滚动条初始化完成");
    }
    
    /**
     * 更新滚动条位置
     */
    private void updateScrollbarPosition() {
        if (customScrollbar == null || isDraggingScrollbar) return;
        
        int scrollRange = recyclerView.computeVerticalScrollRange();
        int scrollOffset = recyclerView.computeVerticalScrollOffset();
        int scrollExtent = recyclerView.computeVerticalScrollExtent();
        
        if (scrollRange <= scrollExtent) {
            // 内容不足一屏，隐藏滚动条
            customScrollbar.setVisibility(View.GONE);
            return;
        }
        
        customScrollbar.setVisibility(View.VISIBLE);
        
        // 计算滚动百分比
        float scrollPercent = (float) scrollOffset / (scrollRange - scrollExtent);
        
        // 计算滚动条位置
        int scrollbarHeight = customScrollbar.getHeight();
        int maxTop = recyclerView.getHeight() - scrollbarHeight;
        int newTop = (int) (scrollPercent * maxTop);
        
        customScrollbar.setTop(newTop);
    }
    
    /**
     * 判断是否在列表顶部
     */
    private boolean isAtTop() {
        if (recyclerView == null) return false;
        
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
            
            if (firstVisiblePosition == 0) {
                View firstView = linearLayoutManager.findViewByPosition(0);
                if (firstView != null) {
                    return firstView.getTop() >= 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 设置下拉刷新监听器
     */
    public void setOnPullRefreshListener(OnPullRefreshListener listener) {
        this.pullRefreshListener = listener;
    }
}
