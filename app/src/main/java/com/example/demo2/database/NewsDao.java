// 【第6次修改】新建文件：DAO（Data Access Object）数据访问对象
// 作用：定义数据库操作方法（增删改查），Room 会自动实现这些方法
package com.example.demo2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * NewsDao - 新闻数据访问对象
 * 
 * @Dao 注解：标识这是一个 DAO 接口
 * Room 会在编译时自动生成这个接口的实现类
 * 
 * DAO 的作用：
 * 1. 定义数据库操作方法
 * 2. 使用注解（@Query, @Insert 等）声明 SQL 操作
 * 3. Room 自动生成实现代码
 * 
 * 为什么使用 DAO？
 * - 类型安全：编译时检查 SQL 语句
 * - 简洁：不需要写大量的数据库操作代码
 * - 易维护：SQL 语句集中管理
 */
@Dao
public interface NewsDao {
    
    // ==================== 查询操作 ====================
    
    /**
     * 查询所有缓存的新闻（按缓存时间倒序）
     * 
     * @Query 注解：执行 SQL 查询
     * :limit 是参数占位符，对应方法参数 limit
     * 
     * SQL 解释：
     * - SELECT * FROM cached_news：查询 cached_news 表的所有列
     * - ORDER BY cacheTime DESC：按 cacheTime 降序排列（最新的在前）
     * - LIMIT :limit：限制返回的行数
     * 
     * @param limit 最多返回多少条数据
     * @return 新闻列表
     * 
     * 使用示例：
     * List<CachedNews> newsList = newsDao.getAllCachedNews(10);
     */
    @Query("SELECT * FROM cached_news ORDER BY cacheTime DESC LIMIT :limit")
    List<CachedNews> getAllCachedNews(int limit);
    
    /**
     * 根据分类查询缓存的新闻
     * 
     * @Query 注解：执行 SQL 查询
     * :category 是参数占位符，对应方法参数 category
     * 
     * SQL 解释：
     * - SELECT * FROM cached_news：查询 cached_news 表的所有列
     * - WHERE category = :category：筛选指定分类的新闻
     * - ORDER BY cacheTime DESC：按 cacheTime 降序排列（最新的在前）
     * - LIMIT :limit：限制返回的行数
     * 
     * @param category 分类代码（tech, economy, sports 等）
     * @param limit 最多返回多少条数据
     * @return 指定分类的新闻列表
     * 
     * 使用示例：
     * List<CachedNews> techNews = newsDao.getCachedNewsByCategory("tech", 10);
     */
    @Query("SELECT * FROM cached_news WHERE category = :category ORDER BY cacheTime DESC LIMIT :limit")
    List<CachedNews> getCachedNewsByCategory(String category, int limit);
    
    /**
     * 根据 ID 查询单条新闻
     * 
     * @param id 新闻ID
     * @return 新闻对象，如果不存在返回 null
     * 
     * 使用示例：
     * CachedNews news = newsDao.getNewsById(1);
     */
    @Query("SELECT * FROM cached_news WHERE id = :id")
    CachedNews getNewsById(int id);
    
    /**
     * 查询所有缓存的新闻（不限制数量）
     * 
     * @return 所有缓存的新闻列表
     * 
     * 使用示例：
     * List<CachedNews> allNews = newsDao.getAllNews();
     */
    @Query("SELECT * FROM cached_news ORDER BY cacheTime DESC")
    List<CachedNews> getAllNews();
    
    /**
     * 统计缓存的新闻数量
     * 
     * @return 缓存的新闻总数
     * 
     * 使用示例：
     * int count = newsDao.getNewsCount();
     */
    @Query("SELECT COUNT(*) FROM cached_news")
    int getNewsCount();
    
    // ==================== 插入操作 ====================
    
    /**
     * 插入新闻列表（批量插入）
     * 
     * @Insert 注解：执行插入操作
     * onConflict = OnConflictStrategy.REPLACE：
     * - 如果插入的数据主键已存在，则替换旧数据
     * - 其他策略：IGNORE（忽略）、ABORT（中止）、FAIL（失败）
     * 
     * 为什么使用 REPLACE？
     * - 确保缓存数据是最新的
     * - 避免重复数据
     * 
     * @param newsList 要插入的新闻列表
     * 
     * 使用示例：
     * List<CachedNews> newsList = new ArrayList<>();
     * newsList.add(new CachedNews(...));
     * newsDao.insertNews(newsList);
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNews(List<CachedNews> newsList);
    
    /**
     * 插入单条新闻
     * 
     * @param news 要插入的新闻对象
     * @return 插入的行 ID
     * 
     * 使用示例：
     * CachedNews news = new CachedNews(...);
     * long rowId = newsDao.insertSingleNews(news);
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSingleNews(CachedNews news);
    
    // ==================== 删除操作 ====================
    
    /**
     * 清空所有缓存
     * 
     * 使用场景：
     * - 用户点击"清空缓存"按钮
     * - 退出登录时清理数据
     * 
     * 使用示例：
     * newsDao.clearAllCache();
     */
    @Query("DELETE FROM cached_news")
    void clearAllCache();
    
    /**
     * 删除过期的缓存
     * 
     * 删除缓存时间早于 expireTime 的所有新闻
     * 
     * 使用场景：
     * - 定期清理过期缓存（如 7 天前的数据）
     * - 控制缓存大小
     * 
     * @param expireTime 过期时间戳（毫秒）
     * @return 删除的行数
     * 
     * 使用示例：
     * // 删除 7 天前的缓存
     * long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
     * int deletedCount = newsDao.deleteExpiredCache(sevenDaysAgo);
     */
    @Query("DELETE FROM cached_news WHERE cacheTime < :expireTime")
    int deleteExpiredCache(long expireTime);
    
    /**
     * 根据 ID 删除新闻
     * 
     * @param id 要删除的新闻ID
     * @return 删除的行数（0 或 1）
     * 
     * 使用示例：
     * int deleted = newsDao.deleteNewsById(1);
     */
    @Query("DELETE FROM cached_news WHERE id = :id")
    int deleteNewsById(int id);
    
    // ==================== 更新操作 ====================
    
    /**
     * 更新新闻的缓存时间
     * 
     * 使用场景：
     * - 刷新缓存时更新时间戳
     * 
     * @param id 新闻ID
     * @param newCacheTime 新的缓存时间戳
     * @return 更新的行数
     * 
     * 使用示例：
     * long currentTime = System.currentTimeMillis();
     * newsDao.updateCacheTime(1, currentTime);
     */
    @Query("UPDATE cached_news SET cacheTime = :newCacheTime WHERE id = :id")
    int updateCacheTime(int id, long newCacheTime);
}

/**
 * DAO 使用注意事项：
 * 
 * 1. 线程安全：
 *    - Room 不允许在主线程执行数据库操作（会抛出异常）
 *    - 必须在子线程或使用协程
 * 
 * 2. 返回值：
 *    - @Query 返回 List、单个对象或基本类型
 *    - @Insert 返回插入的行 ID（long）或行 ID 数组
 *    - @Delete/@Update 返回影响的行数（int）
 * 
 * 3. 参数绑定：
 *    - 使用 :参数名 绑定方法参数
 *    - 参数名必须与方法参数名一致
 * 
 * 4. SQL 语法：
 *    - 使用标准 SQLite 语法
 *    - 表名和列名必须与 Entity 定义一致
 * 
 * 5. 编译时检查：
 *    - Room 会在编译时验证 SQL 语句
 *    - 如果 SQL 有错误，编译会失败
 */
