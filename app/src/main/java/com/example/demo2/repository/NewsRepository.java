// 【第6次修改】新建文件：数据仓库类（Repository 模式）
// 作用：统一管理数据来源（本地缓存 + 网络请求），提供简洁的数据访问接口
package com.example.demo2.repository;

import android.content.Context;
import android.util.Log;

import com.example.demo2.NewsItem;
import com.example.demo2.api.NewsApiService;
import com.example.demo2.database.AppDatabase;
import com.example.demo2.database.CachedNews;
import com.example.demo2.database.NewsDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * NewsRepository - 新闻数据仓库
 * 
 * Repository 模式的作用：
 * 1. 统一数据来源：封装本地缓存和网络请求
 * 2. 简化调用：Activity 不需要关心数据从哪里来
 * 3. 易于测试：可以轻松替换数据源
 * 4. 缓存策略：实现"缓存优先，后台更新"
 * 
 * 数据流向：
 * MainActivity → NewsRepository → 本地缓存 / 网络请求
 *                                    ↓           ↓
 *                                 Room DB    Spring Boot
 *                                 (SQLite)    (MySQL)
 * 
 * 为什么使用 Repository？
 * - 单一职责：数据访问逻辑集中管理
 * - 解耦：Activity 不直接依赖数据库和网络
 * - 灵活：可以轻松切换数据源或添加新的数据源
 */
public class NewsRepository {
    
    private static final String TAG = "NewsRepository";
    
    // 本地数据库 DAO
    private NewsDao newsDao;
    
    // 网络请求服务
    private NewsApiService apiService;
    
    // 上下文对象（用于 Toast 等）
    private Context context;
    
    // 缓存过期时间（7 天，单位：毫秒）
    private static final long CACHE_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
    
    // ==================== 构造方法 ====================
    
    /**
     * 构造方法
     * 
     * @param context 上下文对象
     * @param newsDao 新闻 DAO
     * @param apiService 网络请求服务
     */
    public NewsRepository(Context context, NewsDao newsDao, NewsApiService apiService) {
        this.context = context;
        this.newsDao = newsDao;
        this.apiService = apiService;
    }
    
    // ==================== 本地缓存操作 ====================
    
