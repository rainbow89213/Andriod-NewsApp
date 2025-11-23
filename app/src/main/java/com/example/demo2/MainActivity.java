// 包名声明：定义这个类所属的包
// 包名必须与 AndroidManifest.xml 中声明的包名一致
package com.example.demo2;

// 导入 Android SDK 的类
import android.os.Bundle;  // Bundle 用于保存和恢复 Activity 状态
import android.util.Log;  // 日志工具类
import android.widget.Toast;  // Toast 提示

// 导入 AndroidX 库的类
import androidx.activity.EdgeToEdge;  // 边到边显示功能（全屏显示）
import androidx.annotation.NonNull;  // NonNull 注解
import androidx.appcompat.app.AppCompatActivity;  // 向后兼容的 Activity 基类
import androidx.core.graphics.Insets;  // 表示屏幕边缘的插入区域
import androidx.core.view.ViewCompat;  // 视图兼容性工具类
import androidx.core.view.WindowInsetsCompat;  // 窗口插入区域兼容类
import androidx.recyclerview.widget.LinearLayoutManager;  // 线性布局管理器
import androidx.recyclerview.widget.GridLayoutManager;  // 网格布局管理器
import androidx.recyclerview.widget.RecyclerView;  // RecyclerView 组件

// 导入 Handler 用于定时任务
import android.os.Handler;
import android.os.Looper;

// 【第8次修改】导入分类标签和滚动条相关类
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.graphics.Color;

// 导入 Java 工具类
import java.util.ArrayList;  // 动态数组列表
import java.util.List;  // 列表接口

// 导入网络请求相关类
import com.example.demo2.api.RetrofitClient;
import com.example.demo2.api.NewsApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 【第6次修改】导入 Room 数据库相关类
import com.example.demo2.database.AppDatabase;
import com.example.demo2.database.CachedNews;
import com.example.demo2.repository.NewsRepository;

/**
 * MainActivity - 应用的主界面 Activity
 * 
 * Activity 是 Android 的四大组件之一，代表一个屏幕界面
 * 这是应用启动时第一个显示的界面
 * 
 * 继承关系：
 * MainActivity -> AppCompatActivity -> Activity
 * 
 * AppCompatActivity 提供了向后兼容的功能，让新版本的特性可以在旧设备上运行
 */
public class MainActivity extends AppCompatActivity {
    
    // 日志标签
    private static final String TAG = "MainActivity";

    // RecyclerView 组件：用于显示新闻列表
    private RecyclerView recyclerView;
    
    // 适配器：连接数据和 RecyclerView
    private NewsAdapter newsAdapter;
    
    // 新闻数据列表
    private List<NewsItem> newsList;
    
    // API 服务
    private NewsApiService apiService;
    
    // 【第6次修改】Room 数据库和数据仓库
    private AppDatabase database;
    private NewsRepository newsRepository;
    
    // 是否正在刷新
    private boolean isRefreshing = false;
    
    // 【第10次修改】下拉刷新相关变量
    private float pullDownStartY = 0;  // 下拉开始的Y坐标
    private boolean isPullingDown = false;  // 是否正在下拉
    private static final int PULL_THRESHOLD = 200;  // 下拉阈值（像素）
    
    // 【第8次修改】分类相关变量
    private LinearLayout categoryContainer;
    private String currentCategory = null;  // 当前选中的分类（null表示全部）
    private List<TextView> categoryTabs = new ArrayList<>();  // 分类标签列表
    
    // 【第8次修改】自定义滚动条相关变量
    private View customScrollbar;
    private boolean isDraggingScrollbar = false;
    private float scrollbarDragStartY = 0;
    private int scrollbarInitialTop = 0;
    
    // 【第13次修改】分页加载相关变量
    private int currentOffset = 0;  // 当前偏移量
    private static final int PAGE_SIZE = 2;  // 每页数量（改为2条以便测试）
    private boolean isLoadingMore = false;  // 是否正在加载更多
    private boolean hasMoreData = true;  // 是否还有更多数据
    
    // 【第14次修改】为每个分类单独维护状态
    private java.util.Map<String, Integer> categoryOffsetMap = new java.util.HashMap<>();  // 每个分类的offset
    private java.util.Map<String, Boolean> categoryHasMoreMap = new java.util.HashMap<>();  // 每个分类是否还有更多数据
    private java.util.Map<String, List<NewsItem>> categoryDataMap = new java.util.HashMap<>();  // 每个分类的数据列表
    
    // 【第15次修改】具体分类列表（用于"全部"板块汇总）
    private static final String[] CATEGORY_CODES = {"tech", "economy", "sports", "health", "entertainment", "education", "environment", "food"};
    
    // 【第16次修改】布局模式切换
    private static final int LAYOUT_MODE_SINGLE = 1;  // 单列模式
    private static final int LAYOUT_MODE_GRID = 2;    // 双列模式
    private int currentLayoutMode = LAYOUT_MODE_SINGLE;  // 默认单列
    private ImageButton layoutSwitchButton;
    
    // 【第17次修改】卡片曝光追踪
    private ExposureTracker exposureTracker;
    
    // 【第19次修改】曝光事件测试面板
    private ExposureTestPanel testPanel;

