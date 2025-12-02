package com.example.newsapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 新闻实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    /**
     * 新闻ID
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 摘要
     */
    private String summary;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 图片URL 2
     */
    private String imageUrl2;
    
    /**
     * 图片URL 3
     */
    private String imageUrl3;
    
    /**
     * 媒体类型 (single_image, triple_image, video)
     */
    private String mediaType;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 视频时长（秒）
     */
    private Integer videoDuration;
    
    /**
     * 视频封面URL
     */
    private String videoCoverUrl;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 阅读数
     */
    private Integer readCount;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 关联对象（用于查询时返回）
    /**
     * 分类信息
     */
    private Category category;
    
    /**
     * 用户信息
     */
    private User user;
    
    // 关联查询字段（用于直接映射查询结果）
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 分类代码
     */
    private String categoryCode;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
}
