// 包名声明：定义这个类所属的包
// 包名必须与 AndroidManifest.xml 中声明的包名一致
package com.example.demo2;

// 导入 Android SDK 的类
import android.os.Bundle;  // Bundle 用于保存和恢复 Activity 状态
import android.util.Log;  // 日志工具类
import android.widget.Toast;  // Toast 提示

// 导入 AndroidX 库的类
import androidx.activity.EdgeToEdge;  // 边到边显示功能（全屏显示）
import androidx.appcompat.app.AppCompatActivity;  // 向后兼容的 Activity 基类
import androidx.core.graphics.Insets;  // 表示屏幕边缘的插入区域
import androidx.core.view.ViewCompat;  // 视图兼容性工具类
import androidx.core.view.WindowInsetsCompat;  // 窗口插入区域兼容类
import androidx.recyclerview.widget.LinearLayoutManager;  // 线性布局管理器
import androidx.recyclerview.widget.RecyclerView;  // RecyclerView 组件

// 导入 Java 工具类
import java.util.ArrayList;  // 动态数组列表
import java.util.List;  // 列表接口

// 导入网络请求相关类
import com.example.demo2.api.RetrofitClient;
import com.example.demo2.api.NewsApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        // 调用父类的 onCreate 方法（必须调用）
        super.onCreate(savedInstanceState);
        
        // 启用边到边显示（Edge-to-Edge）
        // 让内容可以延伸到状态栏和导航栏下方，实现沉浸式体验
        EdgeToEdge.enable(this);
        
        // 设置 Activity 的布局文件
        // R.layout.activity_main 引用 res/layout/activity_main.xml 文件
        // R 是自动生成的资源类，包含所有资源的 ID
        setContentView(R.layout.activity_main);
        
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
        
        // 初始化 RecyclerView
        initRecyclerView();
        
        // 初始化 API 服务
        apiService = RetrofitClient.getNewsApiService();
        
        // 从服务器加载数据
        loadNewsFromServer();
    }
    
    /**
     * initRecyclerView - 初始化 RecyclerView
     * 
     * 设置 RecyclerView 的布局管理器和适配器
     */
    private void initRecyclerView() {
        // 1. 获取 RecyclerView 组件
        recyclerView = findViewById(R.id.recyclerView);
        
        // 2. 创建线性布局管理器
        // LinearLayoutManager：垂直线性布局，从上到下排列
        // 其他选项：GridLayoutManager（网格布局）、StaggeredGridLayoutManager（瀑布流）
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        // 3. 初始化数据列表
        newsList = new ArrayList<>();
        
        // 4. 创建适配器
        newsAdapter = new NewsAdapter(newsList);
        
        // 5. 设置适配器到 RecyclerView
        recyclerView.setAdapter(newsAdapter);
        
        // 可选：设置固定大小优化性能
        // 如果 RecyclerView 的大小不会因为内容改变而改变，设置为 true 可以提高性能
        recyclerView.setHasFixedSize(true);
    }
    
    /**
     * loadNewsFromServer - 从服务器加载新闻数据
     * 
     * 使用 Retrofit 进行异步网络请求
     */
    private void loadNewsFromServer() {
        Log.d(TAG, "开始加载新闻数据...");
        
        // 发起网络请求
        apiService.getNewsList().enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                // 请求成功
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> newsItems = response.body();
                    Log.d(TAG, "加载成功，获取到 " + newsItems.size() + " 条新闻");
                    
                    // 更新数据
                    newsList.clear();
                    newsList.addAll(newsItems);
                    newsAdapter.notifyDataSetChanged();
                    
                    Toast.makeText(MainActivity.this, 
                        "加载成功！获取 " + newsItems.size() + " 条新闻", 
                        Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "请求失败：" + response.code());
                    Toast.makeText(MainActivity.this, 
                        "加载失败：" + response.code(), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                // 请求失败（网络错误、超时等）
                Log.e(TAG, "网络请求失败", t);
                Toast.makeText(MainActivity.this, 
                    "网络请求失败：" + t.getMessage(), 
                    Toast.LENGTH_LONG).show();
            }
        });
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
     * protected void onResume() {
     *     super.onResume();
     *     // Activity 开始与用户交互时调用
     * }
     * 
     * @Override
     * protected void onPause() {
     *     super.onPause();
     *     // Activity 即将失去焦点时调用（如弹出对话框）
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