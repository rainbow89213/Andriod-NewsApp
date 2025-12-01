package com.example.demo2.api;

import com.example.demo2.NewsItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * NewsApiService - 新闻 API 接口定义
 * 
 * 使用 Retrofit 定义 RESTful API 接口
 * Retrofit 会根据注解自动生成网络请求代码
 */
public interface NewsApiService {
    
    /**
     * 获取新闻列表（支持分页）
     * 
     * @GET 注解表示这是一个 GET 请求
     * @Query 注解用于添加查询参数
     * 
     * @param offset 偏移量（从第几条开始，默认0）
     * @param limit 每页数量（默认5）
     * @return 返回新闻列表的 Call 对象
     */
    @GET("api/news")
    Call<List<NewsItem>> getNewsList(@Query("offset") Integer offset, @Query("limit") int limit);
    
    /**
     * 【第8次修改】按分类获取新闻列表
     * 
     * @param category 分类代码（tech, economy, sports, health, entertainment, education, environment, food）
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 返回新闻列表的 Call 对象
     */
    @GET("api/news")
    Call<List<NewsItem>> getNewsListByCategory(
        @Query("category") String category,
        @Query("offset") Integer offset,
        @Query("limit") int limit
    );
}