    /**
     * 从本地缓存获取新闻
     * 
     * 注意：必须在子线程调用
     * 
     * @param limit 最多返回多少条
     * @return 缓存的新闻列表
     * 
     * 使用示例：
     * new Thread(() -> {
     *     List<CachedNews> cachedNews = repository.getCachedNews(10);
     *     runOnUiThread(() -> {
     *         // 更新 UI
     *     });
     * }).start();
     */
    public List<CachedNews> getCachedNews(int limit) {
        Log.d(TAG, "📖 从本地缓存读取新闻，limit=" + limit);
        
        try {
            // 查询缓存
            List<CachedNews> cachedNews = newsDao.getAllCachedNews(limit);
            Log.d(TAG, "✅ 缓存读取成功，共 " + cachedNews.size() + " 条");
            
            // 清理过期缓存
            cleanExpiredCache();
            
            return cachedNews;
        } catch (Exception e) {
            Log.e(TAG, "❌ 缓存读取失败：" + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 保存新闻到本地缓存
     * 
     * 注意：必须在子线程调用
     * 
     * @param newsItems 要缓存的新闻列表
     * 
     * 使用示例：
     * new Thread(() -> {
     *     repository.cacheNews(newsItems);
     * }).start();
     */
    public void cacheNews(List<NewsItem> newsItems) {
        Log.d(TAG, "💾 保存新闻到本地缓存，共 " + newsItems.size() + " 条");
        
        try {
            // 转换为 CachedNews 对象
            List<CachedNews> cachedNewsList = new ArrayList<>();
            long currentTime = System.currentTimeMillis();
            
            for (NewsItem item : newsItems) {
                CachedNews cachedNews = new CachedNews(
                    item.getTitle(),
                    item.getSummary(),
                    item.getImageUrl(),
                    item.getPublishTime(),
                    item.getReadCount(),
                    currentTime  // 设置缓存时间为当前时间
                );
                cachedNewsList.add(cachedNews);
            }
            
            // 保存到数据库
            newsDao.insertNews(cachedNewsList);
            Log.d(TAG, "✅ 缓存保存成功");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ 缓存保存失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 清空所有缓存
     * 
     * 注意：必须在子线程调用
     * 
     * 使用场景：
     * - 用户点击"清空缓存"按钮
     * - 退出登录时
     */
    public void clearCache() {
        Log.d(TAG, "🗑️ 清空所有缓存");
        
        try {
            newsDao.clearAllCache();
            Log.d(TAG, "✅ 缓存清空成功");
        } catch (Exception e) {
            Log.e(TAG, "❌ 缓存清空失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 清理过期的缓存
     * 
     * 删除 7 天前的缓存数据
     * 
     * 注意：必须在子线程调用
     */
    private void cleanExpiredCache() {
        try {
            long expireTime = System.currentTimeMillis() - CACHE_EXPIRE_TIME;
            int deletedCount = newsDao.deleteExpiredCache(expireTime);
            
            if (deletedCount > 0) {
                Log.d(TAG, "🧹 清理过期缓存，删除 " + deletedCount + " 条");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ 清理过期缓存失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取缓存的新闻数量
     * 
     * 注意：必须在子线程调用
     * 
     * @return 缓存的新闻总数
     */
    public int getCacheCount() {
        try {
            int count = newsDao.getNewsCount();
            Log.d(TAG, "📊 缓存数量：" + count);
            return count;
        } catch (Exception e) {
            Log.e(TAG, "❌ 获取缓存数量失败：" + e.getMessage());
            return 0;
        }
    }
    
    // ==================== 网络请求操作 ====================
    
    /**
     * 从服务器加载新闻（并自动缓存）
     * 
     * 这个方法会：
     * 1. 发起网络请求到 Spring Boot 后端
     * 2. 成功后自动保存到本地缓存
     * 3. 通过回调返回结果
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
                    
                    // 在子线程保存到缓存
                    new Thread(() -> {
                        cacheNews(newsItems);
                    }).start();
                    
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
    
    // ==================== 工具方法 ====================
    
    /**
     * 将 CachedNews 转换为 NewsItem
     * 
     * 用于将缓存数据转换为 UI 需要的格式
     * 
     * @param cachedNews 缓存的新闻对象
     * @return NewsItem 对象
     */
    public static NewsItem convertToNewsItem(CachedNews cachedNews) {
        return new NewsItem(
            cachedNews.getTitle(),
            cachedNews.getSummary(),
            cachedNews.getImageUrl(),
            cachedNews.getPublishTime(),
            cachedNews.getReadCount()
        );
    }
    
    /**
     * 将 CachedNews 列表转换为 NewsItem 列表
     * 
     * @param cachedNewsList 缓存的新闻列表
     * @return NewsItem 列表
     */
    public static List<NewsItem> convertToNewsItems(List<CachedNews> cachedNewsList) {
        List<NewsItem> newsItems = new ArrayList<>();
        for (CachedNews cachedNews : cachedNewsList) {
            newsItems.add(convertToNewsItem(cachedNews));
        }
        return newsItems;
    }
}

/**
 * NewsRepository 使用指南：
 * 
 * 1. 创建 Repository：
 *    AppDatabase database = AppDatabase.getInstance(this);
 *    NewsRepository repository = new NewsRepository(
 *        this,
 *        database.newsDao(),
 *        RetrofitClient.getNewsApiService()
 *    );
 * 
 * 2. 加载缓存数据（子线程）：
 *    new Thread(() -> {
 *        List<CachedNews> cachedNews = repository.getCachedNews(10);
 *        runOnUiThread(() -> {
 *            // 更新 UI
 *        });
 *    }).start();
 * 
 * 3. 加载服务器数据（自动缓存）：
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
 * 4. 清空缓存（子线程）：
 *    new Thread(() -> {
 *        repository.clearCache();
 *    }).start();
 * 
 * Repository 模式的优势：
 * 
 * 1. 简化调用：
 *    - Activity 只需要调用 Repository 的方法
 *    - 不需要关心数据从哪里来（缓存还是网络）
 * 
 * 2. 统一管理：
 *    - 缓存策略集中在 Repository
 *    - 易于修改和维护
 * 
 * 3. 易于测试：
 *    - 可以创建 Mock Repository 进行测试
 *    - 不依赖真实的数据库和网络
 * 
 * 4. 灵活扩展：
 *    - 可以轻松添加新的数据源
 *    - 可以实现更复杂的缓存策略
 */
