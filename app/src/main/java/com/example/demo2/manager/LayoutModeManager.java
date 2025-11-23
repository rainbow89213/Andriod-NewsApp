package com.example.demo2.manager;

import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo2.NewsAdapter;

/**
 * LayoutModeManager - 布局模式管理器
 * 
 * 职责：
 * - 管理单列/双列布局模式切换
 * - 控制RecyclerView的LayoutManager
 * - 更新Adapter的布局模式状态
 * - 更新切换按钮图标
 */
public class LayoutModeManager {
    
    private static final String TAG = "LayoutModeManager";
    
    // 布局模式常量
    public static final int LAYOUT_MODE_SINGLE = 1;  // 单列模式
    public static final int LAYOUT_MODE_GRID = 2;    // 双列模式
    
    // 上下文
    private Context context;
    
    // RecyclerView
    private RecyclerView recyclerView;
    
    // Adapter
    private NewsAdapter newsAdapter;
    
    // 切换按钮
    private ImageButton layoutSwitchButton;
    
    // 当前布局模式
    private int currentLayoutMode = LAYOUT_MODE_SINGLE;
    
    // 布局模式切换监听器
    private OnLayoutModeChangeListener layoutModeChangeListener;
    
    /**
     * 布局模式切换监听接口
     */
    public interface OnLayoutModeChangeListener {
        void onLayoutModeChanged(int newMode);
    }
    
    /**
     * 构造函数
     */
    public LayoutModeManager(Context context, RecyclerView recyclerView, 
                            NewsAdapter newsAdapter, ImageButton layoutSwitchButton) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.newsAdapter = newsAdapter;
        this.layoutSwitchButton = layoutSwitchButton;
    }
    
    /**
     * 初始化布局切换按钮
     */
    public void initLayoutSwitchButton() {
        // 设置初始图标
        updateButtonIcon();
        
        // 设置点击监听
        layoutSwitchButton.setOnClickListener(v -> {
            toggleLayoutMode();
        });
        
        Log.d(TAG, "✅ 布局切换按钮初始化完成");
    }
    
    /**
     * 切换布局模式
     */
    public void toggleLayoutMode() {
        if (currentLayoutMode == LAYOUT_MODE_SINGLE) {
            switchToGridMode();
        } else {
            switchToSingleMode();
        }
        
        // 触发监听器
        if (layoutModeChangeListener != null) {
            layoutModeChangeListener.onLayoutModeChanged(currentLayoutMode);
        }
    }
    
    /**
     * 切换到单列模式
     */
    private void switchToSingleMode() {
        Log.d(TAG, "🔄 切换到单列模式");
        currentLayoutMode = LAYOUT_MODE_SINGLE;
        
        // 1. 切换LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        
        // 2. 更新Adapter状态
        if (newsAdapter != null) {
            newsAdapter.setGridMode(false);
        }
        
        // 3. 更新按钮图标
        updateButtonIcon();
        
        Log.d(TAG, "✅ 已切换到单列模式");
    }
    
    /**
     * 切换到双列模式
     */
    private void switchToGridMode() {
        Log.d(TAG, "🔄 切换到双列模式");
        currentLayoutMode = LAYOUT_MODE_GRID;
        
        // 1. 切换LayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        
        // 设置SpanSizeLookup：让"加载更多"卡片占满整行
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (newsAdapter != null) {
                    int viewType = newsAdapter.getItemViewType(position);
                    // "加载更多"卡片占2列（整行）
                    if (viewType == NewsAdapter.VIEW_TYPE_LOAD_MORE) {
                        return 2;
                    }
                }
                // 新闻卡片占1列
                return 1;
            }
        });
        
        recyclerView.setLayoutManager(gridLayoutManager);
        
        // 2. 更新Adapter状态
        if (newsAdapter != null) {
            newsAdapter.setGridMode(true);
        }
        
        // 3. 更新按钮图标
        updateButtonIcon();
        
        Log.d(TAG, "✅ 已切换到双列模式");
    }
    
    /**
     * 更新按钮图标
     */
    private void updateButtonIcon() {
        if (layoutSwitchButton == null) return;
        
        if (currentLayoutMode == LAYOUT_MODE_SINGLE) {
            // 单列模式 → 显示网格图标（提示可以切换到双列）
            layoutSwitchButton.setImageResource(android.R.drawable.ic_dialog_dialer);
        } else {
            // 双列模式 → 显示列表图标（提示可以切换到单列）
            layoutSwitchButton.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        }
    }
    
    /**
     * 获取当前布局模式
     */
    public int getCurrentLayoutMode() {
        return currentLayoutMode;
    }
    
    /**
     * 是否为网格模式
     */
    public boolean isGridMode() {
        return currentLayoutMode == LAYOUT_MODE_GRID;
    }
    
    /**
     * 设置布局模式切换监听器
     */
    public void setOnLayoutModeChangeListener(OnLayoutModeChangeListener listener) {
        this.layoutModeChangeListener = listener;
    }
}
