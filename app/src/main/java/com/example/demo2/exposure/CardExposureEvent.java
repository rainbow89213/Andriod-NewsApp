package com.example.demo2.exposure;

/**
 * 卡片曝光事件类型
 */
public class CardExposureEvent {
    
    /**
     * 曝光事件类型枚举
     */
    public enum EventType {
        /** 卡片开始露出（可见性 > 0%） */
        CARD_APPEAR("卡片露出"),
        
        /** 卡片露出超过50% */
        CARD_HALF_VISIBLE("卡片露出超过50%"),
        
        /** 卡片完整露出（可见性 = 100%） */
        CARD_FULLY_VISIBLE("卡片完整露出"),
        
        /** 卡片消失（可见性 = 0%） */
        CARD_DISAPPEAR("卡片消失");
        
        private final String description;
        
        EventType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final int position;           // 卡片位置
    private final String newsId;          // 新闻ID
    private final String newsTitle;       // 新闻标题
    private final EventType eventType;    // 事件类型
    private final float visibilityPercent; // 可见性百分比
    private final long timestamp;         // 时间戳
    
    public CardExposureEvent(int position, String newsId, String newsTitle, 
                             EventType eventType, float visibilityPercent) {
        this.position = position;
        this.newsId = newsId;
        this.newsTitle = newsTitle;
        this.eventType = eventType;
        this.visibilityPercent = visibilityPercent;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getPosition() {
        return position;
    }
    
    public String getNewsId() {
        return newsId;
    }
    
    public String getNewsTitle() {
        return newsTitle;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public float getVisibilityPercent() {
        return visibilityPercent;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%.1f%%)", 
            position, eventType.getDescription(), 
            newsTitle.length() > 15 ? newsTitle.substring(0, 15) + "..." : newsTitle,
            visibilityPercent);
    }
}
