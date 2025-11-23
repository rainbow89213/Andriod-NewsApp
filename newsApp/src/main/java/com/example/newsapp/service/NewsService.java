package com.example.newsapp.service;

import com.example.newsapp.mapper.NewsMapper;
import com.example.newsapp.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 新闻服务类
 */
@Service
public class NewsService {
    
    @Autowired
    private NewsMapper newsMapper;
    
    /**
     * 获取新闻列表
     *
     * @param categoryCode 分类代码（可选）
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 新闻列表
     */
    public List<News> getNewsList(String categoryCode, Integer offset, Integer limit) {
        return newsMapper.selectNewsList(categoryCode, offset, limit);
    }
    
    /**
     * 根据ID获取新闻详情
     *
     * @param id 新闻ID
     * @return 新闻详情
     */
    public News getNewsById(Long id) {
        return newsMapper.selectNewsById(id);
    }
    
    /**
     * 统计新闻总数
     *
     * @param categoryCode 分类代码（可选）
     * @return 新闻总数
     */
    public int countNews(String categoryCode) {
        return newsMapper.countNews(categoryCode);
    }
}
