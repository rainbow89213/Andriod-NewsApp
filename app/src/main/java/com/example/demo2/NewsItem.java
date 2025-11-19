package com.example.demo2;

import com.google.gson.annotations.SerializedName;

/**
 * NewsItem - 新闻数据模型类
 * 
 * 作用：封装单条新闻的所有信息
 * 这是一个 POJO (Plain Old Java Object) 类，用于存储和传递数据
 * 
 * 包含的信息：
 * - 标题
 * - 内容摘要
 * - 图片URL（支持网络图片）
 * - 发布时间
 * - 阅读数
 */
public class NewsItem {
    // 新闻标题
    @SerializedName("title")
    private String title;
    
    // 新闻内容摘要
    @SerializedName("summary")
    private String summary;
    
    // 新闻图片URL（网络图片地址）
    @SerializedName("imageUrl")
    private String imageUrl;
    
    // 发布时间
    @SerializedName("publishTime")
    private String publishTime;
    
    // 阅读数
    @SerializedName("readCount")
    private String readCount;

    /**
     * 构造函数：创建一个新闻对象
     * 
     * @param title 新闻标题
     * @param summary 新闻摘要
     * @param imageUrl 图片URL
     * @param publishTime 发布时间
     * @param readCount 阅读数
     */
    public NewsItem(String title, String summary, String imageUrl, String publishTime, String readCount) {
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.publishTime = publishTime;
        this.readCount = readCount;
    }
    
    // 无参数构造函数（Gson反序列化需要）
    public NewsItem() {
    }

    // Getter 方法：用于获取私有属性的值
    
    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getReadCount() {
        return readCount;
    }

    // Setter 方法：用于修改私有属性的值（可选）
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }
}
