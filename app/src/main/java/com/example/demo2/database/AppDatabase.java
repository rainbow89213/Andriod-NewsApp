// 【第6次修改】新建文件：Room 数据库管理类
// 作用：管理数据库实例、版本和表，提供全局唯一的数据库访问入口
package com.example.demo2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * AppDatabase - 应用数据库类
 * 
 * @Database 注解：标识这是一个 Room 数据库
 * 
 * 参数说明：
 * - entities：数据库包含的表（实体类）数组
 * - version：数据库版本号，用于数据库升级
 * - exportSchema：是否导出数据库架构（默认 true）
 * 
 * 数据库版本管理：
 * - 首次创建：version = 1
 * - 修改表结构后：version++（如 2, 3, 4...）
 * - Room 会自动处理数据库升级
 * 
 * 为什么使用抽象类？
 * - Room 会在编译时生成实现类（AppDatabase_Impl）
 * - 我们只需要定义抽象方法，Room 自动实现
 */
@Database(
    entities = {CachedNews.class},  // 数据库包含的表
    version = 1,                     // 数据库版本号
    exportSchema = false             // 不导出架构文件（简化配置）
)
public abstract class AppDatabase extends RoomDatabase {
    
    // ==================== 数据库名称 ====================
    
    /**
     * 数据库文件名
     * 
     * 完整路径：/data/data/com.example.demo2/databases/news_database.db
     * 
     * 命名规范：
     * - 使用小写字母和下划线
     * - 以 .db 结尾
     * - 见名知意
     */
    private static final String DATABASE_NAME = "news_database.db";
    
    // ==================== 单例模式 ====================
    
    /**
     * 数据库实例（单例）
     * 
     * volatile 关键字：
     * - 保证多线程环境下的可见性
     * - 防止指令重排序
     * 
     * 为什么使用单例？
     * - 数据库实例创建开销大
     * - 避免多个实例导致的数据不一致
     * - 节省内存
     */
    private static volatile AppDatabase INSTANCE;
    
    // ==================== 抽象方法 ====================
    
    /**
     * 获取 NewsDao 实例
     * 
     * Room 会自动实现这个方法
     * 
     * @return NewsDao 实例
     * 
     * 使用示例：
     * AppDatabase db = AppDatabase.getInstance(context);
     * NewsDao newsDao = db.newsDao();
     * List<CachedNews> newsList = newsDao.getAllCachedNews(10);
     */
    public abstract NewsDao newsDao();
    
    // 如果有更多的表，可以添加更多的 DAO 方法
    // 例如：
    // public abstract UserDao userDao();
    // public abstract CommentDao commentDao();
    
    // ==================== 获取数据库实例（单例模式）====================
    
    /**
     * 获取数据库实例（线程安全的单例模式）
     * 
     * 使用双重检查锁定（Double-Checked Locking）：
     * 1. 第一次检查：避免不必要的同步
     * 2. 同步块：确保线程安全
     * 3. 第二次检查：防止多次创建实例
     * 
     * @param context 上下文对象（用于创建数据库）
     * @return 数据库实例
     * 
     * 使用示例：
     * AppDatabase database = AppDatabase.getInstance(this);
     */
    public static AppDatabase getInstance(Context context) {
        // 第一次检查：如果实例已存在，直接返回（避免同步开销）
        if (INSTANCE == null) {
            // 同步块：确保只有一个线程能创建实例
            synchronized (AppDatabase.class) {
                // 第二次检查：防止多个线程同时通过第一次检查
                if (INSTANCE == null) {
                    // 创建数据库实例
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),  // 使用 Application Context（避免内存泄漏）
                        AppDatabase.class,                 // 数据库类
                        DATABASE_NAME                      // 数据库文件名
                    )
                    // 可选配置：
                    // .fallbackToDestructiveMigration()  // 升级失败时删除旧数据库（开发阶段使用）
                    // .addMigrations(MIGRATION_1_2)      // 添加数据库迁移策略
                    // .allowMainThreadQueries()          // 允许主线程查询（不推荐，仅用于测试）
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    // ==================== 关闭数据库 ====================
    
    /**
     * 关闭数据库连接
     * 
     * 使用场景：
     * - 应用退出时
     * - 测试完成后
     * 
     * 注意：关闭后需要重新获取实例才能使用
     * 
     * 使用示例：
     * AppDatabase.closeDatabase();
     */
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
    
    // ==================== 数据库迁移（可选）====================
    
    /**
     * 数据库迁移示例
     * 
     * 当数据库版本升级时（如从 version 1 升级到 version 2），
     * 需要定义迁移策略来保留旧数据
     * 
     * 示例：添加新列
     * 
     * static final Migration MIGRATION_1_2 = new Migration(1, 2) {
     *     @Override
     *     public void migrate(@NonNull SupportSQLiteDatabase database) {
     *         // 在 cached_news 表中添加新列 category
     *         database.execSQL("ALTER TABLE cached_news ADD COLUMN category TEXT");
     *     }
     * };
     * 
     * 使用方法：
     * INSTANCE = Room.databaseBuilder(...)
     *     .addMigrations(MIGRATION_1_2)
     *     .build();
     */
}

/**
 * AppDatabase 使用指南：
 * 
 * 1. 获取数据库实例：
 *    AppDatabase db = AppDatabase.getInstance(context);
 * 
 * 2. 获取 DAO：
 *    NewsDao newsDao = db.newsDao();
 * 
 * 3. 执行数据库操作（必须在子线程）：
 *    new Thread(() -> {
 *        List<CachedNews> newsList = newsDao.getAllCachedNews(10);
 *        // 处理数据...
 *    }).start();
 * 
 * 4. 更新 UI（必须在主线程）：
 *    runOnUiThread(() -> {
 *        // 更新 UI...
 *    });
 * 
 * 完整示例：
 * 
 * // 在 MainActivity 中
 * AppDatabase database = AppDatabase.getInstance(this);
 * 
 * // 在子线程查询数据
 * new Thread(() -> {
 *     List<CachedNews> cachedNews = database.newsDao().getAllCachedNews(10);
 *     
 *     // 在主线程更新 UI
 *     runOnUiThread(() -> {
 *         if (!cachedNews.isEmpty()) {
 *             // 显示缓存数据
 *             updateUI(cachedNews);
 *         }
 *     });
 * }).start();
 * 
 * 注意事项：
 * 
 * 1. 线程安全：
 *    - getInstance() 使用双重检查锁定，线程安全
 *    - 数据库操作必须在子线程执行
 * 
 * 2. 内存泄漏：
 *    - 使用 context.getApplicationContext() 避免内存泄漏
 *    - 不要持有 Activity 的引用
 * 
 * 3. 数据库升级：
 *    - 修改表结构后，version++
 *    - 定义 Migration 保留旧数据
 *    - 或使用 fallbackToDestructiveMigration() 删除旧数据
 * 
 * 4. 性能优化：
 *    - 使用单例模式，避免重复创建
 *    - 批量操作使用事务（@Transaction）
 *    - 合理使用索引（@Index）
 */