    /**
     * onCreate() - Activity 的创建方法
     * 
     * 这是 Activity 生命周期的第一个方法，在 Activity 创建时调用
     * 
     * Activity 生命周期：
     * onCreate() -> onStart() -> onResume() -> 运行中
     * -> onPause() -> onStop() -> onDestroy()
     * 
     * @param savedInstanceState 保存的实例状态
     *                          如果 Activity 被系统销毁后重建，这里会包含之前保存的数据
     *                          首次创建时为 null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "========== MainActivity onCreate 开始 ==========");
        
        // 调用父类的 onCreate 方法（必须调用）
        super.onCreate(savedInstanceState);
        
        // 启用边到边显示（Edge-to-Edge）
        // 让内容可以延伸到状态栏和导航栏下方，实现沉浸式体验
        EdgeToEdge.enable(this);
        Log.d(TAG, "EdgeToEdge 已启用");
        
        // 设置 Activity 的布局文件
        // R.layout.activity_main 引用 res/layout/activity_main.xml 文件
        // R 是自动生成的资源类，包含所有资源的 ID
        setContentView(R.layout.activity_main);
        Log.d(TAG, "布局文件已设置: activity_main.xml");
        
        // 设置窗口插入监听器，处理系统栏（状态栏、导航栏）的边距
        // findViewById(R.id.main) 查找布局中 ID 为 main 的视图
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // 获取系统栏（状态栏和导航栏）的插入区域
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // 设置视图的内边距，避免内容被系统栏遮挡
            // left, top, right, bottom 分别对应左、上、右、下的边距
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            
            // 返回处理后的 insets
            return insets;
        });
        Log.d(TAG, "窗口插入监听器已设置");
        
        // 【第8次修改】初始化分类标签栏
        Log.d(TAG, "准备初始化分类标签栏...");
        initCategoryTabs();
        
        // 【第16次修改】初始化布局切换按钮
        Log.d(TAG, "准备初始化布局切换按钮...");
        initLayoutSwitchButton();
        
        // 初始化 RecyclerView
        Log.d(TAG, "准备初始化 RecyclerView...");
        initRecyclerView();
        
        // 【第8次修改】初始化自定义滚动条
        Log.d(TAG, "准备初始化自定义滚动条...");
        initCustomScrollbar();
        
        // 初始化手动刷新监听
        Log.d(TAG, "准备初始化手动刷新监听...");
        initPullToRefresh();
        
        // 【第11次修改】初始化加载更多文本
        Log.d(TAG, "准备初始化加载更多文本...");
        initLoadMoreText();
        
        // 初始化 API 服务
        Log.d(TAG, "准备初始化 API 服务...");
        apiService = RetrofitClient.getNewsApiService();
        Log.d(TAG, "API 服务初始化完成");
        
        // 【第6次修改】初始化数据库和数据仓库
        Log.d(TAG, "准备初始化数据库...");
        database = AppDatabase.getInstance(this);
        newsRepository = new NewsRepository(this, database.newsDao(), apiService);
        Log.d(TAG, "数据库和数据仓库初始化完成");
        
        // 【第15次修改】初始加载：为所有分类加载第一页数据
        Log.d(TAG, "准备初始化所有分类数据...");
        loadInitialDataForAllCategories();
        
        // 【第17次修改】初始化卡片曝光追踪
        Log.d(TAG, "准备初始化卡片曝光追踪...");
        initExposureTracker();
        
        Log.d(TAG, "========== MainActivity onCreate 完成 ==========");
    }
    
    /**
     * initRecyclerView - 初始化 RecyclerView
     * 
     * 设置 RecyclerView 的布局管理器和适配器
     */
    private void initRecyclerView() {
        Log.d(TAG, "---------- initRecyclerView 开始 ----------");
        
        // 1. 获取 RecyclerView 组件
        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            Log.e(TAG, "❌ RecyclerView 为 null！检查布局文件中是否有 id=recyclerView");
            return;
        }
        Log.d(TAG, "✅ RecyclerView 获取成功");
        
        // 2. 创建线性布局管理器
        // LinearLayoutManager：垂直线性布局，从上到下排列
        // 其他选项：GridLayoutManager（网格布局）、StaggeredGridLayoutManager（瀑布流）
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Log.d(TAG, "✅ LayoutManager 设置完成");
        
        // 3. 初始化数据列表
        newsList = new ArrayList<>();
        Log.d(TAG, "✅ 数据列表初始化完成，当前大小: " + newsList.size());
        
        // 4. 创建适配器
        newsAdapter = new NewsAdapter(newsList);
        Log.d(TAG, "✅ NewsAdapter 创建完成");
        
        // 【第12次修改】设置加载更多监听器
        newsAdapter.setOnLoadMoreClickListener(() -> {
            Log.d(TAG, "🔘 用户点击加载更多卡片");
            loadMoreNews();
        });
        
