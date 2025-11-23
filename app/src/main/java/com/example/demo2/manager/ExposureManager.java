package com.example.demo2.manager;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.demo2.ExposureEventListener;
import com.example.demo2.ExposureTestPanel;
import com.example.demo2.ExposureTracker;
import com.example.demo2.NewsItem;

import java.util.List;

/**
 * ExposureManager - 曝光追踪管理器
 * 
 * 职责：
 * - 管理卡片曝光追踪器
 * - 管理测试面板
 * - 处理曝光事件回调
 */
public class ExposureManager {
    
    private static final String TAG = "ExposureManager";
    
    // 上下文
    private Context context;
    
    // RecyclerView
    private RecyclerView recyclerView;
    
    // 数据列表
    private List<NewsItem> newsList;
    
    // 曝光追踪器
    private ExposureTracker exposureTracker;
    
    // 测试面板
    private ExposureTestPanel testPanel;
    
    /**
     * 构造函数
     */
    public ExposureManager(Context context, RecyclerView recyclerView, List<NewsItem> newsList) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.newsList = newsList;
    }
    
    /**
     * 初始化曝光追踪
     */
    public void initExposureTracker(FrameLayout testPanelContainer) {
        // 创建并添加测试面板
        testPanel = new ExposureTestPanel(context);
        testPanelContainer.addView(testPanel);
        Log.d(TAG, "✅ 测试面板已创建");
        
        // 创建曝光追踪器
        exposureTracker = new ExposureTracker(recyclerView, newsList);
        
        // 设置曝光事件监听器
        exposureTracker.setExposureEventListener(new ExposureEventListener() {
            @Override
            public void onCardAppear(int position, NewsItem newsItem) {
                Log.i(TAG, String.format("📍 [曝光] 卡片露出 - 位置: %d, 标题: %s", 
                    position, newsItem.getTitle()));
                testPanel.logAppear(position, newsItem.getTitle());
            }
            
            @Override
            public void onCardHalfVisible(int position, NewsItem newsItem, float visiblePercent) {
                Log.i(TAG, String.format("📊 [曝光] 卡片50%%可见 - 位置: %d, 标题: %s, 可见度: %.2f%%", 
                    position, newsItem.getTitle(), visiblePercent * 100));
                testPanel.logHalfVisible(position, newsItem.getTitle(), visiblePercent);
            }
            
            @Override
            public void onCardFullyVisible(int position, NewsItem newsItem) {
                Log.i(TAG, String.format("✅ [曝光] 卡片完整露出 - 位置: %d, 标题: %s", 
                    position, newsItem.getTitle()));
                testPanel.logFullyVisible(position, newsItem.getTitle());
            }
            
            @Override
            public void onCardDisappear(int position, NewsItem newsItem) {
                Log.i(TAG, String.format("👋 [曝光] 卡片消失 - 位置: %d, 标题: %s", 
                    position, newsItem.getTitle()));
                testPanel.logDisappear(position, newsItem.getTitle());
            }
        });
        
        // 开始追踪
        exposureTracker.startTracking();
        
        Log.d(TAG, "✅ 卡片曝光追踪已启动");
    }
    
    /**
     * 暂停追踪
     */
    public void pauseTracking() {
        if (exposureTracker != null) {
            exposureTracker.pauseTracking();
        }
    }
    
    /**
     * 恢复追踪
     */
    public void resumeTracking() {
        if (exposureTracker != null) {
            exposureTracker.resumeTracking();
        }
    }
    
    /**
     * 停止追踪
     */
    public void stopTracking() {
        if (exposureTracker != null) {
            exposureTracker.stopTracking();
        }
    }
    
    /**
     * 获取测试面板
     */
    public ExposureTestPanel getTestPanel() {
        return testPanel;
    }
    
    /**
     * 获取曝光追踪器
     */
    public ExposureTracker getExposureTracker() {
        return exposureTracker;
    }
}
