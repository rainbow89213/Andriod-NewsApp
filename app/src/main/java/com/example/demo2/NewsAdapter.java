package com.example.demo2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

/**
 * NewsAdapter - 简化版新闻适配器
 * 根据媒体类型自动选择卡片样式，不再支持用户手动切换
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 视图类型常量
    private static final int VIEW_TYPE_NEWS_SINGLE = 0;      // 单图新闻卡片
    private static final int VIEW_TYPE_NEWS_MULTI_IMAGE = 1; // 多图新闻卡片
    private static final int VIEW_TYPE_NEWS_VIDEO = 2;       // 视频新闻卡片
    public static final int VIEW_TYPE_LOAD_MORE = 3;         // 加载更多卡片

    // 新闻数据列表
    private List<NewsItem> newsList;
    
    // 是否显示加载更多
    private boolean showLoadMore = false;
    
    // 是否还有更多数据
    private boolean hasMoreData = true;
    
    // 是否正在加载
    private boolean isLoading = false;
    
    // 布局模式标识（已废弃，保留以防编译错误）
    private boolean isGridMode = false;
    
    // 监听器
    private OnLoadMoreClickListener loadMoreClickListener;
    private OnItemDeleteListener deleteListener;
    private OnItemClickListener itemClickListener;

    /**
     * 构造函数：初始化适配器
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
     * 设置点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * 显示加载更多卡片
     */
    public void showLoadMore() {
        if (!showLoadMore) {
            showLoadMore = true;
            // 延迟到下一帧执行，避免在滚动回调中修改
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                // 再次检查状态，因为可能在延迟期间状态已改变
                if (showLoadMore) {
                    notifyItemInserted(newsList.size());
                }
            });
        }
    }

    /**
     * 隐藏加载更多卡片
     */
    public void hideLoadMore() {
        if (showLoadMore) {
            int position = newsList.size();  // 先保存位置
            showLoadMore = false;
            // 延迟到下一帧执行，避免在滚动回调中修改
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                notifyItemRemoved(position);
            });
        }
    }

    /**
     * 设置是否显示加载更多
     */
    public void setShowLoadMore(boolean show) {
        if (show) {
            showLoadMore();
        } else {
            hideLoadMore();
        }
    }

    /**
     * 设置是否还有更多数据
     */
    public void setHasMoreData(boolean hasMore) {
        this.hasMoreData = hasMore;
        if (showLoadMore) {
            notifyItemChanged(newsList.size());
        }
    }

    /**
     * 设置加载状态
     */
    public void setLoading(boolean loading) {
        this.isLoading = loading;
        // 不再立即通知更新，由调用者决定何时更新
        // 这样可以避免在滚动回调中出现问题
    }
    
    /**
     * 安全地更新加载状态（延迟执行）
     */
    public void updateLoadingState(boolean loading) {
        this.isLoading = loading;
        if (showLoadMore) {
            // 延迟到下一帧执行
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                notifyItemChanged(newsList.size());
            });
        }
    }

    /**
     * 设置加载状态
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
     * 设置布局模式（已废弃，保留以防编译错误）
     */
    public void setGridMode(boolean isGrid) {
        // 布局模式现在由媒体类型自动决定，不再支持手动切换
    }
    
    /**
     * 加载更多点击监听接口
     */
    public interface OnLoadMoreClickListener {
        void onLoadMoreClick();
    }
    
    /**
     * 删除卡片监听接口
     */
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }
    
    /**
     * 新闻点击监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    /**
     * MultiImageViewHolder - 多图新闻卡片视图持有者
     */
    public static class MultiImageViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        ImageView image1, image2, image3;
        TextView newsTime;
        TextView newsReadCount;
        
        public MultiImageViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.titleText);  // 多图布局使用titleText
            image1 = itemView.findViewById(R.id.image1);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            newsTime = itemView.findViewById(R.id.timeText);  // 多图布局使用timeText
            newsReadCount = itemView.findViewById(R.id.readCountText);  // 多图布局使用readCountText
        }
        
        public void bind(NewsItem newsItem, OnItemDeleteListener deleteListener, 
                        OnItemClickListener clickListener, int position, NewsAdapter adapter) {
            newsTitle.setText(newsItem.getTitle());
            
            // 加载三张图片
            Glide.with(itemView.getContext()).load(newsItem.getImageUrl()).into(image1);
            if (newsItem.getImageUrl2() != null) {
                Glide.with(itemView.getContext()).load(newsItem.getImageUrl2()).into(image2);
            }
            if (newsItem.getImageUrl3() != null) {
                Glide.with(itemView.getContext()).load(newsItem.getImageUrl3()).into(image3);
            }
            
            newsTime.setText(newsItem.getPublishTime());
            newsReadCount.setText(newsItem.getReadCount());
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                android.util.Log.d("NewsAdapter", "🔘 点击新闻: " + newsItem.getTitle());
                if (clickListener != null) {
                    android.util.Log.d("NewsAdapter", "✅ 调用clickListener.onItemClick");
                    clickListener.onItemClick(newsItem);
                } else {
                    android.util.Log.e("NewsAdapter", "❌ clickListener为null");
                }
            });
            
            // 设置长按删除事件
            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    new android.app.AlertDialog.Builder(itemView.getContext())
                            .setTitle("删除新闻")
                            .setMessage("确定要删除这条新闻吗？\n\n" + newsItem.getTitle())
                            .setPositiveButton("确定", (dialog, which) -> deleteListener.onItemDelete(position))
                            .setNegativeButton("取消", null)
                            .show();
                }
                return true;
            });
        }
    }
    
    /**
     * VideoViewHolder - 视频新闻卡片视图持有者
     */
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        ImageView videoCover;
        ImageView playButton;  // 播放按钮
        TextView newsTime;
        TextView newsReadCount;
        TextView videoDuration;  // 视频时长
        
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.titleText);  // 视频布局使用titleText
            videoCover = itemView.findViewById(R.id.videoCover);
            playButton = itemView.findViewById(R.id.playButton);  // 播放按钮
            newsTime = itemView.findViewById(R.id.timeText);  // 视频布局使用timeText
            newsReadCount = itemView.findViewById(R.id.readCountText);  // 视频布局使用readCountText
            videoDuration = itemView.findViewById(R.id.durationText);  // 视频时长显示
        }
        
        public void bind(NewsItem newsItem, OnItemDeleteListener deleteListener,
                        OnItemClickListener clickListener, int position, NewsAdapter adapter) {
            newsTitle.setText(newsItem.getTitle());
            
            // 加载视频封面
            String coverUrl = newsItem.getVideoCoverUrl() != null ? 
                newsItem.getVideoCoverUrl() : newsItem.getImageUrl();
            Glide.with(itemView.getContext()).load(coverUrl).into(videoCover);
            
            // 显示播放按钮
            if (playButton != null) {
                playButton.setVisibility(View.VISIBLE);
            }
            
            // 显示视频时长
            if (videoDuration != null) {
                int duration = newsItem.getVideoDuration();
                videoDuration.setText(String.format(Locale.getDefault(), "%d:%02d", duration / 60, duration % 60));
            }
            
            newsTime.setText(newsItem.getPublishTime());
            newsReadCount.setText(newsItem.getReadCount() + "播放");
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                android.util.Log.d("NewsAdapter", "🔘 点击新闻: " + newsItem.getTitle());
                if (clickListener != null) {
                    android.util.Log.d("NewsAdapter", "✅ 调用clickListener.onItemClick");
                    clickListener.onItemClick(newsItem);
                } else {
                    android.util.Log.e("NewsAdapter", "❌ clickListener为null");
                }
            });
            
            // 设置长按删除事件
            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    new android.app.AlertDialog.Builder(itemView.getContext())
                            .setTitle("删除新闻")
                            .setMessage("确定要删除这条新闻吗？\n\n" + newsItem.getTitle())
                            .setPositiveButton("确定", (dialog, which) -> deleteListener.onItemDelete(position))
                            .setNegativeButton("取消", null)
                            .show();
                }
                return true;
            });
        }
    }
    
    /**
     * NewsViewHolder - 单图新闻卡片视图持有者
     */
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;      // 新闻图片
        TextView newsTitle;       // 新闻标题
        TextView newsSummary;     // 新闻摘要
        TextView newsTime;        // 发布时间
        TextView newsReadCount;   // 阅读数
        TextView categoryText;    // 分类标签

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
            categoryText = itemView.findViewById(R.id.categoryText);
        }

        /**
         * 绑定数据到视图
         */
        public void bind(NewsItem newsItem, OnItemDeleteListener deleteListener, OnItemClickListener clickListener, int position) {
            // 使用 Glide 加载网络图片
            if (newsImage != null) {
                Glide.with(itemView.getContext())
                        .load(newsItem.getImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_report_image)
                                .transform(new RoundedCorners(16)))
                        .into(newsImage);
            }
            
            // 设置文字内容
            if (newsTitle != null) {
                newsTitle.setText(newsItem.getTitle());
            }
            
            if (newsSummary != null) {
                newsSummary.setText(newsItem.getSummary());
            }
            
            if (newsTime != null) {
                newsTime.setText(newsItem.getPublishTime());
            }
            
            if (newsReadCount != null) {
                // getReadCount() 返回的已经是格式化的字符串
                newsReadCount.setText(newsItem.getReadCount());
            }
            
            if (categoryText != null) {
                // 设置分类标签文本
                categoryText.setText(newsItem.getCategoryName() != null ? newsItem.getCategoryName() : "其他");
            }
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                android.util.Log.d("NewsAdapter", "🔘 点击新闻: " + newsItem.getTitle());
                if (clickListener != null) {
                    android.util.Log.d("NewsAdapter", "✅ 调用clickListener.onItemClick");
                    clickListener.onItemClick(newsItem);
                } else {
                    android.util.Log.e("NewsAdapter", "❌ clickListener为null");
                }
            });
            
            // 设置长按删除事件
            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    new android.app.AlertDialog.Builder(itemView.getContext())
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
        }
    }
    
    /**
     * 加载更多ViewHolder
     */
    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        TextView loadMoreText;
        android.widget.ProgressBar loadingProgressBar;

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            loadMoreText = itemView.findViewById(R.id.loadMoreText);
            loadingProgressBar = itemView.findViewById(R.id.loadingProgressBar);
        }

        public void bind(boolean isLoading, boolean hasMoreData, OnLoadMoreClickListener listener) {
            if (hasMoreData) {
                // 有更多数据时，始终显示加载动画（恢复原有逻辑）
                loadingProgressBar.setVisibility(View.VISIBLE);
                loadMoreText.setText("加载中...");
                loadMoreText.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(null);  // 自动加载，不需要点击
            } else {
                // 没有更多数据
                loadingProgressBar.setVisibility(View.GONE);
                loadMoreText.setText("已加载全部数据");
                loadMoreText.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(null);
            }
        }
    }
    
    /**
     * 获取视图类型
     */
    @Override
    public int getItemViewType(int position) {
        // 如果是最后一个位置且显示加载更多
        if (position == newsList.size() && showLoadMore) {
            return VIEW_TYPE_LOAD_MORE;
        }
        
        // 获取新闻项
        NewsItem item = newsList.get(position);
        
        // 根据媒体类型返回对应的视图类型
        if (item.isVideo()) {
            return VIEW_TYPE_NEWS_VIDEO;
        } else if (item.isMultiImage()) {
            return VIEW_TYPE_NEWS_MULTI_IMAGE;
        } else {
            return VIEW_TYPE_NEWS_SINGLE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        switch (viewType) {
            case VIEW_TYPE_NEWS_SINGLE:
                View singleView = inflater.inflate(R.layout.item_news_single, parent, false);
                return new NewsViewHolder(singleView);
                
            case VIEW_TYPE_NEWS_MULTI_IMAGE:
                View multiImageView = inflater.inflate(R.layout.item_news_multi_image, parent, false);
                return new MultiImageViewHolder(multiImageView);
                
            case VIEW_TYPE_NEWS_VIDEO:
                View videoView = inflater.inflate(R.layout.item_news_video, parent, false);
                return new VideoViewHolder(videoView);
                
            case VIEW_TYPE_LOAD_MORE:
                View loadMoreView = inflater.inflate(R.layout.item_load_more, parent, false);
                return new LoadMoreViewHolder(loadMoreView);
                
            default:
                View defaultView = inflater.inflate(R.layout.item_news_single, parent, false);
                return new NewsViewHolder(defaultView);
        }
    }

    /**
     * 绑定数据到ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            ((LoadMoreViewHolder) holder).bind(isLoading, hasMoreData, loadMoreClickListener);
        } else if (holder instanceof NewsViewHolder) {
            NewsItem newsItem = newsList.get(position);
            ((NewsViewHolder) holder).bind(newsItem, deleteListener, itemClickListener, position);
        } else if (holder instanceof MultiImageViewHolder) {
            NewsItem newsItem = newsList.get(position);
            ((MultiImageViewHolder) holder).bind(newsItem, deleteListener, itemClickListener, position, this);
        } else if (holder instanceof VideoViewHolder) {
            NewsItem newsItem = newsList.get(position);
            ((VideoViewHolder) holder).bind(newsItem, deleteListener, itemClickListener, position, this);
        }
    }

    /**
     * 获取项目数量
     */
    @Override
    public int getItemCount() {
        return newsList.size() + (showLoadMore ? 1 : 0);
    }

    /**
     * 添加数据
     */
    public void addData(List<NewsItem> newData) {
        int startPosition = newsList.size();
        newsList.addAll(newData);
        notifyItemRangeInserted(startPosition, newData.size());
    }

    /**
     * 刷新数据
     */
    public void refreshData(List<NewsItem> newData) {
        this.newsList.clear();
        this.newsList.addAll(newData);
        notifyDataSetChanged();
    }
    
    /**
     * 清空数据列表
     */
    public void clearData() {
        this.newsList.clear();
        notifyDataSetChanged();
    }

}
