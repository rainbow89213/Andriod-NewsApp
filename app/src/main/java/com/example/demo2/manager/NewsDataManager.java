package com.example.demo2.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.demo2.NewsAdapter;
import com.example.demo2.NewsItem;
import com.example.demo2.api.NewsApiService;
import com.example.demo2.database.AppDatabase;
import com.example.demo2.database.CachedNews;
import com.example.demo2.repository.NewsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * NewsDataManager - 新闻数据管理器
 * 
 * 职责：
 * - 管理新闻数据的加载（网络+缓存）
 * - 处理分页加载逻辑
 * - 维护各分类的数据状态
 * - 管理加载状态和错误处理
 */
public class NewsDataManager {
    
    private static final String TAG = "NewsDataManager";
    
    // 分页参数
    private static final int PAGE_SIZE = 2;
    private static final String[] CATEGORY_CODES = {
        "tech", "economy", "sports", "health", 
        "entertainment", "education", "environment", "food"
    };
    
    // 上下文
    private Context context;
    
    // 数据列表
    private List<NewsItem> newsList;
    
    // Adapter
    private NewsAdapter newsAdapter;
    
    // 数据仓库
    private NewsRepository newsRepository;
    
    // API服务
    private NewsApiService apiService;
    
    // 数据库
    private AppDatabase database;
    
    // 分类数据状态
    private Map<String, Integer> categoryOffsetMap = new HashMap<>();
    private Map<String, Boolean> categoryHasMoreMap = new HashMap<>();
    private Map<String, List<NewsItem>> categoryDataMap = new HashMap<>();
    
    // 加载状态
    private boolean isLoadingMore = false;
    private boolean isRefreshing = false;
    
    // 数据加载监听器
    private OnDataLoadListener dataLoadListener;
    
    /**
     * 数据加载监听接口
     */
    public interface OnDataLoadListener {
        void onLoadStart();
        void onLoadSuccess(int count);
        void onLoadError(String message);
        void onLoadComplete();
    }
    
    /**
     * 构造函数
     */
    public NewsDataManager(Context context, List<NewsItem> newsList, NewsAdapter newsAdapter,
                          AppDatabase database, NewsApiService apiService) {
        this.context = context;
        this.newsList = newsList;
        this.newsAdapter = newsAdapter;
        this.database = database;
        this.apiService = apiService;
        this.newsRepository = new NewsRepository(context, database.newsDao(), apiService);
    }
    
    /**
     * 初始化所有分类的数据
     */
    public void loadInitialDataForAllCategories() {
        Log.d(TAG, "========== 开始初始化所有分类数据 ==========");
        
        // 先加载"全部"分类（汇总所有分类的第一页）
        loadAllCategoriesSummary();
    }
    
