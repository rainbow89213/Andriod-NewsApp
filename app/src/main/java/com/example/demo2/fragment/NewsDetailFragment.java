package com.example.demo2.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.demo2.NewsItem;
import com.example.demo2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 新闻详情Fragment
 * 用于显示新闻的详细内容
 */
public class NewsDetailFragment extends Fragment {
    
    private static final String ARG_NEWS_ITEM = "news_item";
    
    // 视图组件
    private TextView titleText;
    private TextView categoryText;
    private TextView timeText;
    private TextView readCountText;
    private ImageView mainImage;
    private LinearLayout multiImageContainer;
    private TextView contentText;
    private TextView summaryText;
    private ScrollView scrollView;
    private TextView emptyText;
    
    private NewsItem newsItem;
    
    /**
     * 创建Fragment实例
     */
    public static NewsDetailFragment newInstance(NewsItem newsItem) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_ITEM, newsItem);
        fragment.setArguments(args);
        return fragment;
    }
    
    /**
     * 创建空的Fragment（平板右侧初始状态）
     */
    public static NewsDetailFragment newEmptyInstance() {
        return new NewsDetailFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsItem = (NewsItem) getArguments().getSerializable(ARG_NEWS_ITEM);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化视图
        initViews(view);
        
        // 显示内容
        if (newsItem != null) {
            displayNewsDetail();
        } else {
            showEmptyState();
        }
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews(View view) {
        titleText = view.findViewById(R.id.detailTitle);
        categoryText = view.findViewById(R.id.detailCategory);
        timeText = view.findViewById(R.id.detailTime);
        readCountText = view.findViewById(R.id.detailReadCount);
        mainImage = view.findViewById(R.id.detailMainImage);
        multiImageContainer = view.findViewById(R.id.detailMultiImageContainer);
        contentText = view.findViewById(R.id.detailContent);
        summaryText = view.findViewById(R.id.detailSummary);
        scrollView = view.findViewById(R.id.detailScrollView);
        emptyText = view.findViewById(R.id.emptyText);
    }
    
    /**
     * 显示新闻详情
     */
    private void displayNewsDetail() {
        if (emptyText != null) {
            emptyText.setVisibility(View.GONE);
        }
        if (scrollView != null) {
            scrollView.setVisibility(View.VISIBLE);
        }
        
        // 设置标题
        titleText.setText(newsItem.getTitle());
        
        // 设置分类
        if (!TextUtils.isEmpty(newsItem.getCategoryName())) {
            categoryText.setText(newsItem.getCategoryName());
            categoryText.setVisibility(View.VISIBLE);
        } else {
            categoryText.setVisibility(View.GONE);
        }
        
        // 设置时间（已经是格式化的字符串）
        timeText.setText(newsItem.getPublishTime());
        
        // 设置阅读量（已经是格式化的字符串）
        readCountText.setText(newsItem.getReadCount());
        
        // 设置摘要
        summaryText.setText(newsItem.getSummary());
        
        // 设置图片
        displayImages();
        
        // 设置正文（使用摘要扩展生成详细内容）
        String content = generateDetailContent(newsItem);
        contentText.setText(content);
    }
    
    /**
     * 显示图片
     */
    private void displayImages() {
        String mediaType = newsItem.getMediaType();
        
        if ("single_image".equals(mediaType)) {
            // 单图模式
            mainImage.setVisibility(View.VISIBLE);
            multiImageContainer.setVisibility(View.GONE);
            
            Glide.with(this)
                .load(newsItem.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(mainImage);
                
        } else if ("multi_image".equals(mediaType)) {
            // 多图模式
            mainImage.setVisibility(View.GONE);
            multiImageContainer.setVisibility(View.VISIBLE);
            multiImageContainer.removeAllViews();
            
            // 添加多张图片
            addImageToContainer(newsItem.getImageUrl());
            if (!TextUtils.isEmpty(newsItem.getImageUrl2())) {
                addImageToContainer(newsItem.getImageUrl2());
            }
            if (!TextUtils.isEmpty(newsItem.getImageUrl3())) {
                addImageToContainer(newsItem.getImageUrl3());
            }
            
        } else if ("video".equals(mediaType)) {
            // 视频模式，显示封面
            mainImage.setVisibility(View.VISIBLE);
            multiImageContainer.setVisibility(View.GONE);
            
            String coverUrl = newsItem.getVideoCoverUrl();
            if (TextUtils.isEmpty(coverUrl)) {
                coverUrl = newsItem.getImageUrl();
            }
            
            Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(mainImage);
                
        } else {
            // 无图模式
            mainImage.setVisibility(View.GONE);
            multiImageContainer.setVisibility(View.GONE);
        }
    }
    
    /**
     * 添加图片到容器
     */
    private void addImageToContainer(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) return;
        
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp2px(200)
        );
        params.setMargins(0, 0, 0, dp2px(10));
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error_image)
            .into(imageView);
            
        multiImageContainer.addView(imageView);
    }
    
    /**
     * 显示空状态（平板右侧初始状态）
     */
    private void showEmptyState() {
        if (scrollView != null) {
            scrollView.setVisibility(View.GONE);
        }
        if (emptyText != null) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("请选择一条新闻查看详情");
        }
    }
    
    /**
     * 更新显示的新闻（用于平板模式）
     */
    public void updateNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
        if (getView() != null) {
            displayNewsDetail();
        }
    }
    
    /**
     * 格式化时间
     */
    private String formatTime(Date publishTime) {
        if (publishTime == null) {
            return "刚刚";
        }
        
        long diff = System.currentTimeMillis() - publishTime.getTime();
        long hours = diff / (1000 * 60 * 60);
        
        if (hours < 1) {
            long minutes = diff / (1000 * 60);
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            return sdf.format(publishTime);
        }
    }
    
    /**
     * 格式化阅读量
     */
    private String formatReadCount(int readCount) {
        if (readCount < 1000) {
            return readCount + " 阅读";
        } else if (readCount < 10000) {
            return String.format(Locale.getDefault(), "%.1fk 阅读", readCount / 1000.0);
        } else {
            return String.format(Locale.getDefault(), "%.1fw 阅读", readCount / 10000.0);
        }
    }
    
    /**
     * 生成详细内容（模拟）
     */
    private String generateDetailContent(NewsItem item) {
        StringBuilder content = new StringBuilder();
        
        content.append(item.getSummary());
        content.append("\n\n");
        
        // 添加模拟的详细内容
        content.append("【详细报道】\n\n");
        content.append("据相关消息，").append(item.getTitle()).append("。");
        content.append("这一消息引起了业界的广泛关注。\n\n");
        
        // 根据分类添加不同的模拟内容
        String category = item.getCategoryName();
        if ("科技".equals(category)) {
            content.append("技术专家表示，这一发展将对行业产生深远影响。");
            content.append("相关技术的突破为未来的创新奠定了基础。");
        } else if ("经济".equals(category)) {
            content.append("经济分析师认为，这将对市场产生积极影响。");
            content.append("投资者对此消息反应积极，市场前景看好。");
        } else if ("体育".equals(category)) {
            content.append("体育评论员表示，这是一个历史性的时刻。");
            content.append("球迷们对此表示热烈欢迎和支持。");
        } else {
            content.append("专家表示，这一事件值得持续关注。");
            content.append("相关部门已经开始采取相应措施。");
        }
        
        content.append("\n\n");
        content.append("更多详细信息，请持续关注后续报道。");
        
        return content.toString();
    }
    
    /**
     * dp转px
     */
    private int dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
