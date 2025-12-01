-- 添加多媒体新闻数据
USE news_db;

-- 清空现有新闻数据，重新插入
DELETE FROM news;

-- 插入科技新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('人工智能技术取得重大突破', '最新研究表明，深度学习算法在图像识别领域达到了新的里程碑，准确率提升至99.8%，为自动驾驶、医疗诊断等应用奠定基础。', 'https://picsum.photos/400/200?random=1', NULL, NULL, 'single_image', NULL, 0, NULL, 1, 1, 120000, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('5G网络覆盖率突破90%【多图】', '全国主要城市5G网络覆盖率已突破90%，网络速度和稳定性显著提升，为数字经济发展提供强力支撑。', 'https://picsum.photos/400/200?random=11', 'https://picsum.photos/400/200?random=111', NULL, 'multi_image', NULL, 0, NULL, 1, 1, 85000, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
('量子计算机研发成功【视频】', '我国自主研发的量子计算机成功实现量子优越性，计算能力超越传统超级计算机，标志着量子科技发展新突破。', NULL, NULL, NULL, 'video', 'https://example.com/quantum-video.mp4', 180, 'https://picsum.photos/400/200?random=12', 1, 1, 95000, DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- 插入经济新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('GDP增长超预期', '第三季度GDP同比增长6.8%，超出市场预期，显示经济复苏势头强劲，消费和投资双轮驱动效果明显。', 'https://picsum.photos/400/200?random=2', NULL, NULL, 'single_image', NULL, 0, NULL, 2, 2, 78000, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
('电商直播带货火爆【多图】', '双11购物节期间，电商直播带货销售额创历史新高，头部主播单场销售额突破10亿元，新消费模式持续升温。', 'https://picsum.photos/400/200?random=21', 'https://picsum.photos/400/200?random=211', 'https://picsum.photos/400/200?random=212', 'multi_image', NULL, 0, NULL, 2, 2, 156000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('央行降准释放流动性【视频】', '央行宣布下调存款准备金率0.5个百分点，释放长期资金约1.2万亿元，支持实体经济发展。', NULL, NULL, NULL, 'video', 'https://example.com/bank-video.mp4', 150, 'https://picsum.photos/400/200?random=22', 2, 2, 134000, DATE_SUB(NOW(), INTERVAL 5 HOUR));

-- 插入体育新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('国足世预赛获胜', '国家足球队在世界杯预选赛中2:1战胜对手，取得关键胜利，为晋级世界杯决赛圈奠定基础。', 'https://picsum.photos/400/200?random=3', NULL, NULL, 'single_image', NULL, 0, NULL, 3, 3, 89000, DATE_SUB(NOW(), INTERVAL 8 HOUR)),
('CBA总决赛精彩对决【视频】', '本赛季CBA总决赛进入白热化阶段，两支球队实力相当，比赛悬念迭起，吸引了大量球迷关注。', NULL, NULL, NULL, 'video', 'https://example.com/cba-video.mp4', 200, 'https://picsum.photos/400/200?random=31', 3, 3, 98000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('马拉松赛事圆满举办【多图】', '本市举办的国际马拉松赛事吸引了来自50多个国家的选手参赛，赛事组织有序，获得各方好评。', 'https://picsum.photos/400/200?random=32', 'https://picsum.photos/400/200?random=321', NULL, 'multi_image', NULL, 0, NULL, 3, 3, 34000, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入健康新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('专家建议每天运动30分钟【视频】', '世界卫生组织最新指南指出，每天至少进行30分钟中等强度运动，可以显著降低心血管疾病风险。', NULL, NULL, NULL, 'video', 'https://example.com/exercise-video.mp4', 240, 'https://picsum.photos/400/200?random=4', 4, 4, 57000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('新型疫苗研发成功【多图】', '科研团队成功研发出新型疫苗，临床试验显示有效率达95%以上，为疾病预防提供了新的解决方案。', 'https://picsum.photos/400/200?random=41', 'https://picsum.photos/400/200?random=411', 'https://picsum.photos/400/200?random=412', 'multi_image', NULL, 0, NULL, 4, 4, 78000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('健康饮食指南发布', '营养专家发布最新健康饮食指南，强调均衡营养、适量摄入、多样化饮食的重要性。', 'https://picsum.photos/400/200?random=42', NULL, NULL, 'single_image', NULL, 0, NULL, 4, 4, 43000, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入娱乐新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('知名导演新片获国际大奖【视频】', '在刚刚结束的国际电影节上，华语导演的最新作品斩获最佳影片奖，这是华语电影的又一次突破。', NULL, NULL, NULL, 'video', 'https://example.com/movie-video.mp4', 210, 'https://picsum.photos/400/200?random=5', 5, 5, 234000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('热门综艺节目收视率创新高【多图】', '最新一期综艺节目收视率突破3%，成为本年度收视冠军，节目创新形式获得观众广泛好评。', 'https://picsum.photos/400/200?random=51', 'https://picsum.photos/400/200?random=511', NULL, 'multi_image', NULL, 0, NULL, 5, 5, 156000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('音乐节门票瞬间售罄', '即将举办的大型音乐节门票在开售5分钟内全部售罄，主办方表示将考虑增加场次。', 'https://picsum.photos/400/200?random=52', NULL, NULL, 'single_image', NULL, 0, NULL, 5, 5, 89000, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入教育新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('在线教育平台用户突破千万【多图】', '随着数字化教育的普及，国内领先的在线教育平台宣布注册用户突破1000万，课程覆盖全年龄段。', 'https://picsum.photos/400/200?random=6', 'https://picsum.photos/400/200?random=601', NULL, 'multi_image', NULL, 0, NULL, 6, 6, 42000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('高考改革方案公布【视频】', '教育部公布新一轮高考改革方案，强调综合素质评价，减轻学生负担，促进全面发展。', NULL, NULL, NULL, 'video', 'https://example.com/gaokao-video.mp4', 180, 'https://picsum.photos/400/200?random=61', 6, 6, 125000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('职业教育迎来发展机遇', '国家出台多项政策支持职业教育发展，产教融合、校企合作成为新趋势。', 'https://picsum.photos/400/200?random=62', NULL, NULL, 'single_image', NULL, 0, NULL, 6, 6, 38000, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入环保新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('多地启动碳中和试点项目【多图】', '为应对气候变化，全国多个城市启动碳中和试点项目，通过新能源、绿色建筑等措施实现减排目标。', 'https://picsum.photos/400/200?random=7', 'https://picsum.photos/400/200?random=701', 'https://picsum.photos/400/200?random=702', 'multi_image', NULL, 0, NULL, 7, 7, 68000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('垃圾分类成效显著【视频】', '实施垃圾分类政策两年来，城市生活垃圾回收利用率提升至45%，环境质量明显改善。', NULL, NULL, NULL, 'video', 'https://example.com/garbage-video.mp4', 160, 'https://picsum.photos/400/200?random=71', 7, 7, 52000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('湿地保护取得新成果', '国家湿地保护工程实施以来，湿地面积稳步增加，生物多样性得到有效保护。', 'https://picsum.photos/400/200?random=72', NULL, NULL, 'single_image', NULL, 0, NULL, 7, 7, 34000, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入美食新闻（混合类型）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('网红餐厅排队3小时仍火爆【多图】', '位于市中心的一家网红餐厅持续火爆，即使需要排队3小时，食客们依然络绎不绝，招牌菜品一位难求。', 'https://picsum.photos/400/200?random=8', 'https://picsum.photos/400/200?random=801', NULL, 'multi_image', NULL, 0, NULL, 8, 8, 91000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('地方特色美食节开幕【视频】', '为期一周的地方特色美食节盛大开幕，汇集了全国各地上百种特色小吃，吸引大量游客前来品尝。', NULL, NULL, NULL, 'video', 'https://example.com/food-festival-video.mp4', 200, 'https://picsum.photos/400/200?random=81', 8, 8, 76000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('米其林餐厅榜单发布', '最新米其林餐厅榜单发布，本市新增3家星级餐厅，餐饮业整体水平获得国际认可。', 'https://picsum.photos/400/200?random=82', NULL, NULL, 'single_image', NULL, 0, NULL, 8, 8, 105000, DATE_SUB(NOW(), INTERVAL 3 DAY));

SELECT 'Multimedia news data inserted successfully!' as status;
SELECT COUNT(*) as total_news, media_type, COUNT(*) as count_by_type FROM news GROUP BY media_type;
