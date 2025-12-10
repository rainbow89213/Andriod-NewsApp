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
        android.util.Log.d("NewsAdapter", "✅ 设置点击监听器, count=" + getItemCount());
    }
    
    /**
     * 刷新所有item以应用新的监听器
     */
    public void refreshItemsForListener() {
        if (getItemCount() > 0) {
            notifyItemRangeChanged(0, getItemCount());
            android.util.Log.d("NewsAdapter", "🔄 刷新所有item以应用监听器, count=" + getItemCount());
        }
    }

    /**
     * 显示加载更多
     */
    public void showLoadMore() {
        if (!showLoadMore) {
            showLoadMore = true;
            // 立即通知，不使用延迟
            notifyItemInserted(newsList.size());
        }
    }

    /**
     * 隐藏加载更多
     */
    public void hideLoadMore() {
        if (showLoadMore) {
            showLoadMore = false;
            // 立即通知，不使用延迟
            notifyItemRemoved(newsList.size());
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
     * 安全地更新加载状态（延迟执行）
     */
    public void updateLoadingState(boolean loading) {
        // 如果状态没有改变，不需要更新
        if (this.isLoading == loading) {
            return;
        }
        this.isLoading = loading;
        if (showLoadMore) {
            // 使用post确保在主线程执行，但不延迟
            if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
                // 已在主线程，直接更新
                notifyItemChanged(newsList.size());
            } else {
                // 不在主线程，post到主线程
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    notifyItemChanged(newsList.size());
                });
            }
        }
    }

    /**
     * 设置是否还有更多数据
     */
    public void setHasMoreData(boolean hasMore) {
        // 如果状态没有改变，不需要更新
        if (this.hasMoreData == hasMore) {
            return;
        }
        this.hasMoreData = hasMore;
        // 只更新加载更多项，不刷新整个列表
        if (showLoadMore) {
            int position = newsList.size();
            notifyItemChanged(position);
        }
    }

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
        ImageView image1, image2;  // 只使用两张图片
        TextView newsSummary;
        TextView newsTime;
        TextView newsReadCount;
        
        public MultiImageViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsSummary = itemView.findViewById(R.id.newsSummary);
            image1 = itemView.findViewById(R.id.image1);
            image2 = itemView.findViewById(R.id.image2);
            newsTime = itemView.findViewById(R.id.newsTime);
            newsReadCount = itemView.findViewById(R.id.newsReadCount);
        }
        
        public void bind(NewsItem newsItem, OnItemDeleteListener deleteListener, 
                        OnItemClickListener clickListener, int position, NewsAdapter adapter) {
            newsTitle.setText(newsItem.getTitle());
            if (newsSummary != null) {
                newsSummary.setText(newsItem.getSummary());
            }
            
            // 只加载前两张图片
            // 第一张图片
            if (newsItem.getImageUrl() != null) {
                Glide.with(itemView.getContext())
                    .load(newsItem.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(image1);
            }
            
            // 第二张图片（优先使用imageUrl2，如果没有则使用imageUrl3）
            String secondImage = newsItem.getImageUrl2();
            if (secondImage == null) {
                secondImage = newsItem.getImageUrl3();
            }
            
            if (secondImage != null) {
                image2.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                    .load(secondImage)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(image2);
            } else {
                // 如果没有第二张图片，隐藏第二个ImageView
                image2.setVisibility(View.GONE);
            }
            
            newsTime.setText(newsItem.getPublishTime());
            newsReadCount.setText(newsItem.getReadCount());
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                android.util.Log.d("NewsAdapter", "🔘 [MultiImage] 点击新闻 position=" + position + 
                    " 屏幕坐标: x=" + location[0] + ", y=" + location[1] + 
                    ", 宽=" + v.getWidth() + ", 高=" + v.getHeight() + " | " + newsItem.getTitle());
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
        
        // 倒计时显示相关
        View countdownContainer;  // 倒计时容器
        TextView countdownText;  // 倒计时文本
        TextView playbackTime;  // 播放进度时间
        android.widget.ProgressBar progressBar;  // 播放进度条（辅助）
        
        // 播放控制相关
        private android.os.CountDownTimer countDownTimer;
        private boolean isPlaying = false;
        private int currentProgress = 0;
        private int totalDuration = 0;
        
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.titleText);  // 视频布局使用titleText
            videoCover = itemView.findViewById(R.id.videoCover);
            playButton = itemView.findViewById(R.id.playButton);  // 播放按钮
            newsTime = itemView.findViewById(R.id.timeText);  // 视频布局使用timeText
            newsReadCount = itemView.findViewById(R.id.readCountText);  // 视频布局使用readCountText
            videoDuration = itemView.findViewById(R.id.durationText);  // 视频时长显示
            
            // 倒计时元素
            countdownContainer = itemView.findViewById(R.id.countdownContainer);
            countdownText = itemView.findViewById(R.id.countdownText);
            playbackTime = itemView.findViewById(R.id.playbackTime);  // 播放进度时间
            // 显示播放按钮
            if (playButton != null) {
                playButton.setVisibility(View.VISIBLE);
            }
            
            // 显示视频时长
            if (videoDuration != null) {
                videoDuration.setText(String.format(Locale.getDefault(), "%d:%02d", totalDuration / 60, totalDuration % 60));
            }
            
            // 初始化播放进度（可选）
            if (progressBar != null) {
                progressBar.setMax(totalDuration);
                progressBar.setProgress(currentProgress);
            }
            
            // 初始化显示
            updateCountdownDisplay();
            updatePlaybackTime();
            
            // 确保初始状态：播放按钮可见，倒计时容器隐藏
            if (countdownContainer != null) {
                countdownContainer.setVisibility(View.GONE);
            }
        }
        
        public void bind(NewsItem newsItem, OnItemDeleteListener deleteListener,
                        OnItemClickListener clickListener, int position, NewsAdapter adapter) {
            // 先停止之前的播放（防止ViewHolder复用时出现问题）
            stopPlayback();
            resetPlayback();
            
            newsTitle.setText(newsItem.getTitle());

            // 加载视频封面
            String coverUrl = newsItem.getVideoCoverUrl() != null ? 
                newsItem.getVideoCoverUrl() : newsItem.getImageUrl();
            Glide.with(itemView.getContext()).load(coverUrl).into(videoCover);

            // 保存视频时长
            totalDuration = newsItem.getVideoDuration();
            android.util.Log.d("VideoViewHolder", "📹 绑定视频: " + newsItem.getTitle() + 
                ", 时长=" + totalDuration + "秒, mediaType=" + newsItem.getMediaType());

            // 显示播放按钮
            if (playButton != null) {
                playButton.setVisibility(View.VISIBLE);
            }

            // 显示视频时长
            if (videoDuration != null) {
                videoDuration.setText(String.format(Locale.getDefault(), "%d:%02d", totalDuration / 60, totalDuration % 60));
            }
            
            newsTime.setText(newsItem.getPublishTime());
            newsReadCount.setText(newsItem.getReadCount() + "播放");
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                android.util.Log.d("NewsAdapter", "🔘 [Video] 点击新闻 position=" + position + 
                    " 屏幕坐标: x=" + location[0] + ", y=" + location[1] + 
                    ", 宽=" + v.getWidth() + ", 高=" + v.getHeight() + " | " + newsItem.getTitle());
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
        
        /**
         * 开始播放视频（模拟）
         */
        public void startPlayback() {
            android.util.Log.d("VideoViewHolder", "🎬 startPlayback 被调用: isPlaying=" + isPlaying + 
                ", totalDuration=" + totalDuration + ", countdownContainer=" + (countdownContainer != null));
            
            if (isPlaying || totalDuration == 0) {
                android.util.Log.d("VideoViewHolder", "⚠️ 无法播放: isPlaying=" + isPlaying + ", totalDuration=" + totalDuration);
                return;
            }
            
            isPlaying = true;
            
            // 隐藏播放按钮，显示倒计时容器
            if (playButton != null) {
                playButton.setVisibility(View.GONE);
                android.util.Log.d("VideoViewHolder", "✅ 隐藏播放按钮");
            }
            if (countdownContainer != null) {
                countdownContainer.setVisibility(View.VISIBLE);
                android.util.Log.d("VideoViewHolder", "✅ 显示倒计时容器");
            }
            
            // 先停止之前的计时器（防止重复）
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            
            // 创建倒计时器模拟播放（1秒更新一次，避免性能问题）
            countDownTimer = new android.os.CountDownTimer((totalDuration - currentProgress) * 1000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentProgress = totalDuration - (int)(millisUntilFinished / 1000);
                    updateCountdownDisplay();
                    updatePlaybackTime();
                    
                    // 可选：更新进度条
                    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setProgress(currentProgress);
                    }
                }
                
                @Override
                public void onFinish() {
                    currentProgress = totalDuration;
                    updateCountdownDisplay();
                    updatePlaybackTime();
                    
                    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setProgress(currentProgress);
                    }
                    stopPlayback();
                }
            };
            countDownTimer.start();
            
            android.util.Log.d("VideoViewHolder", "▶️ 开始播放视频");
        }
        
        /**
         * 停止播放视频
         */
        public void stopPlayback() {
            if (!isPlaying) return;
            
            isPlaying = false;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            
            // 显示播放按钮，隐藏倒计时容器
            if (playButton != null) {
                playButton.setVisibility(View.VISIBLE);
            }
            if (countdownContainer != null) {
                countdownContainer.setVisibility(View.GONE);
            }
            
            android.util.Log.d("VideoViewHolder", "⏸️ 停止播放视频");
        }
        
        /**
         * 更新倒计时显示
         */
        private void updateCountdownDisplay() {
            if (countdownText != null) {
                // 显示剩余时间（倒计时效果）
                int remainingTime = totalDuration - currentProgress;
                String countdown = String.format(Locale.getDefault(), "%d:%02d", 
                    remainingTime / 60, remainingTime % 60);
                countdownText.setText(countdown);
            }
        }
        
        /**
         * 更新播放时间显示
         */
        private void updatePlaybackTime() {
            if (playbackTime != null) {
                String current = String.format(Locale.getDefault(), "%d:%02d", 
                    currentProgress / 60, currentProgress % 60);
                String total = String.format(Locale.getDefault(), "%d:%02d", 
                    totalDuration / 60, totalDuration % 60);
                playbackTime.setText(current + " / " + total);
            }
        }
        
        /**
         * 重置播放状态
         */
        public void resetPlayback() {
            stopPlayback();
            currentProgress = 0;
            updateCountdownDisplay();
            updatePlaybackTime();
            if (progressBar != null) {
                progressBar.setProgress(0);
            }
        }
        
        /**
         * 获取是否正在播放
         */
        public boolean isPlaying() {
            return isPlaying;
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
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                android.util.Log.d("NewsAdapter", "🔘 [Single] 点击新闻 position=" + position + 
                    " 屏幕坐标: x=" + location[0] + ", y=" + location[1] + 
                    ", 宽=" + v.getWidth() + ", 高=" + v.getHeight() + " | " + newsItem.getTitle());
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
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        android.util.Log.d("NewsAdapter", "📦 onCreateViewHolder viewType=" + viewType);
        
        switch (viewType) {
            case VIEW_TYPE_NEWS_SINGLE:
                View singleView = inflater.inflate(R.layout.item_news_grid, parent, false);  // 使用网格布局
                return new NewsViewHolder(singleView);
                
            case VIEW_TYPE_NEWS_MULTI_IMAGE:
                View multiImageView = inflater.inflate(R.layout.item_news_double, parent, false);  // 使用双图布局
                return new MultiImageViewHolder(multiImageView);
                
            case VIEW_TYPE_NEWS_VIDEO:
                android.util.Log.d("NewsAdapter", "🎬 创建 VideoViewHolder");
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
     * 根据位置获取视图类型
     */
    @Override
    public int getItemViewType(int position) {
        // 如果是最后一个位置且需要显示加载更多
        if (position == newsList.size() && showLoadMore) {
            return VIEW_TYPE_LOAD_MORE;
        }
        
        NewsItem item = newsList.get(position);
        String mediaType = item.getMediaType();
        
        // 根据媒体类型返回不同的视图类型
        if ("video".equals(mediaType)) {
            return VIEW_TYPE_NEWS_VIDEO;
        } else if ("double_image".equals(mediaType) || "triple_image".equals(mediaType) || "multi_image".equals(mediaType)) {
            return VIEW_TYPE_NEWS_MULTI_IMAGE;  // 多图都使用双图展示
        } else {
            return VIEW_TYPE_NEWS_SINGLE;  // 单图使用网格样式
        }
    }

    /**
     * 添加数据（添加到末尾）
     */
    public void addData(List<NewsItem> newData) {
        if (newData == null || newData.isEmpty()) {
            return;
        }
        int startPosition = newsList.size();
        newsList.addAll(newData);
        notifyItemRangeInserted(startPosition, newData.size());
    }
    
    /**
     * 插入数据到顶部
     */
    public void insertDataAtTop(List<NewsItem> newData) {
        if (newData == null || newData.isEmpty()) {
            return;
        }
        newsList.addAll(0, newData);
        notifyItemRangeInserted(0, newData.size());
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
        int oldSize = this.newsList.size();
        this.newsList.clear();
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize);
        }
        // 如果有加载更多，也要更新它
        if (showLoadMore) {
            notifyItemChanged(0);
        }
    }

}
