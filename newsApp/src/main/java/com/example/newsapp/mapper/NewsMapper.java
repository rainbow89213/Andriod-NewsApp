package com.example.newsapp.mapper;

import com.example.newsapp.model.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻Mapper接口
 */
@Mapper
public interface NewsMapper {
    
    /**
     * 查询所有新闻（带分页和分类筛选）
     *
     * @param categoryCode 分类代码（可选）
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 新闻列表
     */
    List<News> selectNewsList(@Param("categoryCode") String categoryCode,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);
    
    /**
     * 根据ID查询新闻详情
     *
     * @param id 新闻ID
     * @return 新闻详情
     */
    News selectNewsById(@Param("id") Long id);
    
    /**
     * 统计新闻总数
     *
     * @param categoryCode 分类代码（可选）
     * @return 新闻总数
     */
    int countNews(@Param("categoryCode") String categoryCode);
    
    /**
     * 插入新闻
     *
     * @param news 新闻对象
     * @return 影响行数
     */
    int insertNews(News news);
    
    /**
     * 更新新闻
     *
     * @param news 新闻对象
     * @return 影响行数
     */
    int updateNews(News news);
    
    /**
     * 删除新闻
     *
     * @param id 新闻ID
     * @return 影响行数
     */
    int deleteNews(@Param("id") Long id);
}
