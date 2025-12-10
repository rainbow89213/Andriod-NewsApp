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
}
