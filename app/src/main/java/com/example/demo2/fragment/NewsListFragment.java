package com.example.demo2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.demo2.R;
import com.example.demo2.NewsAdapter;
import com.example.demo2.NewsItem;
import com.example.demo2.repository.NewsRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表Fragment
 * 每个分类对应一个独立的Fragment实例
 */
public class NewsListFragment extends Fragment {
    
    /**
     * 新闻选择监听接口
     */
    public interface OnNewsSelectedListener {
        void onNewsSelected(NewsItem newsItem);
    }
    
    private static final String TAG = "NewsListFragment";
    private static final String ARG_CATEGORY_CODE = "category_code";
    private static final String ARG_CATEGORY_NAME = "category_name";
    
    private String categoryCode;
    private String categoryName;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter adapter;
    private List<NewsItem> newsList = new ArrayList<>();
    private NewsRepository newsRepository;
    private OnNewsSelectedListener newsSelectedListener;
    
    // 分页相关
    private int currentOffset = 0;
    private static final int INITIAL_LOAD_SIZE = 4;  // 初次加载4条
    private static final int MORE_LOAD_SIZE = 2;     // 后续每次加载2条
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private boolean isFirstLoad = true;  // 标记是否首次加载
    
    // 自动加载相关（恢复原有逻辑）
    private Handler autoLoadHandler = new Handler(Looper.getMainLooper());
    private Runnable autoLoadRunnable = null;
    private boolean isAutoLoadTriggered = false;
    private static final int AUTO_LOAD_DELAY = 2000;  // 加载动画持续2秒
    
    // 保存滚动位置
    private int scrollPosition = 0;
    
