-- 为所有分类添加更多测试数据（每个分类10条）
-- 请在执行data.sql后执行此文件

-- 体育新闻（补充到10条，原有3条，再加7条）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('奥运会筹备工作进展顺利', '各项场馆建设和赛事准备工作有序推进，志愿者招募已完成80%。', 'https://picsum.photos/400/200?random=33', NULL, NULL, 'single_image', NULL, 0, NULL, 3, 3, 45000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('网球大满贯激战正酣', '四大满贯赛事精彩纷呈，新生代选手表现抢眼。', 'https://picsum.photos/400/200?random=34', 'https://picsum.photos/400/200?random=341', NULL, 'multi_image', NULL, 0, NULL, 3, 3, 56000, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('游泳世锦赛破纪录', '中国游泳队在世锦赛上斩获多枚金牌，打破两项世界纪录。', 'https://picsum.photos/400/200?random=35', NULL, NULL, 'single_image', NULL, 0, NULL, 3, 3, 67000, DATE_SUB(NOW(), INTERVAL 9 HOUR)),
('篮球联赛改革方案出台', '职业篮球联赛将进行重大改革，增加球队数量，延长赛季。', 'https://picsum.photos/400/200?random=36', NULL, NULL, 'single_image', NULL, 0, NULL, 3, 3, 38000, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
('羽毛球世界排名更新', '国羽多名选手进入世界前十，团体实力稳步提升。', 'https://picsum.photos/400/200?random=37', 'https://picsum.photos/400/200?random=371', 'https://picsum.photos/400/200?random=372', 'multi_image', NULL, 0, NULL, 3, 3, 42000, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('乒乓球世界杯开赛', '世界顶尖选手齐聚一堂，国乒志在包揽全部金牌。', 'https://picsum.photos/400/200?random=38', NULL, NULL, 'single_image', NULL, 0, NULL, 3, 3, 51000, DATE_SUB(NOW(), INTERVAL 21 HOUR)),
('足球青训体系升级', '全国足球青训中心增至50个，青少年足球人口突破百万。', 'https://picsum.photos/400/200?random=39', NULL, NULL, 'single_image', NULL, 0, NULL, 3, 3, 29000, DATE_SUB(NOW(), INTERVAL 24 HOUR));

-- 健康新闻（补充到10条，原有3条，再加7条）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('睡眠质量影响健康', '研究表明，保证7-8小时高质量睡眠对身心健康至关重要。', 'https://picsum.photos/400/200?random=43', NULL, NULL, 'single_image', NULL, 0, NULL, 4, 4, 34000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('心理健康受重视', '企业纷纷设立心理咨询室，关注员工心理健康。', 'https://picsum.photos/400/200?random=44', 'https://picsum.photos/400/200?random=441', NULL, 'multi_image', NULL, 0, NULL, 4, 4, 28000, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('中医药发展迎新机遇', '中医药现代化进程加快，国际认可度不断提升。', 'https://picsum.photos/400/200?random=45', NULL, NULL, 'single_image', NULL, 0, NULL, 4, 4, 39000, DATE_SUB(NOW(), INTERVAL 9 HOUR)),
('儿童健康管理升级', '智能化儿童健康管理系统在多地推广应用。', 'https://picsum.photos/400/200?random=46', NULL, NULL, 'single_image', NULL, 0, NULL, 4, 4, 25000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('慢病管理新模式', '社区慢病管理中心建设加快，服务能力显著提升。', 'https://picsum.photos/400/200?random=47', 'https://picsum.photos/400/200?random=471', 'https://picsum.photos/400/200?random=472', 'multi_image', NULL, 0, NULL, 4, 4, 31000, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
('养老服务体系完善', '医养结合模式在全国推广，老年人健康保障水平提高。', 'https://picsum.photos/400/200?random=48', NULL, NULL, 'single_image', NULL, 0, NULL, 4, 4, 36000, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('健身行业快速发展', '健身房数量突破10万家，全民健身意识增强。', 'https://picsum.photos/400/200?random=49', NULL, NULL, 'single_image', NULL, 0, NULL, 4, 4, 41000, DATE_SUB(NOW(), INTERVAL 21 HOUR));

-- 娱乐新闻（补充到10条，原有3条，再加7条）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('春节档电影预售火爆', '多部大片定档春节，预售票房已突破5亿元。', 'https://picsum.photos/400/200?random=53', NULL, NULL, 'single_image', NULL, 0, NULL, 5, 5, 178000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('音乐产业数字化转型', '数字音乐平台用户突破8亿，付费用户快速增长。', 'https://picsum.photos/400/200?random=54', 'https://picsum.photos/400/200?random=541', NULL, 'multi_image', NULL, 0, NULL, 5, 5, 92000, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('短视频创作者大会举办', '千万粉丝创作者分享经验，行业发展趋势明朗。', 'https://picsum.photos/400/200?random=55', NULL, NULL, 'single_image', NULL, 0, NULL, 5, 5, 103000, DATE_SUB(NOW(), INTERVAL 9 HOUR)),
('游戏产业收入创新高', '游戏产业年收入突破3000亿，手游占比超70%。', 'https://picsum.photos/400/200?random=56', NULL, NULL, 'single_image', NULL, 0, NULL, 5, 5, 87000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('文化演出市场复苏', '话剧、音乐会等演出场次大幅增加，票房收入翻倍。', 'https://picsum.photos/400/200?random=57', 'https://picsum.photos/400/200?random=571', 'https://picsum.photos/400/200?random=572', 'multi_image', NULL, 0, NULL, 5, 5, 76000, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
('网络文学改编热潮', '热门网文IP改编影视剧频出，带动原著销量激增。', 'https://picsum.photos/400/200?random=58', NULL, NULL, 'single_image', NULL, 0, NULL, 5, 5, 94000, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('直播带货模式创新', '娱乐明星纷纷入局直播带货，销售额屡创新高。', 'https://picsum.photos/400/200?random=59', NULL, NULL, 'single_image', NULL, 0, NULL, 5, 5, 112000, DATE_SUB(NOW(), INTERVAL 21 HOUR));

-- 教育新闻（补充到10条，原有3条，再加7条）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('高考改革方案发布', '新高考方案在更多省份实施，选科模式更加灵活。', 'https://picsum.photos/400/200?random=63', NULL, NULL, 'single_image', NULL, 0, NULL, 6, 6, 58000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('职业教育迎来春天', '职业教育投入大幅增加，校企合作模式不断创新。', 'https://picsum.photos/400/200?random=64', 'https://picsum.photos/400/200?random=641', NULL, 'multi_image', NULL, 0, NULL, 6, 6, 35000, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('AI教育应用普及', '人工智能辅助教学系统在中小学广泛应用。', 'https://picsum.photos/400/200?random=65', NULL, NULL, 'single_image', NULL, 0, NULL, 6, 6, 46000, DATE_SUB(NOW(), INTERVAL 9 HOUR)),
('留学市场逐步恢复', '出国留学人数回升，热门留学国家签证政策放宽。', 'https://picsum.photos/400/200?random=66', NULL, NULL, 'single_image', NULL, 0, NULL, 6, 6, 29000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('素质教育改革深化', '艺术、体育纳入中考，全面发展理念深入人心。', 'https://picsum.photos/400/200?random=67', 'https://picsum.photos/400/200?random=671', 'https://picsum.photos/400/200?random=672', 'multi_image', NULL, 0, NULL, 6, 6, 37000, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
('教师待遇持续提升', '教师平均工资水平不断提高，职业吸引力增强。', 'https://picsum.photos/400/200?random=68', NULL, NULL, 'single_image', NULL, 0, NULL, 6, 6, 41000, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('终身学习体系构建', '成人教育、老年大学蓬勃发展，学习型社会加快建设。', 'https://picsum.photos/400/200?random=69', NULL, NULL, 'single_image', NULL, 0, NULL, 6, 6, 33000, DATE_SUB(NOW(), INTERVAL 21 HOUR));

-- 环保新闻（补充到10条，原有3条，再加7条）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('碳中和目标稳步推进', '各地制定碳达峰碳中和路线图，绿色转型加速。', 'https://picsum.photos/400/200?random=73', NULL, NULL, 'single_image', NULL, 0, NULL, 7, 7, 27000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('新能源发电占比提升', '风电、光伏发电量创新高，清洁能源占比超40%。', 'https://picsum.photos/400/200?random=74', 'https://picsum.photos/400/200?random=741', NULL, 'multi_image', NULL, 0, NULL, 7, 7, 31000, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('垃圾分类成效显著', '垃圾分类覆盖率达95%，资源回收利用率大幅提高。', 'https://picsum.photos/400/200?random=75', NULL, NULL, 'single_image', NULL, 0, NULL, 7, 7, 23000, DATE_SUB(NOW(), INTERVAL 9 HOUR)),
('生态修复工程推进', '湿地、森林生态修复面积扩大，生物多样性保护加强。', 'https://picsum.photos/400/200?random=76', NULL, NULL, 'single_image', NULL, 0, NULL, 7, 7, 19000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('绿色交通体系建设', '新能源公交车占比超80%，充电桩数量快速增长。', 'https://picsum.photos/400/200?random=77', 'https://picsum.photos/400/200?random=771', 'https://picsum.photos/400/200?random=772', 'multi_image', NULL, 0, NULL, 7, 7, 26000, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
('环保产业规模扩大', '环保产业产值突破10万亿，成为经济新增长点。', 'https://picsum.photos/400/200?random=78', NULL, NULL, 'single_image', NULL, 0, NULL, 7, 7, 29000, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('公众环保意识增强', '绿色消费理念普及，环保志愿者队伍不断壮大。', 'https://picsum.photos/400/200?random=79', NULL, NULL, 'single_image', NULL, 0, NULL, 7, 7, 24000, DATE_SUB(NOW(), INTERVAL 21 HOUR));

-- 美食新闻（补充到10条，原有3条，再加7条）
INSERT INTO news (title, summary, image_url, image_url_2, image_url_3, media_type, video_url, video_duration, video_cover_url, category_id, user_id, read_count, publish_time) VALUES
('米其林餐厅榜单发布', '新一期米其林指南发布，多家本土餐厅上榜。', 'https://picsum.photos/400/200?random=83', NULL, NULL, 'single_image', NULL, 0, NULL, 8, 8, 67000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
('预制菜产业快速发展', '预制菜市场规模突破4000亿，便利性受消费者青睐。', 'https://picsum.photos/400/200?random=84', 'https://picsum.photos/400/200?random=841', NULL, 'multi_image', NULL, 0, NULL, 8, 8, 42000, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('地方美食节举办', '各地美食节活动丰富，带动旅游消费增长。', 'https://picsum.photos/400/200?random=85', NULL, NULL, 'single_image', NULL, 0, NULL, 8, 8, 38000, DATE_SUB(NOW(), INTERVAL 9 HOUR)),
('网红餐厅打卡热', '社交媒体带动网红餐厅兴起，排队现象普遍。', 'https://picsum.photos/400/200?random=86', NULL, NULL, 'single_image', NULL, 0, NULL, 8, 8, 51000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('健康饮食理念流行', '轻食、素食餐厅增多，营养搭配受重视。', 'https://picsum.photos/400/200?random=87', 'https://picsum.photos/400/200?random=871', 'https://picsum.photos/400/200?random=872', 'multi_image', NULL, 0, NULL, 8, 8, 35000, DATE_SUB(NOW(), INTERVAL 15 HOUR)),
('外卖市场持续扩大', '外卖用户突破5亿，配送效率不断提升。', 'https://picsum.photos/400/200?random=88', NULL, NULL, 'single_image', NULL, 0, NULL, 8, 8, 46000, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
('传统美食创新发展', '老字号推陈出新，传统美食焕发新活力。', 'https://picsum.photos/400/200?random=89', NULL, NULL, 'single_image', NULL, 0, NULL, 8, 8, 39000, DATE_SUB(NOW(), INTERVAL 21 HOUR));
