package com.example.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo2.api.NewsApiService;
import com.example.demo2.api.RetrofitClient;
import com.example.demo2.database.AppDatabase;
import com.example.demo2.manager.CategoryManager;
import com.example.demo2.manager.ExposureManager;
import com.example.demo2.manager.LayoutModeManager;
import com.example.demo2.manager.NewsDataManager;
import com.example.demo2.manager.ScrollManager;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity - 重构后的主界面（简化版）
 * 
 * 重构策略：
 * - 使用管理器模式分离职责
 * - 减少MainActivity的代码量
 * - 提高代码可维护性和可测试性
 * 
 * 管理器列表：
 * - CategoryManager: 分类标签管理
 * - LayoutModeManager: 布局模式切换
 * - NewsDataManager: 数据加载和分页
 * - ScrollManager: 滚动和下拉刷新
 * - ExposureManager: 曝光追踪和测试
 */
public class MainActivityRefactored extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    // UI组件
    private RecyclerView recyclerView;
    private LinearLayout categoryContainer;
    private ImageButton layoutSwitchButton;
    
    // 数据
    private List<NewsItem> newsList;
    private NewsAdapter newsAdapter;
    
    // 管理器
    private CategoryManager categoryManager;
    private LayoutModeManager layoutModeManager;
    private NewsDataManager newsDataManager;
    private ScrollManager scrollManager;
    private ExposureManager exposureManager;
    
    // 数据库和API
    private AppDatabase database;
    private NewsApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "========== MainActivity onCreate 开始 ==========");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 设置窗口插入监听器
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // 1. 初始化基础组件
        initComponents();
        
        // 2. 初始化RecyclerView
        initRecyclerView();
        
        // 3. 初始化管理器
        initManagers();
        
        // 4. 加载初始数据
        newsDataManager.loadInitialDataForAllCategories();
        
        Log.d(TAG, "========== MainActivity onCreate 完成 ==========");
    }
    
    /**
     * 初始化基础组件
     */
    private void initComponents() {
        // 初始化数据库和API
        database = AppDatabase.getInstance(this);
        apiService = RetrofitClient.getNewsApiService();
        
        // 获取UI组件
        recyclerView = findViewById(R.id.recyclerView);
        categoryContainer = findViewById(R.id.categoryContainer);
        layoutSwitchButton = findViewById(R.id.layoutSwitchButton);
        
        Log.d(TAG, "✅ 基础组件初始化完成");
    }
    
    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        // 初始化数据列表
        newsList = new ArrayList<>();
        
        // 创建适配器
        newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setHasFixedSize(true);
        
        // 初始化加载更多卡片状态
        newsAdapter.setShowLoadMore(true);
        newsAdapter.setHasMoreData(true);
        
        // 设置删除监听
        newsAdapter.setOnItemDeleteListener(position -> {
            if (position >= 0 && position < newsList.size()) {
                NewsItem deletedItem = newsList.get(position);
                newsList.remove(position);
                newsAdapter.notifyItemRemoved(position);
                Toast.makeText(this, "已删除: " + deletedItem.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        
        Log.d(TAG, "✅ RecyclerView初始化完成");
    }
    
    /**
     * 初始化所有管理器
     */
    private void initManagers() {
        // 1. 初始化分类管理器
        categoryManager = new CategoryManager(this, categoryContainer);
        categoryManager.initCategoryTabs();
        categoryManager.setOnCategoryChangeListener(category -> {
            Log.d(TAG, "📑 分类切换: " + (category == null ? "[全部]" : category));
            newsDataManager.switchCategory(category);
        });
        
        // 2. 初始化布局模式管理器
        layoutModeManager = new LayoutModeManager(this, recyclerView, newsAdapter, layoutSwitchButton);
        layoutModeManager.initLayoutSwitchButton();
        layoutModeManager.setOnLayoutModeChangeListener(newMode -> {
            Log.d(TAG, "🔄 布局模式切换: " + (newMode == LayoutModeManager.LAYOUT_MODE_GRID ? "[双列]" : "[单列]"));
        });
        
        // 3. 初始化数据管理器
        newsDataManager = new NewsDataManager(this, newsList, newsAdapter, database, apiService);
        newsDataManager.setOnDataLoadListener(new NewsDataManager.OnDataLoadListener() {
            @Override
            public void onLoadStart() {
                Log.d(TAG, "⏳ 开始加载数据...");
            }
            
            @Override
            public void onLoadSuccess(int count) {
                Log.d(TAG, "✅ 数据加载成功: " + count + " 条");
            }
            
            @Override
            public void onLoadError(String message) {
                Log.e(TAG, "❌ 数据加载失败: " + message);
                Toast.makeText(MainActivityRefactored.this, "加载失败: " + message, Toast.LENGTH_SHORT).show();
                // 加载失败时也要重置自动加载标志
                if (scrollManager != null) {
                    scrollManager.resetAutoLoadFlag();
                }
            }
            
            @Override
            public void onLoadComplete() {
                Log.d(TAG, "🏁 数据加载完成");
                // 加载完成后重置自动加载标志
                if (scrollManager != null) {
                    scrollManager.resetAutoLoadFlag();
                }
            }
        });
        
        // 设置加载更多监听
        newsAdapter.setOnLoadMoreClickListener(() -> {
            String currentCategory = categoryManager.getCurrentCategory();
            newsDataManager.loadMoreNews(currentCategory);
        });
        
        // 4. 初始化滚动管理器
        scrollManager = new ScrollManager(this, recyclerView, findViewById(R.id.customScrollbar));
        scrollManager.initPullToRefresh();
        scrollManager.initCustomScrollbar();
        scrollManager.setOnPullRefreshListener(() -> {
            Log.d(TAG, "🔄 下拉刷新触发");
            String currentCategory = categoryManager.getCurrentCategory();
            newsDataManager.refreshCurrentCategory(currentCategory);
        });
        
        // 设置自动加载监听器
        Log.d(TAG, "🔧 设置自动加载监听器...");
        scrollManager.setOnAutoLoadListener(new ScrollManager.OnAutoLoadListener() {
            @Override
            public void onAutoLoad() {
                Log.d(TAG, "📤 自动加载触发 - MainActivityRefactored.onAutoLoad()");
                String currentCategory = categoryManager.getCurrentCategory();
                Log.d(TAG, "  - 当前分类: " + (currentCategory == null ? "[全部]" : currentCategory));
                Log.d(TAG, "  - 调用newsDataManager.loadMoreNews()");
                newsDataManager.loadMoreNews(currentCategory);
            }
            
            @Override
            public boolean hasMoreData() {
                boolean result = newsDataManager.hasMoreData();
                Log.d(TAG, "📊 hasMoreData() 返回: " + result);
                return result;
            }
            
            @Override
            public boolean isLoadingMore() {
                boolean result = newsDataManager.isLoadingMore();
                Log.d(TAG, "📊 isLoadingMore() 返回: " + result);
                return result;
            }
            
            @Override
            public void setLoading(boolean loading) {
                Log.d(TAG, "⚙️ setLoading(" + loading + ")");
                newsAdapter.setLoading(loading);
            }
        });
        Log.d(TAG, "✅ 自动加载监听器设置完成");
        
        // 5. 初始化曝光管理器
        exposureManager = new ExposureManager(this, recyclerView, newsList);
        FrameLayout testPanelContainer = findViewById(R.id.testPanelContainer);
        exposureManager.initExposureTracker(testPanelContainer);
        
        Log.d(TAG, "✅ 所有管理器初始化完成");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (exposureManager != null) {
            exposureManager.resumeTracking();
        }
        Log.d(TAG, "📱 onResume - 曝光追踪已恢复");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (exposureManager != null) {
            exposureManager.pauseTracking();
        }
        Log.d(TAG, "📱 onPause - 曝光追踪已暂停");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exposureManager != null) {
            exposureManager.stopTracking();
        }
        Log.d(TAG, "📱 onDestroy - 资源已释放");
    }
}