    /**
     * 创建Fragment实例
     */
    public static NewsListFragment newInstance(String categoryCode, String categoryName) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_CODE, categoryCode);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 附加监听器
        if (context instanceof OnNewsSelectedListener) {
            newsSelectedListener = (OnNewsSelectedListener) context;
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 保留Fragment实例，避免配置更改时重新创建
        setRetainInstance(true);
        
        if (getArguments() != null) {
            categoryCode = getArguments().getString(ARG_CATEGORY_CODE);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
        }
        
        // 初始化Repository
        newsRepository = new NewsRepository(getContext());
        
        Log.d(TAG, "📌 Fragment创建 - 分类: " + categoryName);
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        newsSelectedListener = null;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        setupRecyclerView();
        setupSwipeRefresh();
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 恢复之前保存的状态
        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt("scroll_position", 0);
            currentOffset = savedInstanceState.getInt("current_offset", 0);
            recyclerView.scrollToPosition(scrollPosition);
        }
        
        // 首次加载或恢复后加载数据
        if (newsList.isEmpty()) {
            // 首次加载时，延迟一点以确保视图完全初始化
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // 立即检查并触发自动加载（包括分类页面）
                checkAndTriggerAutoLoad();
            }, 200);
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前滚动位置和偏移量
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            scrollPosition = layoutManager.findFirstVisibleItemPosition();
            outState.putInt("scroll_position", scrollPosition);
            outState.putInt("current_offset", currentOffset);
        }
    }
    
    private void setupRecyclerView() {
        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 初始化时就显示加载更多卡片（确保所有分类都有加载动画）
        adapter.setHasMoreData(true);
        // 延迟一点显示加载卡片，确保RecyclerView完全初始化
        recyclerView.post(() -> {
            adapter.setShowLoadMore(true);
        });
        
        // 设置加载更多监听器
        adapter.setOnLoadMoreClickListener(() -> loadMoreNews());
        
        // 设置删除监听器
        adapter.setOnItemDeleteListener(position -> {
            if (position >= 0 && position < newsList.size()) {
                newsList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "新闻已删除", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 设置点击监听器
        adapter.setOnItemClickListener(newsItem -> {
            Log.d(TAG, "🔘 点击新闻: " + newsItem.getTitle());
            if (newsSelectedListener != null) {
                Log.d(TAG, "✅ 监听器存在，调用onNewsSelected");
                newsSelectedListener.onNewsSelected(newsItem);
            } else {
                Log.e(TAG, "❌ newsSelectedListener为null，无法传递点击事件");
                // 尝试重新获取监听器
                if (getActivity() instanceof OnNewsSelectedListener) {
                    newsSelectedListener = (OnNewsSelectedListener) getActivity();
                    newsSelectedListener.onNewsSelected(newsItem);
                    Log.d(TAG, "🔄 重新获取监听器成功");
                } else {
                    Log.e(TAG, "❌ Activity未实现OnNewsSelectedListener接口");
                }
            }
        });
        
        // 添加滚动监听，实现自动加载更多（恢复原有2秒延迟逻辑）
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkAndTriggerAutoLoad();
            }
        });
    }
    
    /**
     * 检查并触发自动加载（恢复原有逻辑）
     * 滚动接近底部时，显示加载动画2秒，然后自动加载
     */
    private void checkAndTriggerAutoLoad() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;
        
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int totalItemCount = adapter.getItemCount();
        
        // 判断是否接近底部（提前1个位置触发）
        // 特殊处理：当列表项目很少时也触发
        boolean shouldTrigger = (totalItemCount <= 2) ||  // 很少的项目或只有加载更多卡片
                                (lastVisiblePosition >= totalItemCount - 1);  // 看到加载更多卡片时触发
        
        if (shouldTrigger && 
            hasMoreData && 
            !isLoading && 
            !isAutoLoadTriggered) {
            
            Log.d(TAG, "📍 触发自动加载 - 位置: " + lastVisiblePosition + "/" + totalItemCount);
            
            // 标记已触发，防止重复
            isAutoLoadTriggered = true;
            
            // 延迟到下一帧执行数据修改，避免在滚动回调中修改RecyclerView
            recyclerView.post(() -> {
                // 显示加载动画
                adapter.setShowLoadMore(true);
                adapter.updateLoadingState(true);  // 使用安全的更新方法
                
                // 取消之前的延迟任务（如果有）
                if (autoLoadRunnable != null) {
                    autoLoadHandler.removeCallbacks(autoLoadRunnable);
                }
                
                // 创建延迟任务：2秒后执行加载
                autoLoadRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "⏱️ 2秒延迟后开始加载");
                        loadMoreNews();
                    }
                };
                
                // 延迟2秒执行（让用户看到加载动画）
                autoLoadHandler.postDelayed(autoLoadRunnable, AUTO_LOAD_DELAY);
            });
        }
    }
    
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "下拉刷新 - 分类: " + categoryName);
            loadNews(true);
        });
    }
    
    private void loadNews(boolean isRefresh) {
        if (isLoading) {
            Log.w(TAG, "已在加载中，跳过");
            return;
        }
        
        isLoading = true;
        
        // 如果是刷新，重置偏移量和首次加载标记
        if (isRefresh) {
            currentOffset = 0;
            hasMoreData = true;
            isFirstLoad = true;  // 重置为首次加载
        }
        
        // 决定本次加载的数量
        int loadSize = isFirstLoad ? INITIAL_LOAD_SIZE : MORE_LOAD_SIZE;
        
        Log.d(TAG, "开始加载 - 分类: " + categoryName + ", 偏移: " + currentOffset + ", 数量: " + loadSize);
        
        if (!isRefresh && currentOffset == 0) {
            // 首次加载，可以显示一个全屏加载动画
        }
        
        // 调用Repository加载数据
        newsRepository.getNewsList(categoryCode, currentOffset, loadSize, new NewsRepository.NewsCallback() {
            @Override
            public void onSuccess(List<NewsItem> news) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    isLoading = false;
                    isAutoLoadTriggered = false;  // 重置自动加载标志
                    swipeRefreshLayout.setRefreshing(false);
                    
                    if (isRefresh) {
                        // 只需要调用adapter.clearData，它会自动清空newsList
                        adapter.clearData();
                    }
                    
                    if (news != null && !news.isEmpty()) {
                        // 只需要调用adapter.addData，它会自动添加到newsList
                        adapter.addData(news);
                        currentOffset += news.size();
                        
                        // 判断是否还有更多数据
                        // 根据当前加载类型判断：首次加载少于4条，或后续加载少于2条
                        int expectedSize = isFirstLoad ? INITIAL_LOAD_SIZE : MORE_LOAD_SIZE;
                        hasMoreData = news.size() >= expectedSize;
                        
                        // 标记首次加载已完成
                        if (isFirstLoad) {
                            isFirstLoad = false;
                        }
                        
                        adapter.setHasMoreData(hasMoreData);
                        adapter.setLoading(false);  // 停止加载动画
                        
                        // 始终显示加载更多卡片（有数据时显示动画，无数据时显示"已加载全部"）
                        adapter.setShowLoadMore(true);
                    } else {
                        hasMoreData = false;
                        adapter.setHasMoreData(false);
                        adapter.setLoading(false);
                        adapter.setShowLoadMore(true);  // 即使没数据也显示"已加载全部"
                        if (currentOffset == 0) {
                            Toast.makeText(getContext(), "暂无新闻", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    Log.d(TAG, "✅ 加载完成 - 获取 " + (news != null ? news.size() : 0) + 
                              " 条新闻, 总计 " + newsList.size() + " 条, " +
                              (hasMoreData ? "还有更多" : "已加载全部"));
                });
            }
            
            @Override
            public void onError(String error) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    isLoading = false;
                    isAutoLoadTriggered = false;  // 重置自动加载标志
                    adapter.setLoading(false);  // 停止加载动画
                    adapter.setShowLoadMore(true);  // 错误时也保持显示
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "加载失败: " + error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "❌ 加载新闻失败: " + error);
                });
            }
        });
    }
    
    private void loadMoreNews() {
        if (!hasMoreData || isLoading) {
            isAutoLoadTriggered = false;  // 如果不能加载，重置标志
            return;
        }
        
        Log.d(TAG, "🔄 加载更多 - 分类: " + categoryName + ", 当前offset: " + currentOffset);
        // 不需要再设置setLoading(true)，因为在checkAndTriggerAutoLoad中已经设置了
        loadNews(false);
    }
    
    /**
     * Fragment独立管理自己的状态
     * 切换分类时不会影响其他Fragment
     */
    @Override
    public void onPause() {
        super.onPause();
        // 保存当前滚动位置
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            scrollPosition = layoutManager.findFirstVisibleItemPosition();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 恢复滚动位置
        if (scrollPosition > 0) {
            recyclerView.scrollToPosition(scrollPosition);
        }
    }
}
