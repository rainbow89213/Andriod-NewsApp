package com.example.demo2;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

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
public class NewsItem implements Serializable {
    // 新闻标题
    @SerializedName("title")
    private String title;
    
    // 新闻内容摘要
    @SerializedName("summary")
    private String summary;
    
    // 新闻图片URL（网络图片地址）
    @SerializedName("imageUrl")
    private String imageUrl;
    
    // 第二张图片URL（多图模式）
    @SerializedName("imageUrl2")
    private String imageUrl2;
    
    // 第三张图片URL（多图模式）
    @SerializedName("imageUrl3")
    private String imageUrl3;
    
    // 媒体类型：single_image（单图）、multi_image（多图）、video（视频）
    @SerializedName("mediaType")
    private String mediaType = "single_image"; // 默认单图
    
    // 视频URL
    @SerializedName("videoUrl")
    private String videoUrl;
    
    // 视频时长（秒）
    @SerializedName("videoDuration")
    private int videoDuration;
    
    // 视频封面URL
    @SerializedName("videoCoverUrl")
    private String videoCoverUrl;
    
    // 发布时间
    @SerializedName("publishTime")
    private String publishTime;
    
    // 阅读数
    @SerializedName("readCount")
    private String readCount;
    
    // 分类名称
    @SerializedName("categoryName")
    private String categoryName;

    /**
     * 构造函数：创建一个新闻对象（单图模式）
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
        this.mediaType = "single_image";
    }
    
    /**
     * 构造函数：创建一个完整的新闻对象（支持所有媒体类型）
     */
    public NewsItem(String title, String summary, String imageUrl, String imageUrl2, String imageUrl3,
                   String mediaType, String videoUrl, int videoDuration, String videoCoverUrl,
                   String publishTime, String readCount) {
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.mediaType = mediaType != null ? mediaType : "single_image";
        this.videoUrl = videoUrl;
        this.videoDuration = videoDuration;
        this.videoCoverUrl = videoCoverUrl;
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
    
    public String getImageUrl2() {
        return imageUrl2;
    }
    
    public String getImageUrl3() {
        return imageUrl3;
    }
    
    public String getMediaType() {
        return mediaType != null ? mediaType : "single_image";
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public int getVideoDuration() {
        return videoDuration;
    }
    
    public String getVideoCoverUrl() {
        return videoCoverUrl;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    // 判断是否为视频类型
    public boolean isVideo() {
        return "video".equals(mediaType);
    }
    
    // 判断是否为多图类型
    public boolean isMultiImage() {
        return "multi_image".equals(mediaType);
    }
    
    // 获取有效的图片数量（用于多图显示）
    public int getImageCount() {
        if (isVideo()) return 0;
        int count = 0;
        if (imageUrl != null && !imageUrl.isEmpty()) count++;
        if (imageUrl2 != null && !imageUrl2.isEmpty()) count++;
        if (imageUrl3 != null && !imageUrl3.isEmpty()) count++;
        return count;
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
    
    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }
    
    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }
    
    public void setVideoCoverUrl(String videoCoverUrl) {
        this.videoCoverUrl = videoCoverUrl;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
