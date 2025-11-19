package com.example.demo2.api;

import com.example.demo2.NewsItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * NewsApiService - 新闻 API 接口定义
 * 
 * 使用 Retrofit 定义 RESTful API 接口
 * Retrofit 会根据注解自动生成网络请求代码
 */
public interface NewsApiService {
    
    /**
     * 获取新闻列表
     * 
     * @GET 注解表示这是一个 GET 请求
     * 参数是相对于 Base URL 的路径
     * 
     * @return 返回新闻列表的 Call 对象
     */
    @GET("api/news")
    Call<List<NewsItem>> getNewsList();
}
