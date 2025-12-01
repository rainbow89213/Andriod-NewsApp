package com.example.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.demo2.adapter.CategoryPagerAdapter;
import com.example.demo2.fragment.NewsDetailFragment;
import com.example.demo2.fragment.NewsListFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面 - 使用ViewPager2 + Fragment架构
 * 每个分类使用独立的Fragment，自动管理状态
 */
public class MainActivity extends AppCompatActivity implements NewsListFragment.OnNewsSelectedListener {
    
    private static final String TAG = "MainActivity";
    
    // UI组件
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private boolean isTablet = false;  // 是否是平板模式
    private CategoryPagerAdapter pagerAdapter;
    private List<CategoryPagerAdapter.Category> categories;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewpager);
        
        Log.d(TAG, "📱 MainActivity启动 - ViewPager2架构");
        
        // 检查是否是平板模式（是否有detail_container）
        isTablet = findViewById(R.id.detail_container) != null;
        Log.d(TAG, isTablet ? "📱 平板模式" : "📱 手机模式");
        
        // 初始化
        initViews();
        initCategories();
        setupSystemUI();
        setupViewPager();
        
        // 如果是平板模式，显示初始的空白详情页
        if (isTablet) {
            showEmptyDetail();
        }
    }
    
    /**
     * 设置系统UI，适配刘海屏
     */
    private void setupSystemUI() {
        // 设置状态栏透明
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        // 使用WindowInsets API适配刘海屏
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Android 11及以上
            getWindow().setDecorFitsSystemWindows(false);
            
            if (tabLayout != null) {
                tabLayout.setOnApplyWindowInsetsListener((v, insets) -> {
                    int topInset = insets.getInsets(
                        android.view.WindowInsets.Type.systemBars() | 
                        android.view.WindowInsets.Type.displayCutout()
                    ).top;
                    v.setPadding(v.getPaddingLeft(), topInset, v.getPaddingRight(), v.getPaddingBottom());
                    return insets;
                });
            }
        } else {
            // Android 11以下的兼容处理
            android.view.View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }
    
    /**
     * 初始化分类数据
     */
    private void initCategories() {
        categories = new ArrayList<>();
        // 添加所有分类（移除"全部"）
        categories.add(new CategoryPagerAdapter.Category("tech", "科技"));
        categories.add(new CategoryPagerAdapter.Category("economy", "经济"));
        categories.add(new CategoryPagerAdapter.Category("sports", "体育"));
        categories.add(new CategoryPagerAdapter.Category("health", "健康"));
        categories.add(new CategoryPagerAdapter.Category("entertainment", "娱乐"));
        categories.add(new CategoryPagerAdapter.Category("education", "教育"));
        categories.add(new CategoryPagerAdapter.Category("environment", "环保"));
        categories.add(new CategoryPagerAdapter.Category("food", "美食"));
        
        Log.d(TAG, "📑 初始化 " + categories.size() + " 个分类");
    }
    
    /**
     * 设置ViewPager
     */
    private void setupViewPager() {
        // 创建适配器
        pagerAdapter = new CategoryPagerAdapter(this, categories);
        viewPager.setAdapter(pagerAdapter);
        
        // 设置预加载的Fragment数量（左右各1个）
        viewPager.setOffscreenPageLimit(1);
        
        // 连接TabLayout和ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> {
                CategoryPagerAdapter.Category category = categories.get(position);
                tab.setText(category.getName());
                Log.d(TAG, "🏷️ 设置Tab: " + category.getName() + " (位置: " + position + ")");
            }
        ).attach();
        
        // 添加页面切换监听
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                CategoryPagerAdapter.Category category = categories.get(position);
                Log.d(TAG, "📄 切换到分类: " + category.getName());
            }
        });
        
        // 设置Tab选择监听（可选）
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d(TAG, "👆 选择Tab: " + categories.get(position).getName());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Tab取消选择
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tab重新选择（可用于滚动到顶部）
                int position = tab.getPosition();
                Log.d(TAG, "👆👆 重新选择Tab: " + categories.get(position).getName());
                // 可以在这里实现双击Tab滚动到顶部的功能
            }
        });
        
        Log.d(TAG, "✅ ViewPager2设置完成");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "♻️ onResume - 当前分类: " + categories.get(viewPager.getCurrentItem()).getName());
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "⏸️ onPause - Fragment会自动保存各自的状态");
    }
    
    /**
     * 处理新闻选择事件（实现接口方法）
     */
    @Override
    public void onNewsSelected(NewsItem newsItem) {
        if (newsItem == null) {
            Log.e(TAG, "❌ 选中的newsItem为null");
            return;
        }
        
        Log.d(TAG, "📰 选中新闻: " + newsItem.getTitle() + ", 平板模式: " + isTablet);
        
        if (isTablet) {
            // 平板模式：在右侧显示详情
            showNewsDetail(newsItem);
        } else {
            // 手机模式：启动新Activity
            try {
                android.content.Intent intent = new android.content.Intent(this, NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.EXTRA_NEWS_ITEM, newsItem);
                startActivity(intent);
                Log.d(TAG, "✅ 启动NewsDetailActivity");
            } catch (Exception e) {
                Log.e(TAG, "❌ 启动NewsDetailActivity失败: " + e.getMessage(), e);
                Toast.makeText(this, "打开详情页失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * 在平板右侧显示新闻详情
     */
    private void showNewsDetail(NewsItem newsItem) {
        if (newsItem == null) {
            Log.e(TAG, "❌ newsItem为null，无法显示详情");
            return;
        }
        
        // 获取当前Fragment，避免重复显示相同新闻
        NewsDetailFragment currentFragment = (NewsDetailFragment) getSupportFragmentManager()
            .findFragmentById(R.id.detail_container);
        if (currentFragment != null) {
            // 直接更新现有Fragment，不需要replace
            currentFragment.updateNewsItem(newsItem);
            Log.d(TAG, "📄 更新详情Fragment - " + newsItem.getTitle());
            return;
        }
        
        // 如果没有当前Fragment，创建新的
        NewsDetailFragment fragment = NewsDetailFragment.newInstance(newsItem);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.detail_container, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss();  // 使用commitAllowingStateLoss避免状态丢失异常
            
        Log.d(TAG, "📚 创建新详情Fragment - " + newsItem.getTitle());
    }
    
    /**
     * 在平板右侧显示空白状态
     */
    private void showEmptyDetail() {
        NewsDetailFragment fragment = NewsDetailFragment.newEmptyInstance();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.detail_container, fragment)
            // 不添加到返回栈，因为这是初始状态
            .commit();
    }
    
    /**
     * 处理返回键按下事件
     */
    @Override
    public void onBackPressed() {
        // 获取Fragment管理器
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        
        // 检查是否有Fragment在返回栈中
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // 如果有，弹出最上面的Fragment
            fragmentManager.popBackStack();
            Log.d(TAG, "⬅️ 返回上一个Fragment，剩余栈深度: " + (fragmentManager.getBackStackEntryCount() - 1));
            
            // 如果返回栈空了，显示空白详情页（仅平板模式）
            if (isTablet && fragmentManager.getBackStackEntryCount() == 1) {
                // 延迟执行，确保popBackStack完成
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    if (fragmentManager.getBackStackEntryCount() == 0) {
                        showEmptyDetail();
                    }
                }, 100);
            }
        } else {
            // 如果没有Fragment在返回栈中，显示退出确认对话框
            showExitConfirmDialog();
        }
    }
    
    /**
     * 显示退出确认对话框
     */
    private void showExitConfirmDialog() {
        // 检查是否已经显示过退出提示
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime < 2000) {
            // 2秒内按了两次，直接退出
            super.onBackPressed();
        } else {
            // 第一次按返回键，显示提示
            lastBackPressTime = currentTime;
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
        }
    }
    
    // 添加一个变量来记录上次按返回键的时间
    private long lastBackPressTime = 0;
}