        // 【第12次修改】设置删除监听器
        newsAdapter.setOnItemDeleteListener(position -> {
            Log.d(TAG, "🗑️ 删除位置: " + position);
            if (position >= 0 && position < newsList.size()) {
                NewsItem deletedItem = newsList.get(position);
                newsList.remove(position);
                newsAdapter.notifyItemRemoved(position);
                Toast.makeText(this, "已删除：" + deletedItem.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        
        // 5. 设置适配器到 RecyclerView
        recyclerView.setAdapter(newsAdapter);
        Log.d(TAG, "✅ Adapter 设置到 RecyclerView");
        
        // 可选：设置固定大小优化性能
        // 如果 RecyclerView 的大小不会因为内容改变而改变，设置为 true 可以提高性能
        recyclerView.setHasFixedSize(true);
        Log.d(TAG, "✅ RecyclerView 固定大小已设置");
        
        Log.d(TAG, "---------- initRecyclerView 完成 ----------");
    }
    
    /**
     * 【第6次修改】新方法：先加载缓存，再请求网络
     * 
     * 数据加载策略：
     * 1. 先从本地缓存读取数据（快速显示）
     * 2. 同时发起网络请求（获取最新数据）
     * 3. 网络请求成功后更新缓存和 UI
     * 4. 网络请求失败时提示用户正在使用缓存数据
     * 
     * 优势：
     * - 秒开体验：立即显示缓存数据
     * - 离线可用：无网络时也能看到缓存的新闻
     * - 自动更新：后台获取最新数据
     */
    private void loadNewsWithCache() {
        Log.d(TAG, "========== 开始加载数据（缓存优先）==========");
        
        // 1. 先从本地缓存加载（在子线程）
        new Thread(() -> {
            Log.d(TAG, "📖 步骤1：从本地缓存读取数据...");
            List<CachedNews> cachedNewsList = newsRepository.getCachedNews(10);
            
            if (!cachedNewsList.isEmpty()) {
                Log.d(TAG, "✅ 缓存读取成功，共 " + cachedNewsList.size() + " 条");
                
                // 转换为 NewsItem 格式
                List<NewsItem> cachedNewsItems = NewsRepository.convertToNewsItems(cachedNewsList);
                
                // 在主线程更新 UI
                runOnUiThread(() -> {
                    Log.d(TAG, "🎨 更新 UI（显示缓存数据）");
                    newsList.clear();
                    newsList.addAll(cachedNewsItems);
                    newsAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, 
                        "📖 显示缓存数据（" + cachedNewsItems.size() + " 条）", 
                        Toast.LENGTH_SHORT).show();
                });
            } else {
                Log.d(TAG, "ℹ️ 缓存为空，等待网络数据...");
            }
        }).start();
        
        // 2. 同时从服务器加载最新数据
        Log.d(TAG, "🌐 步骤2：从服务器加载最新数据...");
        loadNewsFromServer();
    }
    
    /**
     * loadNewsFromServer - 从服务器加载新闻数据
     * 
     * 【第9次修改】修改为分页加载：
     * - 初始加载：offset=0, limit=10
     * - 加载更多：offset递增, limit=10
     * - 成功后自动保存到缓存
     * 
     * 使用 Retrofit 进行异步网络请求
     */
    private void loadNewsFromServer() {
        loadNewsFromServer(false);  // 默认不是加载更多
    }
    
    /**
     * 【第9次修改】从服务器加载新闻数据（支持加载更多）
     * 
     * @param isLoadMore true表示加载更多，false表示刷新
     */
    private void loadNewsFromServer(boolean isLoadMore) {
        isLoadingMore = isLoadMore;
        
        Log.d(TAG, "\n==========================================");
        Log.d(TAG, "📡 开始网络请求");
        Log.d(TAG, "==========================================");
        Log.d(TAG, "📝 请求参数：");
        Log.d(TAG, "  - 加载模式: " + (isLoadMore ? "加载更多" : "刷新"));
        Log.d(TAG, "  - 当前分类: " + (currentCategory == null ? "全部" : currentCategory));
        Log.d(TAG, "  - 当前offset: " + currentOffset);
        Log.d(TAG, "  - 请求数量(limit): " + PAGE_SIZE);
        Log.d(TAG, "  - isLoadingMore标志: " + isLoadingMore);
        Log.d(TAG, "  - hasMoreData标志: " + hasMoreData);
        
        // 根据当前分类构造请求
        Call<List<NewsItem>> call;
        if (currentCategory == null) {
            // 全部分类
            call = apiService.getNewsList(currentOffset, PAGE_SIZE);
            Log.d(TAG, "请求全部分类的新闻，offset=" + currentOffset + ", limit=" + PAGE_SIZE);
        } else {
            // 指定分类
            call = apiService.getNewsListByCategory(currentCategory, currentOffset, PAGE_SIZE);
            Log.d(TAG, "请求分类: " + currentCategory + ", offset=" + currentOffset + ", limit=" + PAGE_SIZE);
        }
        Log.d(TAG, "Retrofit Call 对象已创建: " + call.request().url());
        
        call.enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                Log.d(TAG, "========== 收到服务器响应 ==========");
                Log.d(TAG, "响应码: " + response.code());
                Log.d(TAG, "响应消息: " + response.message());
                Log.d(TAG, "是否成功: " + response.isSuccessful());
                
                // 请求成功
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> newsItems = response.body();
                    Log.d(TAG, "✅ 加载成功！获取到 " + newsItems.size() + " 条新闻");
                    
                    // 打印每条新闻的标题（用于调试）
                    for (int i = 0; i < newsItems.size(); i++) {
                        Log.d(TAG, "新闻 " + (i+1) + ": " + newsItems.get(i).getTitle());
                    }
                    
                    // 【第9次修改】根据加载模式更新数据
                    Log.d(TAG, "开始更新RecyclerView数据...");
                    if (isLoadMore) {
                        // 加载更多：追加数据
                        int oldSize = newsList.size();
                        newsList.addAll(newsItems);
                        newsAdapter.notifyItemRangeInserted(oldSize, newsItems.size());
                        Log.d(TAG, "追加 " + newsItems.size() + " 条新闻，总数: " + newsList.size());
                    } else {
                        // 刷新：替换数据
                        newsList.clear();
                        newsList.addAll(newsItems);
                        newsAdapter.notifyDataSetChanged();
                        Log.d(TAG, "替换数据，总数: " + newsList.size());
                    }
                    
                    // 【第13次修改】更新分页状态和加载更多卡片显示
                    Log.d(TAG, "\n------------------------------------------");
                    Log.d(TAG, "📊 判断分页状态");
                    Log.d(TAG, "------------------------------------------");
                    Log.d(TAG, "📝 判断条件：");
                    Log.d(TAG, "  - 返回数据量: " + newsItems.size());
                    Log.d(TAG, "  - PAGE_SIZE: " + PAGE_SIZE);
                    Log.d(TAG, "  - 判断结果: " + newsItems.size() + " < " + PAGE_SIZE + " = " + (newsItems.size() < PAGE_SIZE));
                    Log.d(TAG, "  - 加载前offset: " + (currentOffset - (isLoadMore ? 0 : currentOffset)));
                    
                    if (newsItems.size() < PAGE_SIZE) {
                        hasMoreData = false;  // 没有更多数据了
                        newsAdapter.setHasMoreData(false);  // 同步状态到Adapter
                        newsAdapter.setShowLoadMore(true);  // 仍然显示卡片，但文本变为"已加载全部数据"
                        Log.d(TAG, "\n⚠️ 返回数据不足，已加载全部数据");
                        Log.d(TAG, "📌 更新状态：");
                        Log.d(TAG, "  - hasMoreData: " + hasMoreData);
                        Log.d(TAG, "  - currentOffset: " + currentOffset + " (不变)");
                        Log.d(TAG, "  - 总数据量: " + newsList.size());
                        Log.d(TAG, "  - 卡片显示: 已加载全部数据");
                    } else {
                        int oldOffset = currentOffset;
                        currentOffset += newsItems.size();  // 更新offset
                        hasMoreData = true;
                        newsAdapter.setHasMoreData(true);  // 同步状态到Adapter
                        newsAdapter.setShowLoadMore(true);  // 显示加载更多卡片
                        Log.d(TAG, "\n✅ 还有更多数据可加载");
                        Log.d(TAG, "📌 更新状态：");
                        Log.d(TAG, "  - hasMoreData: " + hasMoreData);
                        Log.d(TAG, "  - currentOffset: " + oldOffset + " → " + currentOffset + " (+" + newsItems.size() + ")");
                        Log.d(TAG, "  - 总数据量: " + newsList.size());
                        Log.d(TAG, "  - 卡片显示: 点击加载更多");
                    }
                    Log.d(TAG, "------------------------------------------\n");
                    
                    // 【第15次修改】更新分类数据Map（用于"全部"板块汇总）
                    if (currentCategory != null) {
                        // 当前是具体分类，更新该分类的数据
                        categoryDataMap.put(currentCategory, new ArrayList<>(newsList));
                        categoryOffsetMap.put(currentCategory, currentOffset);
                        categoryHasMoreMap.put(currentCategory, hasMoreData);
                        Log.d(TAG, "💾 更新【" + currentCategory + "】数据到Map: " + newsList.size() + " 条");
                    }
                    
                    // 【第6次修改】保存到本地缓存（在子线程）
                    if (!isLoadMore) {  // 只在刷新时保存缓存
                        new Thread(() -> {
                            Log.d(TAG, "💾 保存数据到本地缓存...");
                            newsRepository.cacheNews(newsItems);
                            Log.d(TAG, "✅ 缓存保存完成");
                        }).start();
                    }
                    
                    String message = isLoadMore ? 
                        "✅ 加载更多成功！获取 " + newsItems.size() + " 条新闻" :
                        "✅ 刷新成功！获取 " + newsItems.size() + " 条新闻";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "❌ 请求失败");
                    Log.e(TAG, "响应码: " + response.code());
                    Log.e(TAG, "错误信息: " + response.message());
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "错误详情: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "无法读取错误详情", e);
                    }
                    
                    Toast.makeText(MainActivity.this, 
                        "❌ 加载失败：" + response.code(), 
                        Toast.LENGTH_SHORT).show();
                }
                
