-- 安全更新数据库表结构
USE news_db;

-- 检查并添加新字段（如果不存在）
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'news_db' 
     AND TABLE_NAME = 'news' 
     AND COLUMN_NAME = 'image_url_2') > 0,
    'SELECT "image_url_2 already exists" as status',
    'ALTER TABLE news ADD COLUMN image_url_2 VARCHAR(255) DEFAULT NULL COMMENT "第二张图片URL"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'news_db' 
     AND TABLE_NAME = 'news' 
     AND COLUMN_NAME = 'image_url_3') > 0,
    'SELECT "image_url_3 already exists" as status',
    'ALTER TABLE news ADD COLUMN image_url_3 VARCHAR(255) DEFAULT NULL COMMENT "第三张图片URL"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'news_db' 
     AND TABLE_NAME = 'news' 
     AND COLUMN_NAME = 'media_type') > 0,
    'SELECT "media_type already exists" as status',
    'ALTER TABLE news ADD COLUMN media_type VARCHAR(20) DEFAULT "single_image" COMMENT "媒体类型"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'news_db' 
     AND TABLE_NAME = 'news' 
     AND COLUMN_NAME = 'video_url') > 0,
    'SELECT "video_url already exists" as status',
    'ALTER TABLE news ADD COLUMN video_url VARCHAR(255) DEFAULT NULL COMMENT "视频URL"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'news_db' 
     AND TABLE_NAME = 'news' 
     AND COLUMN_NAME = 'video_duration') > 0,
    'SELECT "video_duration already exists" as status',
    'ALTER TABLE news ADD COLUMN video_duration INT DEFAULT 0 COMMENT "视频时长(秒)"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'news_db' 
     AND TABLE_NAME = 'news' 
     AND COLUMN_NAME = 'video_cover_url') > 0,
    'SELECT "video_cover_url already exists" as status',
    'ALTER TABLE news ADD COLUMN video_cover_url VARCHAR(255) DEFAULT NULL COMMENT "视频封面URL"'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有新闻的media_type字段
UPDATE news SET media_type = 'single_image' WHERE media_type IS NULL OR media_type = '';

-- 显示更新后的表结构
SELECT 'Database structure updated successfully!' as status;
DESCRIBE news;
