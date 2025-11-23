package com.example.newsapp.controller;

import com.example.newsapp.model.News;
import com.example.newsapp.model.NewsItem;
import com.example.newsapp.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新闻 API 控制器
 *
 * @RestController - 标识这是一个 REST API 控制器
 * @RequestMapping - 定义基础路径
 * @CrossOrigin - 允许跨域请求（允许 Android 应用访问）
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NewsController {

    @Autowired
    private NewsService newsService;
    
    // 时间格式化器（只显示日期，不显示具体时间）
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取新闻列表
     *
     * @param category 分类代码（可选：tech, economy, sports, health, entertainment, education, environment, food）
     * @param offset 偏移量（默认0）
     * @param limit 每页数量（默认10）
     * @return 新闻列表（返回NewsItem格式以兼容Android端）
     */
    @GetMapping("/news")
    public List<NewsItem> getNewsList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        
        System.out.println("📰 查询新闻 - category: " + category + ", offset: " + offset + ", limit: " + limit);
        
        // 从数据库查询新闻
        List<News> newsList = newsService.getNewsList(category, offset, limit);
        
        // 转换为NewsItem格式（兼容Android端）
        List<NewsItem> result = newsList.stream().map(news -> {
            // 格式化阅读数
            String readCount = formatReadCount(news.getReadCount());
            
            // 格式化发布时间
            String publishTime = news.getPublishTime() != null ? 
                news.getPublishTime().format(TIME_FORMATTER) : "";
            
            return new NewsItem(
                news.getTitle(),
                news.getSummary(),
                news.getImageUrl(),
                publishTime,
                readCount
            );
        }).collect(Collectors.toList());
        
        System.out.println("✅ 返回 " + result.size() + " 条新闻");
        
        return result;
    }
    
    /**
     * 格式化阅读数
     */
    private String formatReadCount(Integer count) {
        if (count == null || count == 0) {
            return "0阅读";
        }
        if (count < 10000) {
            return count + "阅读";
        }
        double wan = count / 10000.0;
        return String.format("%.1f万阅读", wan);
    }
}
