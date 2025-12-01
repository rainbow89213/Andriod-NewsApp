// Repository 模式：管理数据来源（网络请求），提供简洁的数据访问接口
package com.example.demo2.repository;

import android.content.Context;
import android.util.Log;

import com.example.demo2.NewsItem;
import com.example.demo2.api.NewsApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * NewsRepository - 新闻数据仓库
 * 
 * Repository 模式的作用：
 * 1. 统一数据来源：封装网络请求
 * 2. 简化调用：Activity 不需要关心数据从哪里来
 * 3. 易于测试：可以轻松替换数据源
 * 
 * 数据流向：
 * MainActivity → NewsRepository → 网络请求
 *                                    ↓
 *                                Spring Boot
 *                                 (MySQL)
 * 
 * 为什么使用 Repository？
 * - 单一职责：数据访问逻辑集中管理
 * - 解耦：Activity 不直接依赖网络
 * - 灵活：可以轻松切换数据源或添加新的数据源
 */
public class NewsRepository {
    
    private static final String TAG = "NewsRepository";
    
    // 网络请求服务
    private NewsApiService apiService;
    
    // 上下文对象（用于 Toast 等）
    private Context context;
    
    // ==================== 构造方法 ====================
    
    /**
     * 构造方法（完整版）
     * 
     * @param context 上下文对象
     * @param apiService 网络请求服务
     */
    public NewsRepository(Context context, NewsApiService apiService) {
        this.context = context;
        this.apiService = apiService;
    }
    
    /**
     * 简化构造方法（自动初始化依赖）
     * 
     * @param context 上下文对象
     */
    public NewsRepository(Context context) {
        this.context = context;
        // 初始化API服务
        this.apiService = com.example.demo2.api.ApiClient.getNewsApiService();
    }
    
    
    // ==================== 网络请求操作 ====================
    
    /**
     * 从服务器加载新闻
     * 
     * 这个方法会：
     * 1. 发起网络请求到 Spring Boot 后端
     * 2. 通过回调返回结果
     * 
     * @param limit 每页数量
     * @param callback 回调接口
     * 
     * 使用示例：
     * repository.loadNewsFromServer(10, new NewsRepository.LoadCallback() {
     *     @Override
     *     public void onSuccess(List<NewsItem> newsItems) {
     *         // 更新 UI
     *     }
     *     
     *     @Override
     *     public void onFailure(String errorMsg) {
     *         // 显示错误信息
     *     }
     * });
     */
    public void loadNewsFromServer(int limit, LoadCallback callback) {
        Log.d(TAG, "🌐 从服务器加载新闻，limit=" + limit);
        
        // 发起网络请求
        Call<List<NewsItem>> call = apiService.getNewsList(null, limit);
        
        call.enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> newsItems = response.body();
                    Log.d(TAG, "✅ 服务器数据加载成功，共 " + newsItems.size() + " 条");
                    
                    // 回调成功
                    if (callback != null) {
                        callback.onSuccess(newsItems);
                    }
                } else {
                    String errorMsg = "请求失败：" + response.code();
                    Log.e(TAG, "❌ " + errorMsg);
                    
                    // 回调失败
                    if (callback != null) {
                        callback.onFailure(errorMsg);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                String errorMsg = "网络请求失败：" + t.getMessage();
                Log.e(TAG, "❌ " + errorMsg);
                
                // 回调失败
                if (callback != null) {
                    callback.onFailure(errorMsg);
                }
            }
        });
    }
    
    // ==================== 回调接口 ====================
    
    /**
     * 加载数据的回调接口
     * 
     * 用于异步返回网络请求结果
     */
    public interface LoadCallback {
        /**
         * 加载成功
         * 
         * @param newsItems 新闻列表
         */
        void onSuccess(List<NewsItem> newsItems);
        
        /**
         * 加载失败
         * 
         * @param errorMsg 错误信息
         */
        void onFailure(String errorMsg);
    }
    
    /**
     * 新闻数据回调接口（为Fragment使用）
     */
    public interface NewsCallback {
        void onSuccess(List<NewsItem> newsItems);
        void onError(String error);
    }
    
    /**
     * 获取分类新闻列表（为Fragment提供）
     * 
     * @param category 分类代码（all, tech, economy, sports等）
     * @param offset 偏移量（用于分页）
     * @param limit 每页数量
     * @param callback 回调接口
     */
    public void getNewsList(String category, int offset, int limit, NewsCallback callback) {
        Log.d(TAG, "📱 获取新闻列表 - 分类: " + category + ", offset: " + offset + ", limit: " + limit);
        
        // 根据分类选择API（移除"all"的处理）
        Call<List<NewsItem>> call;
        if (category == null || category.isEmpty()) {
            // 如果没有分类，获取所有新闻
            call = apiService.getNewsList(offset, limit);
        } else {
            // 有具体分类，获取该分类的新闻
            call = apiService.getNewsListByCategory(category, offset, limit);
        }
        
        // 执行网络请求
        call.enqueue(new retrofit2.Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, retrofit2.Response<List<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> newsItems = response.body();
                    Log.d(TAG, "✅ 获取成功: " + newsItems.size() + " 条新闻");
                    
                    // 为每个新闻项设置分类名称
                    for (NewsItem item : newsItems) {
                        if (item.getCategoryName() == null && category != null) {
                            item.setCategoryName(getCategoryDisplayName(category));
                        }
                    }
                    
                    callback.onSuccess(newsItems);
                } else {
                    String error = "获取失败: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                String error = "网络错误: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * 获取分类显示名称
     */
    private String getCategoryDisplayName(String categoryCode) {
        switch (categoryCode) {
            case "tech": return "科技";
            case "economy": return "经济";
            case "sports": return "体育";
            case "health": return "健康";
            case "entertainment": return "娱乐";
            case "education": return "教育";
            case "environment": return "环保";
            case "food": return "美食";
            default: return "其他";
        }
    }
    
}

/**
 * NewsRepository 使用指南：
 * 
 * 1. 创建 Repository：
 *    NewsRepository repository = new NewsRepository(this);
 * 
 * 2. 加载服务器数据：
 *    repository.loadNewsFromServer(10, new NewsRepository.LoadCallback() {
 *        @Override
 *        public void onSuccess(List<NewsItem> newsItems) {
 *            // 更新 UI
 *        }
 *        
 *        @Override
 *        public void onFailure(String errorMsg) {
 *            // 显示错误
 *        }
 *    });
 * 
 * 3. 获取分类新闻：
 *    repository.getNewsList("tech", 0, 10, new NewsCallback() {
 *        @Override
 *        public void onSuccess(List<NewsItem> newsItems) {
 *            // 更新 UI
 *        }
 *        
 *        @Override
 *        public void onError(String error) {
 *            // 显示错误
 *        }
 *    });
 * 
 * Repository 模式的优势：
 * 
 * 1. 简化调用：
 *    - Activity 只需要调用 Repository 的方法
 *    - 不需要关心网络请求的细节
 * 
 * 2. 统一管理：
 *    - API调用集中在 Repository
 *    - 易于修改和维护
 * 
 * 3. 易于测试：
 *    - 可以创建 Mock Repository 进行测试
 *    - 不依赖真实的网络
 * 
 * 4. 灵活扩展：
 *    - 可以轻松添加新的API端点
 *    - 可以切换不同的数据源
 */
