package com.example.newsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新闻数据模型
 * 
 * 使用 Lombok 注解简化代码：
 * @Data - 自动生成 getter、setter、toString、equals、hashCode
 * @NoArgsConstructor - 生成无参构造函数
 * @AllArgsConstructor - 生成全参构造函数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsItem {
    
    /**
     * 新闻标题
     */
    private String title;
    
    /**
     * 新闻摘要
     */
    private String summary;
    
    /**
     * 图片 URL
     */
    private String imageUrl;
    
    /**
     * 发布时间
     */
    private String publishTime;
    
    /**
     * 阅读数
     */
    private String readCount;
}