                // 【第13次修改】刷新或加载完成，恢复加载中状态
                Log.d(TAG, "\n🔄 恢复加载状态标志");
                Log.d(TAG, "  - isRefreshing: " + isRefreshing + " → false");
                Log.d(TAG, "  - isLoadingMore: " + isLoadingMore + " → false");
                isRefreshing = false;
                isLoadingMore = false;
                newsAdapter.setLoading(false);  // 恢复加载中状态
                Log.d(TAG, "✅ 本次请求完成");
                Log.d(TAG, "==========================================\n");
            }

            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                Log.e(TAG, "========== 网络请求失败 ==========");
                Log.e(TAG, "❌ 错误类型: " + t.getClass().getName());
                Log.e(TAG, "❌ 错误消息: " + t.getMessage());
                Log.e(TAG, "❌ 请求URL: " + call.request().url());
                
                // 打印完整的堆栈跟踪
                t.printStackTrace();
                
                // 根据不同的错误类型给出提示
                String errorMsg;
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "无法连接到服务器，请检查网络";
                    Log.e(TAG, "提示: 可能是DNS解析失败或网络未连接");
                } else if (t instanceof java.net.ConnectException) {
                    errorMsg = "连接服务器失败，请确保后端已启动";
                    Log.e(TAG, "提示: 后端服务可能未启动，或端口被占用");
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "连接超时，请检查网络";
                    Log.e(TAG, "提示: 网络速度慢或服务器响应慢");
                } else {
                    errorMsg = "网络请求失败：" + t.getMessage();
                }
                
                // 【第6次修改】网络失败时，检查是否有缓存数据
                if (!newsList.isEmpty()) {
                    // 如果已经显示了缓存数据，提示用户
                    Toast.makeText(MainActivity.this, 
                        "⚠️ 网络连接失败，显示缓存数据", 
                        Toast.LENGTH_LONG).show();
                } else {
                    // 如果没有缓存数据，显示错误信息
                    Toast.makeText(MainActivity.this, 
                        errorMsg, 
                        Toast.LENGTH_LONG).show();
                }
                
                // 【第13次修改】刷新或加载完成，恢复加载中状态
                Log.d(TAG, "\n❌ 网络请求失败");
                Log.d(TAG, "🔄 恢复加载状态标志");
                Log.d(TAG, "  - isRefreshing: " + isRefreshing + " → false");
                Log.d(TAG, "  - isLoadingMore: " + isLoadingMore + " → false");
                isRefreshing = false;
                isLoadingMore = false;
                newsAdapter.setLoading(false);  // 恢复加载中状态
                Log.d(TAG, "==========================================\n");
            }
        });
        
        Log.d(TAG, "网络请求已发送，等待响应...");
    }
    
    /**
     * 【第10次修改】initPullToRefresh - 初始化手动下拉刷新
     * 
     * 改进逻辑：
     * 1. 只在列表顶部且向下拉动时才触发
     * 2. 必须从ACTION_DOWN开始就在顶部
     * 3. 防止滚动浏览时误触发
     */
    private void initPullToRefresh() {
        recyclerView.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        // 检查是否在列表顶部
                        if (!recyclerView.canScrollVertically(-1)) {
                            // 在顶部，记录起始位置
                            pullDownStartY = event.getY();
                            isPullingDown = true;
                            Log.d(TAG, "👆 在顶部按下，准备检测下拉刷新");
                        } else {
                            // 不在顶部，不允许下拉刷新
                            isPullingDown = false;
                        }
                        break;
                        
                    case android.view.MotionEvent.ACTION_MOVE:
                        // 只有在顶部开始的下拉才处理
                        if (isPullingDown && !isRefreshing) {
                            float currentY = event.getY();
                            float deltaY = currentY - pullDownStartY;
                            
                            // 必须是向下拉（deltaY > 0）且超过阈值
                            if (deltaY > PULL_THRESHOLD) {
                                Log.d(TAG, "🔄 检测到下拉刷新手势，deltaY: " + deltaY);
                                
                                // 重置到第一页
                                currentOffset = 0;
                                hasMoreData = true;
                                loadNewsWithCache();
                                
                                // 重置状态，避免重复触发
                                isPullingDown = false;
                            }
                        }
                        break;
                        
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        // 重置下拉状态
                        isPullingDown = false;
                        pullDownStartY = 0;
                        break;
                }
                return false; // 返回false让RecyclerView继续处理滑动事件
            }
        });
        
        Log.d(TAG, "✅ 手动下拉刷新已初始化（只在顶部下拉" + PULL_THRESHOLD + "像素触发）");
    }
    
    /**
     * 【第8次修改】初始化分类标签栏
     */
    private void initCategoryTabs() {
        categoryContainer = findViewById(R.id.categoryContainer);
        
        // 定义分类列表
        String[] categories = {"全部", "科技", "经济", "体育", "健康", "娱乐", "教育", "环保", "美食"};
        String[] categoryCodes = {null, "tech", "economy", "sports", "health", "entertainment", "education", "environment", "food"};
        
        for (int i = 0; i < categories.length; i++) {
            final String categoryName = categories[i];
            final String categoryCode = categoryCodes[i];
            
            // 创建分类标签
            TextView tabView = new TextView(this);
            tabView.setText(categoryName);
            tabView.setTextSize(14);
            tabView.setPadding(40, 20, 40, 20);
            
            // 设置布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            tabView.setLayoutParams(params);
            
            // 设置初始样式（全部默认选中）
            if (i == 0) {
                tabView.setBackgroundResource(R.drawable.category_tab_selected);
                tabView.setTextColor(Color.WHITE);
            } else {
                tabView.setBackgroundResource(R.drawable.category_tab_normal);
                tabView.setTextColor(Color.BLACK);
            }
            
            // 设置点击事件
            tabView.setOnClickListener(v -> {
                // 【第15次修改】保存当前分类的状态和数据（仅对非"全部"板块）
                if (currentCategory != null) {
                    // 当前是具体分类，保存其数据
                    String oldCategoryKey = currentCategory;
                    categoryOffsetMap.put(oldCategoryKey, currentOffset);
                    categoryHasMoreMap.put(oldCategoryKey, hasMoreData);
                    categoryDataMap.put(oldCategoryKey, new ArrayList<>(newsList));
                    Log.d(TAG, "💾 保存【" + oldCategoryKey + "】状态: offset=" + currentOffset + ", hasMore=" + hasMoreData + ", 数据量=" + newsList.size());
                }
                
                // 更新选中状态
                currentCategory = categoryCode;
                updateCategoryTabsUI(tabView);
                
                // 【第15次修改】区分"全部"和具体分类
                if (categoryCode == null) {
                    // 切换到"全部"板块：汇总所有分类的数据
                    Log.d(TAG, "📊 切换到【全部】板块，汇总所有分类数据...");
                    aggregateAllCategoryData();
                } else {
                    // 切换到具体分类
                    String newCategoryKey = categoryCode;
                    if (categoryDataMap.containsKey(newCategoryKey)) {
                        // 之前访问过，直接恢复数据和状态
                        currentOffset = categoryOffsetMap.get(newCategoryKey);
                        hasMoreData = categoryHasMoreMap.get(newCategoryKey);
                        List<NewsItem> savedData = categoryDataMap.get(newCategoryKey);
                        
                        newsList.clear();
                        newsList.addAll(savedData);
                        newsAdapter.notifyDataSetChanged();
                        
                        // 同步状态到Adapter
                        newsAdapter.setHasMoreData(hasMoreData);
                        newsAdapter.setShowLoadMore(true);
                        
                        Log.d(TAG, "📂 恢复【" + newCategoryKey + "】: offset=" + currentOffset + ", hasMore=" + hasMoreData + ", 数据量=" + savedData.size());
                        Toast.makeText(MainActivity.this, "已恢复【" + categoryName + "】的浏览状态", Toast.LENGTH_SHORT).show();
                    } else {
                        // 第一次访问，从服务器加载
                        currentOffset = 0;
                        hasMoreData = true;
                        Log.d(TAG, "🆕 首次访问【" + newCategoryKey + "】，从服务器加载");
                        loadNewsWithCache();
                    }
                }
            });
            
            // 添加到容器
            categoryContainer.addView(tabView);
            categoryTabs.add(tabView);
        }
        
        Log.d(TAG, "✅ 分类标签栏初始化完成，共 " + categories.length + " 个分类");
    }
    
    /**
     * 【第8次修改】更新分类标签的UI状态
     */
    private void updateCategoryTabsUI(TextView selectedTab) {
        for (TextView tab : categoryTabs) {
            if (tab == selectedTab) {
                // 选中状态
                tab.setBackgroundResource(R.drawable.category_tab_selected);
                tab.setTextColor(Color.WHITE);
            } else {
                // 未选中状态
                tab.setBackgroundResource(R.drawable.category_tab_normal);
                tab.setTextColor(Color.BLACK);
            }
        }
    }
    
    /**
     * 【第15次修改】汇总所有分类的数据到"全部"板块
     * 
     * 将8个分类（科技、经济、体育、健康、娱乐、教育、环保、美食）
     * 已加载的所有新闻合并显示，按发布时间倒序排列
     */
    private void aggregateAllCategoryData() {
        Log.d(TAG, "==========================================");
        Log.d(TAG, "🔄 开始汇总所有分类数据");
        Log.d(TAG, "==========================================");
        
        // 创建临时列表存储所有新闻
        List<NewsItem> allNews = new ArrayList<>();
        
        // 遍历所有分类，收集已加载的数据
        for (String categoryCode : CATEGORY_CODES) {
            if (categoryDataMap.containsKey(categoryCode)) {
                List<NewsItem> categoryNews = categoryDataMap.get(categoryCode);
                allNews.addAll(categoryNews);
                Log.d(TAG, "  📁 【" + categoryCode + "】: " + categoryNews.size() + " 条");
            } else {
                Log.d(TAG, "  ⚪ 【" + categoryCode + "】: 未加载");
            }
        }
        
        // 按发布时间倒序排序（最新的在前面）
        java.util.Collections.sort(allNews, (item1, item2) -> {
            // publishTime 格式: "2025-11-23 14:20:00"
            return item2.getPublishTime().compareTo(item1.getPublishTime());
        });
        
        // 更新UI
        newsList.clear();
        newsList.addAll(allNews);
        newsAdapter.notifyDataSetChanged();
        
        // "全部"板块不显示"加载更多"按钮（因为它是汇总视图）
        newsAdapter.setShowLoadMore(false);
        
        Log.d(TAG, "------------------------------------------");
        Log.d(TAG, "✅ 汇总完成！总计 " + allNews.size() + " 条新闻");
        Log.d(TAG, "==========================================");
        
        if (allNews.isEmpty()) {
            Toast.makeText(this, "请先访问各分类板块加载数据", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "【全部】板块已汇总 " + allNews.size() + " 条新闻", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 【第15次修改】初始加载所有分类的第一页数据
     * 
     * 应用启动时，为所有8个分类并发加载第一页数据，
     * 然后汇总显示在"全部"板块
     */
    private void loadInitialDataForAllCategories() {
        Log.d(TAG, "==========================================");
        Log.d(TAG, "🚀 初始化加载：为所有分类加载第一页数据");
        Log.d(TAG, "==========================================");
        
        // 用于跟踪已完成的请求数
        final int[] completedCount = {0};
        final int totalCategories = CATEGORY_CODES.length;
        
        // 为每个分类发起网络请求
        for (String categoryCode : CATEGORY_CODES) {
            Call<List<NewsItem>> call = apiService.getNewsListByCategory(categoryCode, 0, PAGE_SIZE);
            
            call.enqueue(new Callback<List<NewsItem>>() {
                @Override
                public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<NewsItem> data = response.body();
                        
                        // 保存到分类数据Map
                        categoryDataMap.put(categoryCode, new ArrayList<>(data));
                        categoryOffsetMap.put(categoryCode, data.size());
                        categoryHasMoreMap.put(categoryCode, data.size() >= PAGE_SIZE);
                        
                        Log.d(TAG, "✅ 【" + categoryCode + "】加载成功: " + data.size() + " 条");
                    } else {
                        Log.e(TAG, "❌ 【" + categoryCode + "】加载失败");
                    }
                    
                    // 增加完成计数
                    completedCount[0]++;
                    
                    // 如果所有分类都加载完成，汇总数据
                    if (completedCount[0] == totalCategories) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "🎉 所有分类加载完成，开始汇总...");
                            aggregateAllCategoryData();
                        });
                    }
                }
                
                @Override
                public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                    Log.e(TAG, "❌ 【" + categoryCode + "】网络请求失败: " + t.getMessage());
                    
                    // 即使失败也要增加计数，避免卡住
                    completedCount[0]++;
                    
                    if (completedCount[0] == totalCategories) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "⚠️ 部分分类加载失败，汇总已成功的数据...");
                            aggregateAllCategoryData();
                        });
                    }
                }
            });
        }
        
        Log.d(TAG, "📡 已发起 " + totalCategories + " 个网络请求");
    }
    
    /**
     * 【第16次修改】初始化布局切换按钮
     */
    private void initLayoutSwitchButton() {
        layoutSwitchButton = findViewById(R.id.layoutSwitchButton);
        
        // 设置初始图标
        updateLayoutButtonIcon();
        
        // 设置点击监听
        layoutSwitchButton.setOnClickListener(v -> {
            // 切换布局模式
            if (currentLayoutMode == LAYOUT_MODE_SINGLE) {
                currentLayoutMode = LAYOUT_MODE_GRID;
                Log.d(TAG, "🔄 切换到双列布局");
            } else {
                currentLayoutMode = LAYOUT_MODE_SINGLE;
                Log.d(TAG, "🔄 切换到单列布局");
            }
            
            // 更新布局
            switchLayoutMode();
        });
        
        Log.d(TAG, "✅ 布局切换按钮初始化完成");
    }
    
    /**
     * 【第16次修改】切换布局模式
     */
    private void switchLayoutMode() {
        RecyclerView.LayoutManager layoutManager;
        
        if (currentLayoutMode == LAYOUT_MODE_SINGLE) {
            // 单列模式：使用LinearLayoutManager
            layoutManager = new LinearLayoutManager(this);
            
            // 【第16次修改】设置Adapter为单列模式
            newsAdapter.setGridMode(false);
            
            Log.d(TAG, "📱 应用单列布局");
            Toast.makeText(this, "单列布局", Toast.LENGTH_SHORT).show();
        } else {
            // 双列模式：使用GridLayoutManager
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            
            // 【第16次修改】设置Adapter为网格模式
            newsAdapter.setGridMode(true);
            
            // 【重要】设置SpanSizeLookup，让加载更多卡片占满整行
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果是加载更多卡片，占满2列
                    if (position == newsAdapter.getItemCount() - 1 && 
                        newsAdapter.getItemViewType(position) == NewsAdapter.VIEW_TYPE_LOAD_MORE) {
                        return 2;  // 占满整行
                    }
                    return 1;  // 普通卡片占1列
                }
            });
            
            layoutManager = gridLayoutManager;
            Log.d(TAG, "🔲 应用双列布局（简洁版）");
            Toast.makeText(this, "双列布局", Toast.LENGTH_SHORT).show();
        }
        
        // 设置新的LayoutManager
        recyclerView.setLayoutManager(layoutManager);
        
        // 更新按钮图标
        updateLayoutButtonIcon();
    }
    
    /**
     * 【第16次修改】更新布局切换按钮图标
     */
    private void updateLayoutButtonIcon() {
        if (currentLayoutMode == LAYOUT_MODE_SINGLE) {
            // 当前是单列，显示网格图标（提示可以切换到双列）
            layoutSwitchButton.setImageResource(android.R.drawable.ic_dialog_dialer);
        } else {
            // 当前是双列，显示列表图标（提示可以切换到单列）
            layoutSwitchButton.setImageResource(android.R.drawable.ic_menu_view);
        }
    }
    
    /**
     * 【第8次修改】初始化自定义滚动条
     */
    private void initCustomScrollbar() {
        customScrollbar = findViewById(R.id.customScrollbar);
        
        // 设置滚动条触摸监听
        customScrollbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDraggingScrollbar = true;
                        scrollbarDragStartY = event.getRawY();
                        scrollbarInitialTop = customScrollbar.getTop();
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        if (isDraggingScrollbar) {
                            float deltaY = event.getRawY() - scrollbarDragStartY;
                            int newTop = scrollbarInitialTop + (int) deltaY;
                            
                            // 计算滚动条可移动的范围
                            int scrollbarHeight = customScrollbar.getHeight();
                            int containerHeight = recyclerView.getHeight();
                            int maxTop = containerHeight - scrollbarHeight;
                            
                            // 限制滚动条位置
                            newTop = Math.max(0, Math.min(newTop, maxTop));
                            
                            // 更新滚动条位置
                            customScrollbar.setY(newTop + findViewById(R.id.categoryScrollView).getHeight());
                            
                            // 计算RecyclerView应该滚动到的位置
                            if (recyclerView.computeVerticalScrollRange() > containerHeight) {
                                float scrollRatio = (float) newTop / maxTop;
                                int scrollRange = recyclerView.computeVerticalScrollRange() - containerHeight;
                                int scrollY = (int) (scrollRatio * scrollRange);
                                recyclerView.scrollTo(0, scrollY);
                            }
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
                
                if (!isDraggingScrollbar) {
                    updateScrollbarPosition();
                }
            }
        });
        
        Log.d(TAG, "✅ 自定义滚动条初始化完成");
    }
    
    /**
     * 【第8次修改】更新滚动条位置
     */
    private void updateScrollbarPosition() {
        int scrollRange = recyclerView.computeVerticalScrollRange();
        int scrollExtent = recyclerView.computeVerticalScrollExtent();
        int scrollOffset = recyclerView.computeVerticalScrollOffset();
        
        if (scrollRange > scrollExtent) {
            int containerHeight = recyclerView.getHeight();
            int scrollbarHeight = customScrollbar.getHeight();
            int maxTop = containerHeight - scrollbarHeight;
            
            float scrollRatio = (float) scrollOffset / (scrollRange - scrollExtent);
            int newTop = (int) (scrollRatio * maxTop);
            
            customScrollbar.setY(newTop + findViewById(R.id.categoryScrollView).getHeight());
        }
    }
    
    /**
     * 【第13次修改】初始化加载更多卡片
     * 
     * 功能：
     * 1. 加载更多卡片始终显示在列表最后
     * 2. 用户翻到最下方就能看到
     * 3. 点击卡片加载下一页
     */
    private void initLoadMoreText() {
        Log.d(TAG, "🔧 初始化加载更多卡片...");
        Log.d(TAG, "📊 初始状态：hasMoreData=" + hasMoreData + ", currentOffset=" + currentOffset);
        
        // 初始显示加载更多卡片（如果有更多数据）
        if (hasMoreData) {
            newsAdapter.setShowLoadMore(true);
            Log.d(TAG, "✅ 显示加载更多卡片");
        } else {
            Log.d(TAG, "⚠️ hasMoreData=false，不显示加载更多卡片");
        }
        
        Log.d(TAG, "✅ 加载更多卡片初始化完成");
    }
    
    /**
     * 【第13次修改】加载更多新闻
     * 
     * 公共方法，供外部调用（如点击"加载更多"卡片）
     * 根据当前分类加载对应板块的新闻
     */
    public void loadMoreNews() {
        Log.d(TAG, "\n========================================");
        Log.d(TAG, "🔘 点击了【加载更多】按钮");
        Log.d(TAG, "========================================");
        Log.d(TAG, "📊 当前状态检查：");
        Log.d(TAG, "  - isLoadingMore: " + isLoadingMore);
        Log.d(TAG, "  - hasMoreData: " + hasMoreData);
        Log.d(TAG, "  - currentOffset: " + currentOffset);
        Log.d(TAG, "  - 当前列表数量: " + newsList.size());
        Log.d(TAG, "  - PAGE_SIZE: " + PAGE_SIZE);
        
        // 检查是否正在加载或已无更多数据
        if (isLoadingMore || !hasMoreData) {
            Log.d(TAG, "❌ 无法加载更多！");
            if (isLoadingMore) {
                Log.d(TAG, "   原因：正在加载中，请等待");
            }
            if (!hasMoreData) {
                Log.d(TAG, "   原因：已无更多数据");
                Toast.makeText(this, "已加载全部数据", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "========================================\n");
            return;
        }
        
        String categoryName = currentCategory == null ? "全部" : currentCategory;
        Log.d(TAG, "✅ 通过状态检查，可以加载更多");
        Log.d(TAG, "📥 开始加载更多【" + categoryName + "】板块的新闻...");
        Log.d(TAG, "📍 将请求：offset=" + currentOffset + ", limit=" + PAGE_SIZE);
        
        // 【第13次修改】显示加载中状态
        newsAdapter.setLoading(true);
        Log.d(TAG, "🔄 已设置Adapter为加载中状态");
        
        loadNewsFromServer(true);  // true表示加载更多
        Log.d(TAG, "========================================\n");
    }
    
    /**
     * 【第17次修改】初始化卡片曝光追踪
     */
    private void initExposureTracker() {
        // 【第19次修改】创建并添加测试面板
        testPanel = new ExposureTestPanel(this);
        android.widget.FrameLayout testPanelContainer = findViewById(R.id.testPanelContainer);
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
                // 【第19次修改】同步到测试面板
                testPanel.logAppear(position, newsItem.getTitle());
            }
            
            @Override
            public void onCardHalfVisible(int position, NewsItem newsItem, float visiblePercent) {
                Log.i(TAG, String.format("📊 [曝光] 卡片50%%可见 - 位置: %d, 标题: %s, 可见度: %.2f%%", 
                    position, newsItem.getTitle(), visiblePercent * 100));
                // 【第19次修改】同步到测试面板
                testPanel.logHalfVisible(position, newsItem.getTitle(), visiblePercent);
            }
            
            @Override
            public void onCardFullyVisible(int position, NewsItem newsItem) {
                Log.i(TAG, String.format("✅ [曝光] 卡片完整露出 - 位置: %d, 标题: %s", 
                    position, newsItem.getTitle()));
                // 【第19次修改】同步到测试面板
                testPanel.logFullyVisible(position, newsItem.getTitle());
            }
            
            @Override
            public void onCardDisappear(int position, NewsItem newsItem) {
                Log.i(TAG, String.format("👋 [曝光] 卡片消失 - 位置: %d, 标题: %s", 
                    position, newsItem.getTitle()));
                // 【第19次修改】同步到测试面板
                testPanel.logDisappear(position, newsItem.getTitle());
            }
        });
        
        // 开始追踪
        exposureTracker.startTracking();
        
        Log.d(TAG, "✅ 卡片曝光追踪已启动");
    }
    
    /**
     * 【第17次修改】Activity生命周期 - onResume
     * 恢复曝光追踪
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (exposureTracker != null) {
            exposureTracker.startTracking();
            Log.d(TAG, "🔄 曝光追踪已恢复");
        }
    }
    
    /**
     * 【第17次修改】Activity生命周期 - onPause
     * 暂停曝光追踪
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (exposureTracker != null) {
            exposureTracker.stopTracking();
            Log.d(TAG, "⏸️ 曝光追踪已暂停");
        }
    }
    
    /**
     * 其他常用的生命周期方法（可以根据需要重写）：
     * 
     * @Override
     * protected void onStart() {
     *     super.onStart();
     *     // Activity 即将对用户可见时调用
     * }
     * 
     * @Override
     * protected void onStop() {
     *     super.onStop();
     *     // Activity 对用户不可见时调用
     * }
     * 
     * @Override
     * protected void onDestroy() {
     *     super.onDestroy();
     *     // Activity 被销毁前调用，用于释放资源
     * }
     */
}