    /**
     * 加载"全部"分类数据（汇总所有分类）
     */
    private void loadAllCategoriesSummary() {
        Log.d(TAG, "🔄 开始加载"全部"分类数据...");
        
        if (dataLoadListener != null) {
            dataLoadListener.onLoadStart();
        }
        
        List<NewsItem> allNews = new ArrayList<>();
        int[] loadedCount = {0};
        int totalCategories = CATEGORY_CODES.length;
        
        // 为每个分类加载第一页
        for (String category : CATEGORY_CODES) {
            loadCategoryData(category, 0, PAGE_SIZE, new Callback<List<NewsItem>>() {
                @Override
                public void onResponse(@NonNull Call<List<NewsItem>> call, 
                                      @NonNull Response<List<NewsItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<NewsItem> categoryNews = response.body();
                        allNews.addAll(categoryNews);
                        
                        // 保存分类数据
                        categoryDataMap.put(category, new ArrayList<>(categoryNews));
                        categoryOffsetMap.put(category, PAGE_SIZE);
                        categoryHasMoreMap.put(category, !categoryNews.isEmpty());
                    }
                    
                    loadedCount[0]++;
                    
                    // 所有分类加载完成
                    if (loadedCount[0] == totalCategories) {
                        updateUIWithData(allNews);
                        if (dataLoadListener != null) {
                            dataLoadListener.onLoadSuccess(allNews.size());
                            dataLoadListener.onLoadComplete();
                        }
                    }
                }
                
                @Override
                public void onFailure(@NonNull Call<List<NewsItem>> call, @NonNull Throwable t) {
                    Log.e(TAG, "❌ 加载分类 " + category + " 失败: " + t.getMessage());
                    loadedCount[0]++;
                    
                    if (loadedCount[0] == totalCategories) {
                        if (allNews.isEmpty()) {
                            loadCachedData();
                        } else {
                            updateUIWithData(allNews);
                        }
                        if (dataLoadListener != null) {
                            dataLoadListener.onLoadComplete();
                        }
                    }
                }
            });
        }
    }
    
    /**
     * 加载指定分类的数据
     */
    public void loadCategoryData(String category, int offset, int limit, 
                                 Callback<List<NewsItem>> callback) {
        Call<List<NewsItem>> call = apiService.getNewsByCategory(category, offset, limit);
        call.enqueue(callback);
    }
    
    /**
     * 切换分类
     */
    public void switchCategory(String category) {
        Log.d(TAG, "🔄 切换到分类: " + (category == null ? "全部" : category));
        
        if (category == null) {
            // 切换到"全部"分类
            loadAllCategoriesSummary();
        } else {
            // 切换到具体分类
            if (categoryDataMap.containsKey(category)) {
                // 如果已有缓存数据，直接显示
                List<NewsItem> cachedData = categoryDataMap.get(category);
                updateUIWithData(cachedData);
            } else {
                // 否则加载新数据
                loadSingleCategory(category);
            }
        }
    }
    
    /**
     * 加载单个分类的数据
     */
    private void loadSingleCategory(String category) {
        if (dataLoadListener != null) {
            dataLoadListener.onLoadStart();
        }
        
        loadCategoryData(category, 0, PAGE_SIZE, new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsItem>> call, 
                                  @NonNull Response<List<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> categoryNews = response.body();
                    
                    // 保存数据
                    categoryDataMap.put(category, new ArrayList<>(categoryNews));
                    categoryOffsetMap.put(category, PAGE_SIZE);
                    categoryHasMoreMap.put(category, !categoryNews.isEmpty());
                    
                    updateUIWithData(categoryNews);
                    
                    if (dataLoadListener != null) {
                        dataLoadListener.onLoadSuccess(categoryNews.size());
                        dataLoadListener.onLoadComplete();
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<List<NewsItem>> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ 加载失败: " + t.getMessage());
                if (dataLoadListener != null) {
                    dataLoadListener.onLoadError(t.getMessage());
                    dataLoadListener.onLoadComplete();
                }
            }
        });
    }
    
    /**
     * 加载更多数据
     */
    public void loadMoreNews(String currentCategory) {
        if (isLoadingMore) {
            Log.d(TAG, "⚠️ 正在加载中，请稍候...");
            return;
        }
        
        isLoadingMore = true;
        
        if (newsAdapter != null) {
            newsAdapter.setLoadingState(true, true);
        }
        
        Log.d(TAG, "========== 开始加载更多 ==========");
        
        if (currentCategory == null) {
            // "全部"分类 - 继续加载各分类数据
            loadMoreForAllCategories();
        } else {
            // 具体分类 - 加载该分类的更多数据
            loadMoreForCategory(currentCategory);
        }
    }
    
    /**
     * 为"全部"分类加载更多
     */
    private void loadMoreForAllCategories() {
        // 简化：只加载第一个还有数据的分类
        for (String category : CATEGORY_CODES) {
            Boolean hasMore = categoryHasMoreMap.get(category);
            if (hasMore != null && hasMore) {
                loadMoreForCategory(category);
                return;
            }
        }
        
        // 没有更多数据
        isLoadingMore = false;
        if (newsAdapter != null) {
            newsAdapter.setLoadingState(false, false);
        }
        Toast.makeText(context, "没有更多数据了", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 为指定分类加载更多
     */
    private void loadMoreForCategory(String category) {
        Integer offset = categoryOffsetMap.get(category);
        if (offset == null) offset = 0;
        
        final int currentOffset = offset;
        
        loadCategoryData(category, currentOffset, PAGE_SIZE, new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsItem>> call, 
                                  @NonNull Response<List<NewsItem>> response) {
                isLoadingMore = false;
                
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> moreNews = response.body();
                    
                    if (!moreNews.isEmpty()) {
                        // 添加到数据列表
                        newsList.addAll(moreNews);
                        newsAdapter.notifyDataSetChanged();
                        
                        // 更新状态
                        categoryOffsetMap.put(category, currentOffset + PAGE_SIZE);
                        categoryHasMoreMap.put(category, true);
                        
                        if (newsAdapter != null) {
                            newsAdapter.setLoadingState(false, true);
                        }
                        
                        Log.d(TAG, "✅ 加载更多成功: " + moreNews.size() + " 条");
                    } else {
                        // 没有更多数据
                        categoryHasMoreMap.put(category, false);
                        if (newsAdapter != null) {
                            newsAdapter.setLoadingState(false, false);
                        }
                        Toast.makeText(context, "没有更多数据了", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<List<NewsItem>> call, @NonNull Throwable t) {
                isLoadingMore = false;
                Log.e(TAG, "❌ 加载更多失败: " + t.getMessage());
                
                if (newsAdapter != null) {
                    newsAdapter.setLoadingState(false, true);
                }
                
                Toast.makeText(context, "加载失败: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 加载缓存数据
     */
    private void loadCachedData() {
        new Thread(() -> {
            List<CachedNews> cachedNewsList = newsRepository.getCachedNews(10);
            
            if (!cachedNewsList.isEmpty()) {
                List<NewsItem> cachedNewsItems = 
                    NewsRepository.convertToNewsItems(cachedNewsList);
                
                ((android.app.Activity) context).runOnUiThread(() -> {
                    updateUIWithData(cachedNewsItems);
                    Toast.makeText(context, "显示缓存数据", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    /**
     * 更新UI显示数据
     */
    private void updateUIWithData(List<NewsItem> data) {
        newsList.clear();
        newsList.addAll(data);
        if (newsAdapter != null) {
            newsAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "✅ UI已更新，共 " + data.size() + " 条数据");
    }
    
    /**
     * 刷新当前分类数据
     */
    public void refreshCurrentCategory(String category) {
        isRefreshing = true;
        
        if (category == null) {
            loadAllCategoriesSummary();
        } else {
            // 重置offset，重新加载
            categoryOffsetMap.put(category, 0);
            loadSingleCategory(category);
        }
        
        isRefreshing = false;
    }
    
    /**
     * 设置数据加载监听器
     */
    public void setOnDataLoadListener(OnDataLoadListener listener) {
        this.dataLoadListener = listener;
    }
    
    /**
     * 判断是否正在加载
     */
    public boolean isLoading() {
        return isLoadingMore || isRefreshing;
    }
    
    /**
     * 获取数据仓库
     */
    public NewsRepository getNewsRepository() {
        return newsRepository;
    }
}
