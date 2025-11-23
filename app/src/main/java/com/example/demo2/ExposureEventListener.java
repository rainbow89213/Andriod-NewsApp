package com.example.demo2;

/**
 * 卡片曝光事件监听接口
 * 
 * 用于追踪新闻卡片在列表中的曝光情况，支持精准的曝光事件回调
 */
public interface ExposureEventListener {
    
    /**
     * 卡片开始露出（任意像素可见）
     * 
     * @param position 卡片位置
     * @param newsItem 新闻项
     */
    void onCardAppear(int position, NewsItem newsItem);
    
    /**
     * 卡片露出超过50%
     * 
     * @param position 卡片位置
     * @param newsItem 新闻项
     * @param visiblePercent 可见比例（0.0-1.0）
     */
    void onCardHalfVisible(int position, NewsItem newsItem, float visiblePercent);
    
    /**
     * 卡片完整露出（100%可见）
     * 
     * @param position 卡片位置
     * @param newsItem 新闻项
     */
    void onCardFullyVisible(int position, NewsItem newsItem);
    
    /**
     * 卡片消失（完全不可见）
     * 
     * @param position 卡片位置
     * @param newsItem 新闻项
     */
    void onCardDisappear(int position, NewsItem newsItem);
}
