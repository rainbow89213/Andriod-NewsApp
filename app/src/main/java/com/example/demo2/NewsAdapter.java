package com.example.demo2;

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
 * 作用：
 * 1. 连接数据源（新闻列表）和 RecyclerView
 * 2. 负责创建和绑定每个卡片视图
 * 3. 处理卡片的点击事件
 * 
 * RecyclerView 工作原理：
 * - 只创建屏幕可见的视图 + 少量缓存
 * - 当视图滚出屏幕时，会被回收并复用
 * - 这样即使有成千上万条数据，也只会创建少量视图对象
 * 
 * 继承关系：
 * NewsAdapter -> RecyclerView.Adapter<NewsAdapter.ViewHolder>
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    // 新闻数据列表
    private List<NewsItem> newsList;

    /**
     * 构造函数：初始化适配器
     * 
     * @param newsList 新闻数据列表
     */
    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    /**
     * ViewHolder - 视图持有者内部类
     * 
     * 作用：
     * 1. 持有卡片布局中的所有视图引用
     * 2. 避免重复调用 findViewById，提高性能
     * 3. 处理单个卡片的交互事件
     * 
     * 这是 ViewHolder 模式的实现，是 RecyclerView 高性能的关键
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 卡片中的各个视图组件
        ImageView newsImage;      // 新闻图片
        TextView newsTitle;       // 新闻标题
        TextView newsSummary;     // 新闻摘要
        TextView newsTime;        // 发布时间
        TextView newsReadCount;   // 阅读数

        /**
         * ViewHolder 构造函数
         * 
         * @param itemView 单个卡片的根视图
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // 通过 findViewById 获取卡片中的各个视图
            // 这些 ID 对应 item_news_card.xml 中定义的 ID
            newsImage = itemView.findViewById(R.id.newsImage);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsSummary = itemView.findViewById(R.id.newsSummary);
            newsTime = itemView.findViewById(R.id.newsTime);
            newsReadCount = itemView.findViewById(R.id.newsReadCount);
        }

        /**
         * 绑定数据到视图
         * 
         * @param newsItem 要显示的新闻数据
         */
        public void bind(NewsItem newsItem) {
            // 使用 Glide 加载网络图片
            Glide.with(itemView.getContext())
                    .load(newsItem.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(android.R.drawable.ic_menu_gallery) // 加载中显示的占位图
                            .error(android.R.drawable.ic_menu_report_image) // 加载失败显示的图片
                            .transform(new RoundedCorners(16))) // 圆角处理
                    .into(newsImage);
            
            // 设置文字内容
            newsTitle.setText(newsItem.getTitle());
            newsSummary.setText(newsItem.getSummary());
            newsTime.setText(newsItem.getPublishTime());
            newsReadCount.setText(newsItem.getReadCount());

            // 设置卡片点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击卡片时显示 Toast 提示
                    Toast.makeText(v.getContext(), 
                        "点击了：" + newsItem.getTitle(), 
                        Toast.LENGTH_SHORT).show();
                    
                    // 在实际应用中，这里可以：
                    // 1. 跳转到新闻详情页
                    // 2. 打开浏览器查看完整新闻
                    // 3. 分享新闻等
                }
            });
        }
    }

    /**
     * onCreateViewHolder - 创建 ViewHolder
     * 
     * 当 RecyclerView 需要一个新的 ViewHolder 时调用
     * 这个方法只在需要创建新视图时调用，复用时不会调用
     * 
     * @param parent RecyclerView
     * @param viewType 视图类型（如果有多种卡片样式时使用）
     * @return 新创建的 ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用 LayoutInflater 将 XML 布局文件转换为 View 对象
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news_card, parent, false);
        
        // 创建并返回 ViewHolder
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder - 绑定数据到 ViewHolder
     * 
     * 当 RecyclerView 需要显示数据时调用
     * 这个方法会频繁调用，每次滚动都可能触发
     * 
     * @param holder 要绑定数据的 ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取指定位置的新闻数据
        NewsItem newsItem = newsList.get(position);
        
        // 将数据绑定到 ViewHolder
        holder.bind(newsItem);
    }

    /**
     * getItemCount - 获取数据总数
     * 
     * RecyclerView 通过这个方法知道有多少条数据
     * 
     * @return 数据总数
     */
    @Override
    public int getItemCount() {
        return newsList.size();
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
}
