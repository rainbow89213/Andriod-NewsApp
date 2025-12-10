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
import com.example.demo2.exposure.CardExposureListener;
import com.example.demo2.exposure.CardExposureTracker;
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
    
    // 保存滚动位置（确保初始值为0）
    private int scrollPosition = 0;
    private boolean hasBeenDisplayed = false;  // 标记Fragment是否已经显示过
    
    // 超时重置机制
    private static final int LOADING_TIMEOUT = 10000;  // 10秒超时
    private Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;
    
    // 曝光追踪器
    private CardExposureTracker exposureTracker;
    private CardExposureListener exposureListener;
    
    // 视频自动播放防抖
    private Handler videoCheckHandler = new Handler(Looper.getMainLooper());
    private Runnable videoCheckRunnable = null;
    private static final int VIDEO_CHECK_DELAY = 200;  // 200ms 防抖延迟
    
    // 调试信息 - 追踪滚动位置变化（已禁用，减少日志输出）
    private static final boolean DEBUG_SCROLL_ENABLED = false;
    private void debugLog(String method, String message) {
        if (DEBUG_SCROLL_ENABLED) {
            Log.e("DEBUG_SCROLL", String.format("[%s] %s - scrollPosition=%d, category=%s, %s", 
                method, categoryName, scrollPosition, categoryName, message));
        }
    }
    
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
        
        if (getArguments() != null) {
            categoryCode = getArguments().getString(ARG_CATEGORY_CODE);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
        }
        
        // 不再强制重置scrollPosition
        debugLog("onCreate", "Fragment创建, scrollPosition=" + scrollPosition);
        
        // 初始化Repository
        newsRepository = new NewsRepository(getContext());
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        newsSelectedListener = null;
        // 清理超时处理器
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
        }
        
        // 清理视频检查处理器
        if (videoCheckRunnable != null) {
            videoCheckHandler.removeCallbacks(videoCheckRunnable);
            videoCheckRunnable = null;
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "🏗️ onCreateView - " + categoryName + " (hashCode=" + this.hashCode() + ")");
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
        
        debugLog("onViewCreated", "开始, recyclerView=" + (recyclerView != null));
        
        // 恢复之前保存的状态
        if (savedInstanceState != null) {
            int oldPos = scrollPosition;
            // 配置更改时（如屏幕旋转）恢复状态
            scrollPosition = savedInstanceState.getInt("scroll_position", 0);
            currentOffset = savedInstanceState.getInt("current_offset", 0);
            
            // 防止scrollPosition变成负数
            if (scrollPosition < 0) {
                Log.w(TAG, "⚠️ 修正异常的scrollPosition: " + scrollPosition + " -> 0");
                scrollPosition = 0;
            }
            
            debugLog("onViewCreated", "从savedInstanceState恢复: oldPos=" + oldPos + " -> newPos=" + scrollPosition);
        } else {
            debugLog("onViewCreated", "无savedInstanceState");
        }
        
        // 首次加载或恢复后加载数据
        if (newsList.isEmpty()) {
            // 首次加载时，延迟一点以确保视图完全初始化
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // 立即检查并触发自动加载（包括分类页面）
                Log.d(TAG, "✨ 触发初始加载: " + categoryName);
                checkAndTriggerAutoLoad();
            }, 200);
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存状态以便配置更改后恢复
        debugLog("onSaveInstanceState", "保存状态");
        
        // 保存滚动位置（确保不保存负数）
        outState.putInt("scroll_position", Math.max(0, scrollPosition));
        outState.putInt("current_offset", Math.max(0, currentOffset));
    }
    
    private void setupRecyclerView() {
        debugLog("setupRecyclerView", "开始初始化RecyclerView");
        
        adapter = new NewsAdapter(newsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // 设置稳定的ID，提高性能
        recyclerView.setHasFixedSize(false);  // 因为有加载更多，所以不是固定大小
        
        // 获取当前滚动位置
        int currentPos = layoutManager.findFirstVisibleItemPosition();
        debugLog("setupRecyclerView", "RecyclerView初始化完成, 当前可见位置=" + currentPos);
        
        // 移除强制滚动到0的代码，看看问题在哪里
        // layoutManager.scrollToPositionWithOffset(0, 0);
        debugLog("setupRecyclerView", "不强制滚动，让RecyclerView自然显示");
        
        // 初始化时设置状态，但不显示加载更多
        adapter.setHasMoreData(true);
        // 加载更多卡片的显示会在checkAndTriggerAutoLoad中处理
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
        
        // 初始化曝光追踪器
        setupExposureTracker();
        
        // 添加滚动监听，实现自动加载更多（恢复原有2秒延迟逻辑）
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean hasLoggedInitialScroll = false;
            
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int pos = layoutManager.findFirstVisibleItemPosition();
                    String stateStr = newState == RecyclerView.SCROLL_STATE_IDLE ? "IDLE" :
                                     newState == RecyclerView.SCROLL_STATE_DRAGGING ? "DRAGGING" : "SETTLING";
                    debugLog("onScrollStateChanged", "state=" + stateStr + ", position=" + pos);
                }
                
                // 当滚动停止时，检查是否需要自动加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkAndTriggerAutoLoad();
                }
            }
            
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // 记录第一次滚动
                if (!hasLoggedInitialScroll && (dx != 0 || dy != 0)) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int pos = layoutManager.findFirstVisibleItemPosition();
                        debugLog("onScrolled-FIRST", "首次滚动检测 dx=" + dx + ", dy=" + dy + ", position=" + pos);
                        hasLoggedInitialScroll = true;
                    }
                }
                
                // 滚动时检查视频自动播放（使用防抖）
                scheduleVideoCheck();
            }
        });
    }
    
    /**
     * 设置下拉刷新
     */
    private void setupSwipeRefresh() {
        // 设置刷新动画颜色
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "下拉刷新 - 分类: " + categoryName);
            refreshNewNews();
        });
    }
    
    /**
     * 下拉刷新 - 获取2条新数据插入到顶部
     */
    private void refreshNewNews() {
        if (isLoading) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        
        isLoading = true;
        
        // 从当前offset位置获取2条新数据（即获取还没加载的数据）
        int refreshSize = 2;
        
        Log.d(TAG, "🔄 下拉刷新 - 从 offset=" + currentOffset + " 获取 " + refreshSize + " 条新数据");
        
        newsRepository.getNewsList(categoryCode, currentOffset, refreshSize, new NewsRepository.NewsCallback() {
            @Override
            public void onSuccess(List<NewsItem> news) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    
                    if (news != null && !news.isEmpty()) {
                        // 插入到列表顶部
                        adapter.insertDataAtTop(news);
                        currentOffset += news.size();
                        
                        // 滚动到顶部显示新内容
                        recyclerView.scrollToPosition(0);
                        
                        Toast.makeText(getContext(), "刷新了 " + news.size() + " 条新闻", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "✅ 刷新完成 - 新增 " + news.size() + " 条新闻");
                        
                        // 更新是否还有更多数据
                        hasMoreData = news.size() == refreshSize;
                        adapter.setHasMoreData(hasMoreData);
                    } else {
                        hasMoreData = false;
                        adapter.setHasMoreData(false);
                        Toast.makeText(getContext(), "没有更多新闻了", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "ℹ️ 刷新完成 - 没有更多数据");
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "刷新失败: " + error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "❌ 刷新失败: " + error);
                });
            }
        });
    }
    
    /**
     * 检查并触发自动加载
     * 当用户滚动到接近底部时自动加载更多
     */
    private void checkAndTriggerAutoLoad() {
        Log.d(TAG, "🔍 checkAndTriggerAutoLoad - " + categoryName + 
                   ", newsList.size=" + newsList.size() + 
                   ", isAutoLoadTriggered=" + isAutoLoadTriggered);
        
        // 特殊情况：初始状态，无数据且第一次触发
        if (newsList.isEmpty() && !isAutoLoadTriggered) {
            Log.d(TAG, "📍 初始触发自动加载 - " + categoryName);
            isAutoLoadTriggered = true;
            
            recyclerView.post(() -> {
                // 确保按顺序执行，避免并发问题
                adapter.setShowLoadMore(true);
                // 延迟一点更新加载状态，避免notify冲突
                recyclerView.postDelayed(() -> {
                    adapter.updateLoadingState(true);
                }, 100);
                
                autoLoadRunnable = () -> {
                    Log.d(TAG, "⏰ 延迟时间到，开始加载: " + categoryName);
                    loadMoreNews();
                };
                
                // 延迟2秒执行（让用户看到加载动画）
                Log.d(TAG, "🕒 设置2秒延迟加载: " + categoryName);
                autoLoadHandler.postDelayed(autoLoadRunnable, AUTO_LOAD_DELAY);
            });
            return;
        }
        
        if (recyclerView == null || adapter == null || isLoading) {
            return;
        }
        
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        
        int totalItemCount = adapter.getItemCount();
        if (totalItemCount == 0) {
            return;
        }
        
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        
        // 当滚动到接近底部时（看到加载更多卡片时）触发
        boolean shouldTrigger = (lastVisiblePosition >= totalItemCount - 1);  // 看到加载更多卡片时触发
        
        if (shouldTrigger && 
            hasMoreData && 
            !isLoading && 
            !isAutoLoadTriggered) {
            
            Log.d(TAG, "📍 触发自动加载 - 位置: " + lastVisiblePosition + "/" + totalItemCount);
            isAutoLoadTriggered = true;
            
            // 显示加载动画
            recyclerView.post(() -> {
                // 延迟一点更新，避免和滚动事件冲突
                adapter.updateLoadingState(true);
                
                autoLoadRunnable = () -> {
                    Log.d(TAG, "⏰ 延迟时间到，开始加载: " + categoryName);
                    loadMoreNews();
                };
                
                // 延迟2秒执行（让用户看到加载动画）
                Log.d(TAG, "🕒 设置2秒延迟加载: " + categoryName);
                autoLoadHandler.postDelayed(autoLoadRunnable, AUTO_LOAD_DELAY);
            });
        }
    }
    
    private void loadMoreNews() {
    Log.d(TAG, "🔄 loadMoreNews 调用 - 分类: " + categoryName + 
               ", hasMoreData=" + hasMoreData + ", isLoading=" + isLoading);
    
    if (!hasMoreData || isLoading) {
        Log.d(TAG, "   跳过加载: hasMoreData=" + hasMoreData + ", isLoading=" + isLoading);
        isAutoLoadTriggered = false;  // 如果不能加载，重置标志
        return;
    }
    
    Log.d(TAG, "🔄 执行加载更多 - 分类: " + categoryName + ", 当前offset: " + currentOffset);
    // 不需要再设置setLoading(true)，因为在checkAndTriggerAutoLoad中已经设置了
    loadNews(false);
    }
    
    /**
     * 加载新闻
     * @param isRefresh 是否是刷新操作
     */
    private void loadNews(boolean isRefresh) {
    // 取消之前的超时检查
    if (timeoutRunnable != null) {
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }
    
    if (isLoading) {
        Log.w(TAG, "已在加载中，跳过");
        return;
    }
    
    isLoading = true;
    
    // 设置新的超时检查（10秒后如果还在加载，强制重置）
    timeoutRunnable = () -> {
        if (isLoading) {
            Log.e(TAG, "⚠️ 加载超时，强制重置状态");
            isLoading = false;
            isAutoLoadTriggered = false;
            adapter.updateLoadingState(false);
            adapter.setShowLoadMore(true);
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "加载超时，请检查网络", Toast.LENGTH_SHORT).show();
        }
    };
    timeoutHandler.postDelayed(timeoutRunnable, LOADING_TIMEOUT);
    
    // 如果是刷新，重置偏移量和首次加载标记
    if (isRefresh) {
        currentOffset = 0;
        hasMoreData = true;
        isFirstLoad = true;  // 重置为首次加载
    }
    
    // 决定本次加载的数量
    int loadSize = isFirstLoad ? INITIAL_LOAD_SIZE : MORE_LOAD_SIZE;
    
    Log.d(TAG, "🎯 开始加载新闻 - 分类: " + categoryName + "(" + categoryCode + ")");
    Log.d(TAG, "   - offset: " + currentOffset + ", limit: " + loadSize);
    Log.d(TAG, "   - isFirstLoad: " + isFirstLoad + ", hasMoreData: " + hasMoreData);
    Log.d(TAG, "   - isLoading: " + isLoading + ", isAutoLoadTriggered: " + isAutoLoadTriggered);
    
    if (!isRefresh && currentOffset == 0) {
        // 首次加载，可以显示一个全屏加载动画
    }
    
    // 调用Repository加载数据
    Log.d(TAG, "📡 发起网络请求: " + categoryName + "(" + categoryCode + ")");
    newsRepository.getNewsList(categoryCode, currentOffset, loadSize, new NewsRepository.NewsCallback() {
        @Override
        public void onSuccess(List<NewsItem> news) {
            Log.d(TAG, "✅ 网络请求成功: " + categoryName + ", 返回 " + (news != null ? news.size() : 0) + " 条数据");
            
            // 调试：打印前3条新闻的mediaType
            if (news != null && !news.isEmpty()) {
                for (int i = 0; i < Math.min(3, news.size()); i++) {
                    NewsItem item = news.get(i);
                    Log.d(TAG, "🔍 新闻[" + i + "]: " + item.getTitle() + 
                        ", mediaType=" + item.getMediaType() + 
                        ", videoDuration=" + item.getVideoDuration());
                }
            }
            
            new Handler(Looper.getMainLooper()).post(() -> {
                // 移除超时检查
                if (timeoutRunnable != null) {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    timeoutRunnable = null;
                }
                
                isLoading = false;
                isAutoLoadTriggered = false;  // 重置自动加载标志
                swipeRefreshLayout.setRefreshing(false);
                
                if (isRefresh) {
                    // 只需要调用adapter.clearData，它会自动清空newsList
                    adapter.clearData();
                }
                
                if (news != null && !news.isEmpty()) {
                    // 记录是否是首次加载（用于滚动到顶部）
                    boolean shouldScrollToTop = isRefresh || newsList.isEmpty();
                    
                    // 只需要调用adapter.addData，它会自动添加到newsList
                    adapter.addData(news);
                    currentOffset += news.size();
                    
                    // 判断是否还有更多数据
                    // 修正判断逻辑：只有返回数据数量等于期望值时才认为可能有更多
                    int expectedSize = isFirstLoad ? INITIAL_LOAD_SIZE : MORE_LOAD_SIZE;
                    hasMoreData = news.size() == expectedSize;  // 修改为严格等于
                    
                    // 输出调试日志
                    Log.d(TAG, "🔍 加载判断 - 获得:" + news.size() + 
                               "条, 期望:" + expectedSize + 
                               "条, hasMoreData=" + hasMoreData);
                    
                    // 标记首次加载已完成
                    if (isFirstLoad) {
                        isFirstLoad = false;
                    }
                    
                    adapter.setHasMoreData(hasMoreData);
                    adapter.updateLoadingState(false);  // 停止加载动画
                    
                    // 始终显示加载更多卡片（有数据时显示动画，无数据时显示"已加载全部"）
                    adapter.setShowLoadMore(true);
                    
                    // 首次加载或刷新后滚动到顶部（放在最后，确保所有adapter更新完成后执行）
                    if (shouldScrollToTop && recyclerView != null) {
                        recyclerView.post(() -> {
                            recyclerView.scrollToPosition(0);
                            Log.d(TAG, "📍 首次加载/刷新后滚动到顶部");
                        });
                    }
                } else {
                    hasMoreData = false;
                    adapter.setHasMoreData(false);
                    adapter.updateLoadingState(false);
                    adapter.setShowLoadMore(true);  // 即使没数据也显示"已加载全部"
                    if (currentOffset == 0) {
                        Toast.makeText(getContext(), "该分类暂无新闻", Toast.LENGTH_SHORT).show();
                    }
                }
                
                Log.d(TAG, "✅ 加载完成 - 获取 " + (news != null ? news.size() : 0) + 
                          " 条新闻, 总计 " + newsList.size() + " 条, " +
                          (hasMoreData ? "还有更多" : "已加载全部"));
                
                // 刷新所有item以确保点击监听器生效
                adapter.refreshItemsForListener();
                
                // 数据加载后记录状态
                if (recyclerView != null) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int pos = layoutManager.findFirstVisibleItemPosition();
                        debugLog("loadNews-onSuccess", "数据加载完成, 当前位置=" + pos + 
                                ", isRefresh=" + isRefresh + ", hasBeenDisplayed=" + hasBeenDisplayed);
                        if (!hasBeenDisplayed) {
                            hasBeenDisplayed = true;
                            debugLog("loadNews-onSuccess", "标记hasBeenDisplayed=true");
                        }
                    }
                }
                
                // 数据加载完成后检查视频自动播放
                recyclerView.post(() -> checkVideoAutoPlay());
            });
        }
        
        @Override
        public void onError(String error) {
            Log.e(TAG, "❌ 网络请求失败: " + categoryName + ", 错误: " + error);
            new Handler(Looper.getMainLooper()).post(() -> {
                // 移除超时检查
                if (timeoutRunnable != null) {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    timeoutRunnable = null;
                }
                
                isLoading = false;
                isAutoLoadTriggered = false;  // 重置自动加载标志
                adapter.updateLoadingState(false);  // 停止加载动画
                adapter.setShowLoadMore(true);  // 错误时也保持显示
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "加载失败: " + error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ 加载新闻失败: " + error);
            });
        }
    });
    }
    
    /**
     * Fragment暂停时调用
     * 切换分类时不会影响其他Fragment
     */
    @Override
    public void onPause() {
        super.onPause();
        // 保存当前滚动位置（用于Tab切换时恢复）
        if (recyclerView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                scrollPosition = layoutManager.findFirstVisibleItemPosition();
                debugLog("onPause", "保存滚动位置: " + scrollPosition);
            }
        }
        
        // 停止所有视频播放
        stopAllVideoPlayback();
        
        debugLog("onPause", "Fragment暂停");
    }
    
    /**
     * 停止所有视频播放
     */
    private void stopAllVideoPlayback() {
        if (recyclerView == null || adapter == null) return;
        
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;
        
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        
        for (int i = firstVisible; i <= lastVisible; i++) {
            if (i < 0 || i >= newsList.size()) continue;
            
            NewsItem item = newsList.get(i);
            if (!"video".equals(item.getMediaType())) continue;
            
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof NewsAdapter.VideoViewHolder) {
                ((NewsAdapter.VideoViewHolder) holder).stopPlayback();
            }
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // View被销毁但Fragment实例可能被缓存（ViewPager2会缓存Fragment）
        // 不要在这里重置scrollPosition，让ViewPager2管理状态
        Log.d(TAG, "📤 onDestroyView - " + categoryName + " (hashCode=" + this.hashCode() + ")");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        debugLog("onResume", "开始, recyclerView=" + (recyclerView != null) + ", dataSize=" + newsList.size() + ", isVisible=" + getUserVisibleHint());
        
        // 获取当前实际位置
        if (recyclerView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                int actualPos = layoutManager.findFirstVisibleItemPosition();
                debugLog("onResume", "当前实际显示位置=" + actualPos + ", 保存的scrollPosition=" + scrollPosition);
            }
        }
        
        // 移除所有自动滚动代码，只记录状态
        // 只有在 Fragment 真正可见时才检查视频播放
        if (!newsList.isEmpty() && recyclerView != null && getUserVisibleHint()) {
            debugLog("onResume", "hasBeenDisplayed=" + hasBeenDisplayed);
            // 延迟检查视频自动播放，避免在 Fragment 切换时触发
            recyclerView.postDelayed(() -> {
                if (isResumed() && getUserVisibleHint()) {
                    checkVideoAutoPlay();
                }
            }, 300);
        }
        
        // 通知 Activity Fragment 已恢复（用于切换 Tab 时重新连接监听器）
        if (exposureTracker != null) {
            notifyFragmentReady();
        }
    }
    
    /**
     * 滚动到顶部（供外部调用）
     */
    public void scrollToTop() {
        if (recyclerView != null) {
            scrollPosition = 0;  // 同时重置保存的位置
            recyclerView.scrollToPosition(0);
            Log.d(TAG, "📍 滚动到顶部 - " + categoryName);
        }
    }
    
    /**
     * 调度视频检查（防抖）
     * 在滚动停止后延迟执行，避免频繁调用
     */
    private void scheduleVideoCheck() {
        // 取消之前的调度
        if (videoCheckRunnable != null) {
            videoCheckHandler.removeCallbacks(videoCheckRunnable);
        }
        
        // 创建新的调度任务
        videoCheckRunnable = () -> checkVideoAutoPlay();
        
        // 延迟执行
        videoCheckHandler.postDelayed(videoCheckRunnable, VIDEO_CHECK_DELAY);
    }
    
    /**
     * 检查视频自动播放
     * 当视频卡片显示≥50%时自动播放，<50%时停止
     */
    private void checkVideoAutoPlay() {
        if (recyclerView == null || adapter == null) return;
        
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;
        
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        
        // 遍历可见项
        for (int i = firstVisible; i <= lastVisible; i++) {
            if (i < 0 || i >= newsList.size()) continue;
            
            NewsItem item = newsList.get(i);
            // 只处理视频类型
            if (!"video".equals(item.getMediaType())) continue;
            
            // 获取ViewHolder
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof NewsAdapter.VideoViewHolder) {
                NewsAdapter.VideoViewHolder videoHolder = (NewsAdapter.VideoViewHolder) holder;
                
                // 计算可见性百分比
                View itemView = videoHolder.itemView;
                android.graphics.Rect rect = new android.graphics.Rect();
                boolean isVisible = itemView.getLocalVisibleRect(rect);
                
                if (isVisible) {
                    int viewHeight = itemView.getHeight();
                    int visibleHeight = rect.height();
                    float visibilityPercentage = (float) visibleHeight / viewHeight * 100;
                    
                    Log.d(TAG, "📹 视频可见性: " + item.getTitle() + " - " + 
                              String.format("%.1f%%", visibilityPercentage));
                    
                    // 根据可见性控制播放（50%阈值）
                    if (visibilityPercentage >= 50) {
                        // 自动播放
                        if (!videoHolder.isPlaying()) {
                            videoHolder.startPlayback();
                            Log.d(TAG, "▶️ 自动播放视频: " + item.getTitle());
                        }
                    } else {
                        // 自动停止
                        if (videoHolder.isPlaying()) {
                            videoHolder.stopPlayback();
                            Log.d(TAG, "⏸️ 自动停止视频: " + item.getTitle());
                        }
                    }
                } else {
                    // 不可见，停止播放
                    if (videoHolder.isPlaying()) {
                        videoHolder.stopPlayback();
                    }
                }
            }
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment实例被销毁时（不是View），重置所有状态
        // 这确保APP重启时从顶部开始
        scrollPosition = 0;
        hasBeenDisplayed = false;
        
        // 清理曝光追踪器
        if (exposureTracker != null) {
            exposureTracker.detachFromRecyclerView();
            exposureTracker = null;
        }
        
        // Log.d(TAG, "💥 Fragment销毁，重置所有状态 - " + categoryName);  // 调试日志已关闭
    }
    
    /**
     * 初始化曝光追踪器
     */
    private void setupExposureTracker() {
        exposureTracker = new CardExposureTracker();
        
        // 设置数据提供者
        exposureTracker.setDataProvider(new CardExposureTracker.DataProvider() {
            @Override
            public NewsItem getNewsItem(int position) {
                if (position >= 0 && position < newsList.size()) {
                    return newsList.get(position);
                }
                return null;
            }
            
            @Override
            public int getItemCount() {
                return newsList.size();
            }
        });
        
        // 如果有外部监听器，添加它
        if (exposureListener != null) {
            exposureTracker.addListener(exposureListener);
        }
        
        // 绑定到RecyclerView
        exposureTracker.attachToRecyclerView(recyclerView);
        
        Log.d(TAG, "📊 曝光追踪器已初始化 - " + categoryName);
        
        // 通知 Activity Fragment 已准备好
        notifyFragmentReady();
    }
    
    /**
     * 通知 Activity Fragment 已准备好接收曝光监听器
     */
    private void notifyFragmentReady() {
        if (getActivity() instanceof com.example.demo2.MainActivity) {
            com.example.demo2.MainActivity activity = (com.example.demo2.MainActivity) getActivity();
            activity.onFragmentReady(this, categoryCode);
        }
    }
    
    /**
     * 设置曝光事件监听器
     * @param listener 曝光事件监听器
     */
    public void setExposureListener(CardExposureListener listener) {
        this.exposureListener = listener;
        Log.d(TAG, "📊 setExposureListener 被调用, exposureTracker=" + (exposureTracker != null));
        if (exposureTracker != null) {
            exposureTracker.addListener(listener);
            // 立即触发一次检查
            exposureTracker.checkVisibility();
        }
    }
    
    /**
     * 移除曝光事件监听器
     * @param listener 曝光事件监听器
     */
    public void removeExposureListener(CardExposureListener listener) {
        if (exposureTracker != null) {
            exposureTracker.removeListener(listener);
        }
        if (this.exposureListener == listener) {
            this.exposureListener = null;
        }
    }
    
    /**
     * 获取曝光追踪器
     */
    public CardExposureTracker getExposureTracker() {
        return exposureTracker;
    }
}
