// 【第6次修改】新建文件：Room 数据库实体类
// 作用：定义本地缓存表的结构，用于存储从服务器获取的新闻数据
package com.example.demo2.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * CachedNews - 缓存的新闻实体类
 * 
 * @Entity 注解：标识这是一个 Room 数据库的表
 * tableName：指定表名为 "cached_news"
 * 
 * Room 会根据这个类自动创建数据库表，字段名对应列名
 * 
 * 表结构：
 * ┌────────────┬──────────┬─────────────────────┐
 * │ 列名        │ 类型      │ 说明                 │
 * ├────────────┼──────────┼─────────────────────┤
 * │ id         │ int      │ 主键，自动生成        │
 * │ title      │ String   │ 新闻标题             │
 * │ summary    │ String   │ 新闻摘要             │
 * │ imageUrl   │ String   │ 图片URL              │
 * │ publishTime│ String   │ 发布时间             │
 * │ readCount  │ String   │ 阅读数               │
 * │ cacheTime  │ long     │ 缓存时间戳（用于过期）│
 * └────────────┴──────────┴─────────────────────┘
 */
@Entity(tableName = "cached_news")
public class CachedNews {
    
    /**
     * 主键 ID
     * 
     * @PrimaryKey：标识这是主键
     * autoGenerate = true：自动生成 ID（从 1 开始递增）
     */
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    /**
     * 新闻标题
     * 例如："科技巨头发布新产品"
     */
    private String title;
    
    /**
     * 新闻摘要（简短描述）
     * 例如："今日，某科技公司发布了..."
     */
    private String summary;
    
    /**
     * 图片 URL
     * 例如："https://example.com/image.jpg"
     */
    private String imageUrl;
    
    /**
     * 发布时间
     * 例如："2025-11-23 12:00:00"
     */
    private String publishTime;
    
    /**
     * 阅读数
     * 例如："1.2万阅读"
     */
    private String readCount;
    
    /**
     * 缓存时间戳（毫秒）
     * 
     * 用于判断缓存是否过期
     * 例如：1700712000000（对应 2025-11-23 12:00:00）
     * 
     * 获取当前时间戳：System.currentTimeMillis()
     */
    private long cacheTime;
    
    // ==================== 构造方法 ====================
    
    /**
     * 无参构造方法（Room 需要）
     */
    public CachedNews() {
    }
    
    /**
     * 完整构造方法
     * 
     * @param title 新闻标题
     * @param summary 新闻摘要
     * @param imageUrl 图片URL
     * @param publishTime 发布时间
     * @param readCount 阅读数
     * @param cacheTime 缓存时间戳
     */
    public CachedNews(String title, String summary, String imageUrl, 
                      String publishTime, String readCount, long cacheTime) {
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.publishTime = publishTime;
        this.readCount = readCount;
        this.cacheTime = cacheTime;
    }
    
    // ==================== Getter 和 Setter 方法 ====================
    // Room 需要这些方法来读取和写入数据
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
    
    public String getReadCount() {
        return readCount;
    }
    
    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }
    
    public long getCacheTime() {
        return cacheTime;
    }
    
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 判断缓存是否过期
     * 
     * @param maxAgeMillis 最大缓存时间（毫秒）
     * @return true = 已过期，false = 未过期
     * 
     * 例如：判断是否超过 7 天
     * boolean expired = cachedNews.isExpired(7 * 24 * 60 * 60 * 1000L);
     */
    public boolean isExpired(long maxAgeMillis) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - cacheTime) > maxAgeMillis;
    }
    
    /**
     * 转换为字符串（用于调试）
     */
    @Override
    public String toString() {
        return "CachedNews{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", readCount='" + readCount + '\'' +
                ", cacheTime=" + cacheTime +
                '}';
    }
}
