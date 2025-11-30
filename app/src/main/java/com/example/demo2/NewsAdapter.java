package com.example.demo2;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * NewsAdapter - RecyclerView 的适配器类
 * 
 * 【第12次修改】新增功能：
 * 1. 支持多种卡片布局（垂直、横向、加载更多）
 * 2. 长按删除卡片功能
 * 3. 动态显示加载更多卡片
 * 
 * 作用：
 * 1. 连接数据源（新闻列表）和 RecyclerView
 * 2. 负责创建和绑定每个卡片视图
 * 3. 处理卡片的点击和长按事件
 * 
 * RecyclerView 工作原理：
 * - 只创建屏幕可见的视图 + 少量缓存
 * - 当视图滚出屏幕时，会被回收并复用
 * - 这样即使有成千上万条数据，也只会创建少量视图对象
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 视图类型常量
    private static final int VIEW_TYPE_NEWS_VERTICAL = 0;    // 垂直新闻卡片
    private static final int VIEW_TYPE_NEWS_HORIZONTAL = 1;  // 横向新闻卡片
    public static final int VIEW_TYPE_LOAD_MORE = 2;         // 加载更多卡片（public以便外部访问）
    private static final int VIEW_TYPE_NEWS_GRID = 3;        // 网格布局专用卡片（简洁版）

    // 新闻数据列表
    private List<NewsItem> newsList;
    
    // 是否显示加载更多
    private boolean showLoadMore = false;
    
    // 是否还有更多数据
    private boolean hasMoreData = true;
    
    // 是否正在加载
    private boolean isLoading = false;
    
    // 【第16次修改】布局模式标识
    private boolean isGridMode = false;  // 是否为网格布局模式
    
    // 【第18次修改】单卡片样式覆盖
    // Key: position, Value: 视图类型（0=垂直, 1=横向, 3=网格）
    private java.util.Map<Integer, Integer> cardStyleOverrides = new java.util.HashMap<>();
    
    // 加载更多点击监听器
    private OnLoadMoreClickListener loadMoreClickListener;
    
    // 删除卡片监听器
    private OnItemDeleteListener deleteListener;

    /**
     * 构造函数：初始化适配器
     * 
     * @param newsList 新闻数据列表
     */
    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }
    
    /**
     * 设置加载更多点击监听器
     */
    public void setOnLoadMoreClickListener(OnLoadMoreClickListener listener) {
        this.loadMoreClickListener = listener;
    }
    
    /**
     * 设置删除监听器
     */
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }
    
    /**
     * 设置是否显示加载更多
     */
    public void setShowLoadMore(boolean show) {
        android.util.Log.d("NewsAdapter", "⚙️ setShowLoadMore: " + showLoadMore + " → " + show);
        this.showLoadMore = show;
        // 使用Handler延迟到下一帧执行，避免在滚动回调中修改数据
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            notifyDataSetChanged();
            android.util.Log.d("NewsAdapter", "  → notifyDataSetChanged() 已调用");
        });
    }
    
    /**
     * 设置是否还有更多数据
     */
    public void setHasMoreData(boolean hasMore) {
        this.hasMoreData = hasMore;
        // 使用Handler延迟到下一帧执行，避免在滚动回调中修改数据
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            notifyDataSetChanged();
        });
    }
    
    /**
     * 【第13次修改】设置加载中状态
     */
    public void setLoading(boolean loading) {
        this.isLoading = loading;
        // 使用Handler延迟到下一帧执行，避免在滚动回调中修改数据
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            notifyDataSetChanged();
        });
    }
    
    /**
     * 设置加载状态（支持更多参数）
     * 
     * @param loading 是否正在加载
     * @param hasMore 是否还有更多数据
     */
    public void setLoadingState(boolean loading, boolean hasMore) {
        this.isLoading = loading;
        this.hasMoreData = hasMore;
        // 使用Handler延迟到下一帧执行，避免在滚动回调中修改数据
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            notifyDataSetChanged();
        });
    }
    
    /**
     * 【第16次修改】设置布局模式
     * 
     * @param isGrid true表示网格布局，false表示单列布局
     */
    public void setGridMode(boolean isGrid) {
        android.util.Log.d("NewsAdapter", "⚙️ 设置布局模式: " + (isGrid ? "网格" : "单列"));
        this.isGridMode = isGrid;
        notifyDataSetChanged();
    }
    
    /**
     * 加载更多点击监听接口
     */
    public interface OnLoadMoreClickListener {
        void onLoadMoreClick();
    }
    
    /**
     * 删除监听接口
     */
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    /**
     * 新闻ViewHolder - 新闻卡片视图持有者
     */
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        // 卡片中的各个视图组件
        ImageView newsImage;      // 新闻图片
        TextView newsTitle;       // 新闻标题
        TextView newsSummary;     // 新闻摘要
        TextView newsTime;        // 发布时间
        TextView newsReadCount;   // 阅读数
        android.widget.ImageButton cardMenuButton;  // 【第18次修改】卡片菜单按钮

        /**
         * NewsViewHolder 构造函数
         */
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsSummary = itemView.findViewById(R.id.newsSummary);
            newsTime = itemView.findViewById(R.id.newsTime);
            newsReadCount = itemView.findViewById(R.id.newsReadCount);
            cardMenuButton = itemView.findViewById(R.id.cardMenuButton);  // 【第18次修改】
        }

        /**
         * 绑定数据到视图
         */
        public void bind(NewsItem newsItem, OnItemDeleteListener deleteListener, int position, NewsAdapter adapter) {
            // 使用 Glide 加载网络图片
            if (newsImage != null) {
                Glide.with(itemView.getContext())
                        .load(newsItem.getImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(android.R.drawable.ic_menu_gallery) // 加载中显示的占位图
                                .error(android.R.drawable.ic_menu_report_image) // 加载失败显示的图片
                                .transform(new RoundedCorners(16))) // 圆角处理
                        .into(newsImage);
            }
            
            // 设置文字内容（必有的View）
            if (newsTitle != null) {
                newsTitle.setText(newsItem.getTitle());
            }
            
            if (newsReadCount != null) {
                newsReadCount.setText(newsItem.getReadCount());
            }
            
            // 设置可选的View（网格布局中不存在）
            if (newsSummary != null) {
                newsSummary.setText(newsItem.getSummary());
            }
            
            if (newsTime != null) {
                newsTime.setText(newsItem.getPublishTime());
            }

            // 设置卡片点击事件
            itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), 
                    "点击了：" + newsItem.getTitle(), 
                    Toast.LENGTH_SHORT).show();
            });
            
            // 设置长按删除事件
            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    // 显示删除确认对话框
                    new AlertDialog.Builder(v.getContext())
                        .setTitle("删除新闻")
                        .setMessage("确定要删除这条新闻吗？\n\n" + newsItem.getTitle())
                        .setPositiveButton("确定", (dialog, which) -> {
                            deleteListener.onItemDelete(position);
                        })
                        .setNegativeButton("取消", null)
                        .show();
                }
                return true;
            });
            
            // 【第18次修改】设置卡片菜单按钮点击事件
            if (cardMenuButton != null) {
                cardMenuButton.setOnClickListener(v -> {
                    adapter.showCardStyleMenu(v, position);
                });
            }
        }
    }
    
    /**
     * 加载更多ViewHolder - 加载更多卡片视图持有者
     */
    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        TextView loadMoreText;
        android.widget.ProgressBar loadingProgressBar;
        
        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            loadMoreText = itemView.findViewById(R.id.loadMoreText);
            loadingProgressBar = itemView.findViewById(R.id.loadingProgressBar);
        }
        
        public void bind(OnLoadMoreClickListener listener, boolean hasMoreData, boolean isLoading) {
            android.util.Log.d("NewsAdapter", "🔧 LoadMoreViewHolder.bind 被调用");
            android.util.Log.d("NewsAdapter", "  - hasMoreData: " + hasMoreData);
            android.util.Log.d("NewsAdapter", "  - isLoading: " + isLoading);
            
            if (hasMoreData) {
                // 有更多数据时，始终显示加载动画（无论是否正在加载）
                android.util.Log.d("NewsAdapter", "  → 显示：加载动画（自动加载模式）");
                loadingProgressBar.setVisibility(android.view.View.VISIBLE);
                loadMoreText.setText("加载中...");
                loadMoreText.setTextColor(0xFF999999);  // 灰色
                loadMoreText.setVisibility(android.view.View.VISIBLE);
            } else {
                // 没有更多数据，显示"已加载全部数据"
                android.util.Log.d("NewsAdapter", "  → 显示：已加载全部数据");
                loadingProgressBar.setVisibility(android.view.View.GONE);
                loadMoreText.setText("已加载全部数据");
                loadMoreText.setTextColor(0xFF999999);  // 灰色
                loadMoreText.setVisibility(android.view.View.VISIBLE);
            }
        }
    }
    
    /**
     * 获取视图类型
     */
    @Override
    public int getItemViewType(int position) {
        android.util.Log.d("NewsAdapter", "🔍 getItemViewType - position: " + position);
        android.util.Log.d("NewsAdapter", "  - newsList.size: " + newsList.size());
        android.util.Log.d("NewsAdapter", "  - showLoadMore: " + showLoadMore);
        android.util.Log.d("NewsAdapter", "  - isGridMode: " + isGridMode);
        
        // 如果是最后一个位置且显示加载更多
        if (position == newsList.size() && showLoadMore) {
            android.util.Log.d("NewsAdapter", "  → 返回：VIEW_TYPE_LOAD_MORE");
            return VIEW_TYPE_LOAD_MORE;
        }
        
        // 检查是否有单卡片样式覆盖
        if (cardStyleOverrides.containsKey(position)) {
            int overrideType = cardStyleOverrides.get(position);
            android.util.Log.d("NewsAdapter", "  → 返回：单卡片覆盖样式 = " + overrideType);
            return overrideType;
        }
        
        // 网格模式使用简洁布局
        if (isGridMode) {
            android.util.Log.d("NewsAdapter", "  → 返回：网格卡片（简洁版）");
            return VIEW_TYPE_NEWS_GRID;
        }
        
        // 单列模式：偶数位置使用垂直布局，奇数位置使用横向布局
        int type = position % 2 == 0 ? VIEW_TYPE_NEWS_VERTICAL : VIEW_TYPE_NEWS_HORIZONTAL;
        android.util.Log.d("NewsAdapter", "  → 返回：" + (type == VIEW_TYPE_NEWS_VERTICAL ? "垂直卡片" : "横向卡片"));
        return type;
    }

    /**
     * onCreateViewHolder - 创建 ViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        switch (viewType) {
            case VIEW_TYPE_NEWS_VERTICAL:
                // 垂直布局
                View verticalView = inflater.inflate(R.layout.item_news_card, parent, false);
                return new NewsViewHolder(verticalView);
                
            case VIEW_TYPE_NEWS_HORIZONTAL:
                // 横向布局
                View horizontalView = inflater.inflate(R.layout.item_news_card_horizontal, parent, false);
                return new NewsViewHolder(horizontalView);
                
            case VIEW_TYPE_NEWS_GRID:
                // 网格布局
                View gridView = inflater.inflate(R.layout.item_news_card_grid, parent, false);
                return new NewsViewHolder(gridView);
                
            case VIEW_TYPE_LOAD_MORE:
                // 加载更多
                View loadMoreView = inflater.inflate(R.layout.item_load_more, parent, false);
                return new LoadMoreViewHolder(loadMoreView);
                
            default:
                View defaultView = inflater.inflate(R.layout.item_news_card, parent, false);
                return new NewsViewHolder(defaultView);
        }
    }

    /**
     * onBindViewHolder - 绑定数据到 ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        android.util.Log.d("NewsAdapter", "📍 onBindViewHolder - position: " + position + ", 总数: " + getItemCount());
        
        if (holder instanceof NewsViewHolder) {
            // 新闻卡片
            android.util.Log.d("NewsAdapter", "  → 绑定新闻卡片");
            NewsItem newsItem = newsList.get(position);
            ((NewsViewHolder) holder).bind(newsItem, deleteListener, position, this);
        } else if (holder instanceof LoadMoreViewHolder) {
            // 加载更多卡片，传递isLoading状态
            android.util.Log.d("NewsAdapter", "  → 绑定加载更多卡片");
            android.util.Log.d("NewsAdapter", "     showLoadMore: " + showLoadMore);
            android.util.Log.d("NewsAdapter", "     hasMoreData: " + hasMoreData);
            android.util.Log.d("NewsAdapter", "     isLoading: " + isLoading);
            ((LoadMoreViewHolder) holder).bind(loadMoreClickListener, hasMoreData, isLoading);
        } else {
            android.util.Log.e("NewsAdapter", "  ❌ 未知的ViewHolder类型！");
        }
    }

    /**
     * getItemCount - 获取数据总数
     */
    @Override
    public int getItemCount() {
        // 如果显示加载更多，总数+1
        return showLoadMore ? newsList.size() + 1 : newsList.size();
    }

    /**
     * 更新数据列表
     * 
     * 当数据源发生变化时调用此方法
     * 
     * @param newsList 新的数据列表
     */
    public void updateData(List<NewsItem> newsList) {
        this.newsList = newsList;
        // 通知 RecyclerView 数据已改变，需要刷新
        notifyDataSetChanged();
    }
    
    /**
     * 清空数据列表
     */
    public void clearData() {
        this.newsList.clear();
        notifyDataSetChanged();
    }
    
    /**
     * 显示卡片样式选择菜单
     */
    public void showCardStyleMenu(android.view.View anchor, int position) {
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(anchor.getContext(), anchor);
        
        // 添加菜单项
        popupMenu.getMenu().add(0, VIEW_TYPE_NEWS_VERTICAL, 0, "垂直卡片样式");
        popupMenu.getMenu().add(0, VIEW_TYPE_NEWS_HORIZONTAL, 1, "横向卡片样式");
        popupMenu.getMenu().add(0, VIEW_TYPE_NEWS_GRID, 2, "网格卡片样式");
        popupMenu.getMenu().add(0, -1, 3, "恢复默认样式");
        
        // 设置菜单项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            int selectedStyle = item.getItemId();
            
            if (selectedStyle == -1) {
                // 恢复默认样式
                cardStyleOverrides.remove(position);
                android.util.Log.d("NewsAdapter", "🔄 恢复默认样式 - position: " + position);
            } else {
                // 设置单卡片样式
                cardStyleOverrides.put(position, selectedStyle);
                String styleName = selectedStyle == VIEW_TYPE_NEWS_VERTICAL ? "垂直" :
                                   selectedStyle == VIEW_TYPE_NEWS_HORIZONTAL ? "横向" : "网格";
                android.util.Log.d("NewsAdapter", "✨ 设置单卡片样式 - position: " + position + ", 样式: " + styleName);
            }
            
            // 刷新该卡片
            notifyItemChanged(position);
            
            android.widget.Toast.makeText(anchor.getContext(), 
                item.getTitle() + " 已应用", 
                android.widget.Toast.LENGTH_SHORT).show();
            
            return true;
        });
        
        popupMenu.show();
    }
}
