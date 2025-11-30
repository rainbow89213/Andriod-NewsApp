package com.example.demo2.manager;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Looper;

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
    
    // 自动加载相关变量
    private Handler autoLoadHandler = new Handler(Looper.getMainLooper());
    private Runnable autoLoadRunnable = null;
    private boolean isAutoLoadTriggered = false;
    private static final int AUTO_LOAD_DELAY = 2000;  // 2秒延迟
    private OnAutoLoadListener autoLoadListener;
    
    /**
     * 下拉刷新监听接口
     */
    public interface OnPullRefreshListener {
        void onPullRefresh();
    }
    
    /**
     * 自动加载监听接口
     */
    public interface OnAutoLoadListener {
        void onAutoLoad();
        boolean hasMoreData();
        boolean isLoadingMore();
        void setLoading(boolean loading);
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
        
        // 监听RecyclerView滚动，同步更新滚动条位置和自动加载检测
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateScrollbarPosition();
                
                // 检测自动加载
                checkAndTriggerAutoLoad();
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
    
    /**
     * 设置自动加载监听器
     */
    public void setOnAutoLoadListener(OnAutoLoadListener listener) {
        this.autoLoadListener = listener;
    }
    
    /**
     * 检测并触发自动加载
     */
    private void checkAndTriggerAutoLoad() {
        if (autoLoadListener == null) {
            Log.w(TAG, "❌ autoLoadListener为null，无法自动加载");
            return;
        }
        
        // 获取LayoutManager
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            Log.w(TAG, "❌ layoutManager为null，无法自动加载");
            return;
        }
        
        if (recyclerView.getAdapter() == null) {
            Log.w(TAG, "❌ adapter为null，无法自动加载");
            return;
        }
        
        int lastVisiblePosition = -1;
        int totalItemCount = recyclerView.getAdapter().getItemCount();
        
        // 根据不同的LayoutManager类型获取最后可见项
        if (layoutManager instanceof LinearLayoutManager) {
            lastVisiblePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            lastVisiblePosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        
        // 打印当前状态
        boolean hasMoreData = autoLoadListener.hasMoreData();
        boolean isLoadingMore = autoLoadListener.isLoadingMore();
        
        Log.d(TAG, "📊 自动加载检测状态：");
        Log.d(TAG, "  - 最后可见位置: " + lastVisiblePosition);
        Log.d(TAG, "  - 总项数: " + totalItemCount);
        Log.d(TAG, "  - 触发位置: " + (totalItemCount - 2));
        Log.d(TAG, "  - hasMoreData: " + hasMoreData);
        Log.d(TAG, "  - isLoadingMore: " + isLoadingMore);
        Log.d(TAG, "  - isAutoLoadTriggered: " + isAutoLoadTriggered);
        Log.d(TAG, "  - 可以触发: " + (lastVisiblePosition >= totalItemCount - 2));
        
        // 判断是否滚动到底部（加载卡片可见即触发）
        // 提前2个位置就开始加载，让体验更流畅
        if (lastVisiblePosition >= totalItemCount - 2 && 
            hasMoreData && 
            !isLoadingMore && 
            !isAutoLoadTriggered) {
            
            Log.d(TAG, "✅ 满足所有条件，准备自动加载");
            Log.d(TAG, "  - 最后可见位置: " + lastVisiblePosition);
            Log.d(TAG, "  - 总项数: " + totalItemCount);
            
            // 标记已触发
            isAutoLoadTriggered = true;
            
            // 显示加载中状态（延迟到下一帧执行，避免在滚动回调中修改RecyclerView）
            autoLoadHandler.post(() -> {
                if (autoLoadListener != null) {
                    autoLoadListener.setLoading(true);
                    Log.d(TAG, "✅ 已设置加载状态");
                }
            });
            Log.d(TAG, "🔄 延迟" + AUTO_LOAD_DELAY + "ms后自动加载");
            
            // 取消之前的延迟任务
            if (autoLoadRunnable != null) {
                autoLoadHandler.removeCallbacks(autoLoadRunnable);
            }
            
            // 创建延迟任务
            autoLoadRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "⏰ 2秒延迟到期，开始执行加载");
                    if (autoLoadListener != null) {
                        Log.d(TAG, "📤 调用onAutoLoad()");
                        autoLoadListener.onAutoLoad();
                        
                        // 设置超时检查
                        autoLoadHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isAutoLoadTriggered && autoLoadListener.isLoadingMore()) {
                                    Log.e(TAG, "⚠️ 警告：加载已经超过5秒还未完成！");
                                    Log.e(TAG, "  - 可能原因1：网络请求失败");
                                    Log.e(TAG, "  - 可能原因2：loadMoreNews()方法未被正确调用");
                                    Log.e(TAG, "  - 可能原因3：回调未正确处理");
                                }
                            }
                        }, 5000);  // 5秒后检查
                    } else {
                        Log.e(TAG, "❌ autoLoadListener变为null了，无法加载");
                    }
                }
            };
            
            // 延迟执行
            autoLoadHandler.postDelayed(autoLoadRunnable, AUTO_LOAD_DELAY);
        } else {
            // 打印为什么没有触发
            if (lastVisiblePosition < totalItemCount - 2) {
                Log.d(TAG, "⏸ 未触发：还没滑到底部");
            } else if (!hasMoreData) {
                Log.d(TAG, "⏸ 未触发：没有更多数据");
            } else if (isLoadingMore) {
                Log.d(TAG, "⏸ 未触发：正在加载中");
            } else if (isAutoLoadTriggered) {
                Log.d(TAG, "⏸ 未触发：已经触发过了");
            }
        }
    }
    
    /**
     * 重置自动加载标志（在加载完成后调用）
     */
    public void resetAutoLoadFlag() {
        isAutoLoadTriggered = false;
    }
}
