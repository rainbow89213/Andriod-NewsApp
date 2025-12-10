USE news_db;

-- 插入分类数据
INSERT INTO category (name, code, sort_order) VALUES
('科技', 'tech', 1),
('经济', 'economy', 2),
('体育', 'sports', 3),
('健康', 'health', 4),
('娱乐', 'entertainment', 5),
('教育', 'education', 6),
('环保', 'environment', 7),
('美食', 'food', 8);

-- 插入用户数据
INSERT INTO user (username, nickname, avatar, email) VALUES
('tech_reporter', '科技记者小王', 'https://picsum.photos/100/100?random=1', 'tech@news.com'),
('economy_expert', '经济专家李明', 'https://picsum.photos/100/100?random=2', 'economy@news.com'),
('sports_fan', '体育迷张三', 'https://picsum.photos/100/100?random=3', 'sports@news.com'),
('health_doctor', '健康医生刘芳', 'https://picsum.photos/100/100?random=4', 'health@news.com'),
('entertainment_editor', '娱乐编辑赵敏', 'https://picsum.photos/100/100?random=5', 'entertainment@news.com'),
('education_teacher', '教育老师陈华', 'https://picsum.photos/100/100?random=6', 'education@news.com'),
('environment_activist', '环保志愿者王芳', 'https://picsum.photos/100/100?random=7', 'environment@news.com'),
('food_blogger', '美食博主李雷', 'https://picsum.photos/100/100?random=8', 'food@news.com');

-- 插入科技新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('人工智能技术取得重大突破', '最新研究表明，深度学习算法在图像识别领域达到了新的里程碑，准确率提升至99.8%，为自动驾驶和医疗诊断带来新希望。', 'https://picsum.photos/400/200?random=1', 1, 1, 12000, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('5G网络覆盖率突破90%', '全国主要城市5G网络覆盖率已突破90%，网络速度和稳定性大幅提升，为物联网和智慧城市建设奠定基础。', 'https://picsum.photos/400/200?random=11', 1, 1, 8500, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
('量子计算机研发获新进展', '我国科研团队在量子计算机研发方面取得重大突破，量子比特数量达到新高度，计算能力大幅提升。', 'https://picsum.photos/400/200?random=12', 1, 1, 15000, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入经济新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('全球市场迎来复苏信号', '多个国际机构发布报告显示，全球经济正在逐步恢复，预计今年GDP增长率将达到3.5%，消费者信心指数持续上升。', 'https://picsum.photos/400/200?random=2', 2, 2, 86000, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
('新能源汽车销量创新高', '今年前三季度新能源汽车销量同比增长120%，市场渗透率突破30%，产业链上下游企业业绩普遍向好。', 'https://picsum.photos/400/200?random=21', 2, 2, 45000, DATE_SUB(NOW(), INTERVAL 8 HOUR)),
('央行降准释放流动性', '央行宣布下调存款准备金率0.5个百分点，预计释放长期资金约5000亿元，支持实体经济发展。', 'https://picsum.photos/400/200?random=22', 2, 2, 67000, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入体育新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('国足晋级世界杯预选赛下一轮', '在昨晚进行的关键比赛中，国家队以2:1战胜对手，成功晋级世界杯预选赛下一轮，全国球迷欢欣鼓舞。', 'https://picsum.photos/400/200?random=3', 3, 3, 153000, DATE_SUB(NOW(), INTERVAL 8 HOUR)),
('CBA总决赛精彩对决', '本赛季CBA总决赛进入白热化阶段，两支球队实力相当，比赛悬念迭起，吸引了大量球迷关注。', 'https://picsum.photos/400/200?random=31', 3, 3, 98000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('马拉松赛事圆满举办', '本市举办的国际马拉松赛事吸引了来自50多个国家的选手参赛，赛事组织有序，获得各方好评。', 'https://picsum.photos/400/200?random=32', 3, 3, 34000, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入健康新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('专家建议每天运动30分钟', '世界卫生组织最新指南指出，每天至少进行30分钟中等强度运动，可以显著降低心血管疾病风险，提高生活质量。', 'https://picsum.photos/400/200?random=4', 4, 4, 57000, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('新型疫苗研发成功', '科研团队成功研发出新型疫苗，临床试验显示有效率达95%以上，为疾病预防提供了新的解决方案。', 'https://picsum.photos/400/200?random=41', 4, 4, 78000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('健康饮食指南发布', '营养专家发布最新健康饮食指南，强调均衡营养、适量摄入、多样化饮食的重要性。', 'https://picsum.photos/400/200?random=42', 4, 4, 43000, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入娱乐新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('知名导演新片获国际大奖', '在刚刚结束的国际电影节上，华语导演的最新作品斩获最佳影片奖，这是华语电影在国际舞台上的又一次突破。', 'https://picsum.photos/400/200?random=5', 5, 5, 234000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('热门综艺节目收视率创新高', '最新一期综艺节目收视率突破3%，成为本年度收视冠军，节目创新形式获得观众广泛好评。', 'https://picsum.photos/400/200?random=51', 5, 5, 156000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('音乐节门票瞬间售罄', '即将举办的大型音乐节门票在开售5分钟内全部售罄，主办方表示将考虑增加场次。', 'https://picsum.photos/400/200?random=52', 5, 5, 89000, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入教育新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('在线教育平台用户突破千万', '随着数字化教育的普及，国内领先的在线教育平台宣布注册用户突破1000万，课程覆盖从小学到职业培训的各个领域。', 'https://picsum.photos/400/200?random=6', 6, 6, 42000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('高考改革方案公布', '教育部公布新一轮高考改革方案，强调综合素质评价，减轻学生负担，促进全面发展。', 'https://picsum.photos/400/200?random=61', 6, 6, 125000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('职业教育迎来发展机遇', '国家出台多项政策支持职业教育发展，产教融合、校企合作成为新趋势。', 'https://picsum.photos/400/200?random=62', 6, 6, 38000, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入环保新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('多地启动碳中和试点项目', '为应对气候变化，全国多个城市启动碳中和试点项目，通过新能源、绿色建筑等措施，力争2030年实现碳达峰目标。', 'https://picsum.photos/400/200?random=7', 7, 7, 68000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('垃圾分类成效显著', '实施垃圾分类政策两年来，城市生活垃圾回收利用率提升至45%，环境质量明显改善。', 'https://picsum.photos/400/200?random=71', 7, 7, 52000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('湿地保护取得新成果', '国家湿地保护工程实施以来，湿地面积稳步增加，生物多样性得到有效保护。', 'https://picsum.photos/400/200?random=72', 7, 7, 34000, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入美食新闻
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('网红餐厅排队3小时仍火爆', '位于市中心的一家网红餐厅持续火爆，即使需要排队3小时，食客们依然络绎不绝，招牌菜品更是一位难求。', 'https://picsum.photos/400/200?random=8', 8, 8, 91000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('地方特色美食节开幕', '为期一周的地方特色美食节盛大开幕，汇集了全国各地上百种特色小吃，吸引大量游客前来品尝。', 'https://picsum.photos/400/200?random=81', 8, 8, 76000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('米其林餐厅榜单发布', '最新米其林餐厅榜单发布，本市新增3家星级餐厅，餐饮业整体水平获得国际认可。', 'https://picsum.photos/400/200?random=82', 8, 8, 105000, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 【扩展数据】每个分类添加更多新闻以测试分页功能
-- 科技新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('云计算服务市场规模突破千亿', '国内云计算市场持续增长，今年市场规模预计突破千亿元，企业上云成为数字化转型的重要选择。', 'https://picsum.photos/400/200?random=13', 1, 1, 25000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('芯片产业迎来突破性进展', '国产芯片在高端制程方面取得重大突破，打破技术封锁，为科技自立自强奠定基础。', 'https://picsum.photos/400/200?random=14', 1, 1, 32000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('智能家居市场爆发增长', '智能音箱、智能门锁等智能家居产品销量暴涨，市场规模同比增长65%。', 'https://picsum.photos/400/200?random=15', 1, 1, 18000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('新一代操作系统发布', '国产操作系统发布新版本，性能大幅提升，生态建设取得明显进展。', 'https://picsum.photos/400/200?random=16', 1, 1, 28000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('虚拟现实技术应用广泛', 'VR/AR技术在教育、医疗、娱乐等领域应用日益广泛，市场前景广阔。', 'https://picsum.photos/400/200?random=17', 1, 1, 22000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('区块链技术赋能实体经济', '区块链技术在供应链管理、数字资产等领域展现巨大潜力。', 'https://picsum.photos/400/200?random=18', 1, 1, 19000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('物联网设备数量超百亿', '全球物联网设备连接数突破100亿，智慧城市建设加速推进。', 'https://picsum.photos/400/200?random=19', 1, 1, 27000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('网络安全投入持续增加', '企业网络安全投入同比增长40%，数据安全和隐私保护受到重视。', 'https://picsum.photos/400/200?random=20', 1, 1, 21000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('自动驾驶测试里程创纪录', '自动驾驶汽车测试里程突破千万公里，技术成熟度不断提升。', 'https://picsum.photos/400/200?random=111', 1, 1, 35000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('人脸识别技术精度提升', '最新人脸识别算法准确率达到99.9%，应用场景更加广泛。', 'https://picsum.photos/400/200?random=112', 1, 1, 16000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('卫星互联网进入商用阶段', '低轨卫星互联网开始商用，偏远地区也能享受高速网络。', 'https://picsum.photos/400/200?random=113', 1, 1, 24000, DATE_SUB(NOW(), INTERVAL 13 DAY)),
('超级计算机性能再创新高', '我国超级计算机性能排名全球前列，为科研提供强大算力支持。', 'https://picsum.photos/400/200?random=114', 1, 1, 29000, DATE_SUB(NOW(), INTERVAL 14 DAY));

-- 经济新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('跨境电商交易额再创新高', '今年跨境电商交易额预计突破2万亿元，成为外贸新增长点。', 'https://picsum.photos/400/200?random=23', 2, 2, 52000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('消费市场持续复苏', '国庆假期消费数据显示市场活力强劲，餐饮、旅游等行业全面回暖。', 'https://picsum.photos/400/200?random=24', 2, 2, 48000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('房地产市场趋于稳定', '多地房价保持平稳，市场预期逐步回归理性，长效机制发挥作用。', 'https://picsum.photos/400/200?random=25', 2, 2, 61000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('外资企业持续看好中国市场', '今年前三季度外资投资额同比增长15%，显示国际资本对中国市场信心。', 'https://picsum.photos/400/200?random=26', 2, 2, 38000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('数字货币试点稳步推进', '数字人民币试点城市扩大，应用场景日益丰富，交易量持续增长。', 'https://picsum.photos/400/200?random=27', 2, 2, 44000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('制造业PMI重回扩张区间', '官方制造业PMI指数升至51.2，表明制造业景气度回升。', 'https://picsum.photos/400/200?random=28', 2, 2, 36000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('绿色金融规模快速增长', '绿色信贷、绿色债券等产品规模突破15万亿元，支持绿色发展。', 'https://picsum.photos/400/200?random=29', 2, 2, 29000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('民营经济发展环境持续优化', '多项政策措施支持民营企业发展，营商环境不断改善。', 'https://picsum.photos/400/200?random=210', 2, 2, 42000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('国际贸易总额逆势增长', '前三季度进出口总额同比增长8%，贸易结构进一步优化。', 'https://picsum.photos/400/200?random=211', 2, 2, 51000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('税收优惠政策助力企业发展', '新一轮减税降费政策落地，预计为企业减负超万亿元。', 'https://picsum.photos/400/200?random=212', 2, 2, 33000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('资本市场改革深化', '注册制改革全面推开，多层次资本市场体系不断完善。', 'https://picsum.photos/400/200?random=213', 2, 2, 47000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('居民收入稳步增长', '前三季度居民人均可支配收入同比增长6.5%，消费能力增强。', 'https://picsum.photos/400/200?random=214', 2, 2, 55000, DATE_SUB(NOW(), INTERVAL 13 DAY));

-- 体育新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('奥运选手刷新世界纪录', '在国际田径赛事中，我国选手刷新100米短跑世界纪录，成为亚洲之光。', 'https://picsum.photos/400/200?random=33', 3, 3, 125000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('全运会圆满落幕', '第十四届全国运动会圆满闭幕，各代表团奖牌榜竞争激烈。', 'https://picsum.photos/400/200?random=34', 3, 3, 87000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('冬奥会筹备工作进展顺利', '冬奥会场馆建设全部完工，各项测试赛有序进行。', 'https://picsum.photos/400/200?random=35', 3, 3, 92000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('青少年足球联赛启动', '覆盖全国的青少年足球联赛正式启动，助力足球人才培养。', 'https://picsum.photos/400/200?random=36', 3, 3, 54000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('游泳队斩获多枚金牌', '世界游泳锦标赛上，中国队表现出色，斩获多枚金牌。', 'https://picsum.photos/400/200?random=37', 3, 3, 78000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('网球公开赛精彩纷呈', '本年度网球公开赛吸引全球顶尖选手参赛，比赛精彩纷呈。', 'https://picsum.photos/400/200?random=38', 3, 3, 61000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('电竞产业规模破千亿', '电子竞技产业持续火爆，市场规模突破千亿元大关。', 'https://picsum.photos/400/200?random=39', 3, 3, 115000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('羽毛球世锦赛夺冠', '中国羽毛球队在世锦赛上包揽多项冠军，展现强大实力。', 'https://picsum.photos/400/200?random=310', 3, 3, 69000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('马术比赛首次落户中国', '国际顶级马术赛事首次在中国举办，推动马术运动发展。', 'https://picsum.photos/400/200?random=311', 3, 3, 42000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('自行车赛事吸引万人参与', '城市自行车赛吸引上万名骑行爱好者参与，倡导绿色出行。', 'https://picsum.photos/400/200?random=312', 3, 3, 38000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('滑雪场迎来客流高峰', '随着冬季来临，各大滑雪场迎来客流高峰，冰雪运动持续升温。', 'https://picsum.photos/400/200?random=313', 3, 3, 71000, DATE_SUB(NOW(), INTERVAL 13 DAY)),
('拳击赛事引发热议', '职业拳击赛事精彩对决，吸引大量拳击爱好者关注。', 'https://picsum.photos/400/200?random=314', 3, 3, 55000, DATE_SUB(NOW(), INTERVAL 14 DAY));

-- 健康新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('睡眠质量影响身体健康', '研究表明，良好的睡眠质量对身心健康至关重要，建议成年人每天睡眠7-8小时。', 'https://picsum.photos/400/200?random=43', 4, 4, 64000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('心理健康服务体系完善', '各地加强心理健康服务建设，为公众提供专业心理咨询。', 'https://picsum.photos/400/200?random=44', 4, 4, 48000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('中医药走向国际市场', '中医药产品和服务加速走向国际，在多个国家受到认可。', 'https://picsum.photos/400/200?random=45', 4, 4, 56000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('慢性病防控成效显著', '慢性病综合防控示范区建设取得成效，居民健康水平提升。', 'https://picsum.photos/400/200?random=46', 4, 4, 39000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('互联网医疗服务普及', '在线问诊、远程医疗等互联网医疗服务快速普及，方便群众就医。', 'https://picsum.photos/400/200?random=47', 4, 4, 72000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('儿童近视防控工作加强', '多部门联合开展儿童青少年近视防控工作，保护孩子视力健康。', 'https://picsum.photos/400/200?random=48', 4, 4, 61000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('癌症早筛技术突破', '新型癌症早期筛查技术问世，可提前发现多种癌症。', 'https://picsum.photos/400/200?random=49', 4, 4, 85000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('全民健身计划实施', '政府推出全民健身计划，建设更多公共体育设施。', 'https://picsum.photos/400/200?random=410', 4, 4, 44000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('营养师职业需求增长', '随着健康意识提升，营养师职业需求快速增长。', 'https://picsum.photos/400/200?random=411', 4, 4, 37000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('基因检测服务普及', '个人基因检测服务价格下降，越来越多人了解自身健康风险。', 'https://picsum.photos/400/200?random=412', 4, 4, 52000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('医保覆盖范围扩大', '医保目录新增多种药品和诊疗项目，惠及更多患者。', 'https://picsum.photos/400/200?random=413', 4, 4, 68000, DATE_SUB(NOW(), INTERVAL 13 DAY)),
('康复医疗体系建设加快', '各地加强康复医疗机构建设，满足患者康复需求。', 'https://picsum.photos/400/200?random=414', 4, 4, 41000, DATE_SUB(NOW(), INTERVAL 14 DAY));

-- 娱乐新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('国产电影票房创历史新高', '今年国产电影总票房突破600亿元，创历史最高纪录。', 'https://picsum.photos/400/200?random=53', 5, 5, 285000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('流行音乐榜单更新', '本周流行音乐榜单揭晓，多首新歌上榜受到欢迎。', 'https://picsum.photos/400/200?random=54', 5, 5, 178000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('电视剧收视率持续走高', '最新热播剧收视率节节攀升，话题讨论度居高不下。', 'https://picsum.photos/400/200?random=55', 5, 5, 196000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('明星公益活动获赞', '多位明星参与公益活动，传递社会正能量。', 'https://picsum.photos/400/200?random=56', 5, 5, 142000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('动漫产业蓬勃发展', '国产动漫作品质量提升，产业规模不断扩大。', 'https://picsum.photos/400/200?random=57', 5, 5, 124000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('短视频平台用户破10亿', '短视频平台用户数突破10亿，成为重要娱乐方式。', 'https://picsum.photos/400/200?random=58', 5, 5, 267000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('演唱会市场火爆', '各大演唱会一票难求，演出市场全面复苏。', 'https://picsum.photos/400/200?random=59', 5, 5, 189000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('综艺节目创新形式', '多档综艺节目推出创新形式，收视口碑双丰收。', 'https://picsum.photos/400/200?random=510', 5, 5, 163000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('话剧市场稳步增长', '话剧演出场次和观众人数持续增长，市场前景广阔。', 'https://picsum.photos/400/200?random=511', 5, 5, 95000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('直播带货成新趋势', '明星直播带货成为娱乐营销新趋势，销售额屡创新高。', 'https://picsum.photos/400/200?random=512', 5, 5, 218000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('电影节吸引全球关注', '国际电影节在国内举办，吸引全球电影人参与。', 'https://picsum.photos/400/200?random=513', 5, 5, 176000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('网络剧制作水平提升', '网络剧投资和制作水平大幅提升，精品剧集频出。', 'https://picsum.photos/400/200?random=514', 5, 5, 201000, DATE_SUB(NOW(), INTERVAL 13 DAY));

-- 教育新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('双减政策成效明显', '双减政策实施以来，学生课业负担明显减轻，家长满意度提升。', 'https://picsum.photos/400/200?random=63', 6, 6, 98000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('高校毕业生就业率稳定', '今年高校毕业生就业率保持稳定，就业质量持续提高。', 'https://picsum.photos/400/200?random=64', 6, 6, 76000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('乡村教师待遇提升', '多地提高乡村教师待遇，吸引优秀人才投身乡村教育。', 'https://picsum.photos/400/200?random=65', 6, 6, 54000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('智慧校园建设加速', '5G、AI等技术应用于教育，智慧校园建设全面加速。', 'https://picsum.photos/400/200?random=66', 6, 6, 62000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('留学市场逐步恢复', '随着疫情好转，出国留学市场逐步恢复活力。', 'https://picsum.photos/400/200?random=67', 6, 6, 48000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('素质教育理念深入人心', '素质教育理念得到广泛认同，学校课程更加多元化。', 'https://picsum.photos/400/200?random=68', 6, 6, 71000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('校外培训机构规范管理', '校外培训机构治理工作取得阶段性成果，市场秩序明显好转。', 'https://picsum.photos/400/200?random=69', 6, 6, 83000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('家庭教育促进法实施', '家庭教育促进法正式实施，引导家长科学育儿。', 'https://picsum.photos/400/200?random=610', 6, 6, 59000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('学前教育普及率提升', '学前三年毛入园率达到85%以上，学前教育资源更加充足。', 'https://picsum.photos/400/200?random=611', 6, 6, 44000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('研究生招生规模扩大', '高校研究生招生规模适度扩大，满足人才培养需求。', 'https://picsum.photos/400/200?random=612', 6, 6, 67000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('教师培训体系完善', '教师培训体系不断完善，提升教师专业能力。', 'https://picsum.photos/400/200?random=613', 6, 6, 52000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('国际教育交流深化', '与多国开展教育交流合作，引进优质教育资源。', 'https://picsum.photos/400/200?random=614', 6, 6, 46000, DATE_SUB(NOW(), INTERVAL 13 DAY));

-- 环保新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('新能源汽车推广成效显著', '新能源汽车保有量突破1000万辆，减排效果明显。', 'https://picsum.photos/400/200?random=73', 7, 7, 72000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('森林覆盖率持续提升', '全国森林覆盖率达到23.5%，生态环境持续改善。', 'https://picsum.photos/400/200?random=74', 7, 7, 56000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('海洋生态保护加强', '海洋保护区面积扩大，海洋生态环境逐步好转。', 'https://picsum.photos/400/200?random=75', 7, 7, 48000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('绿色建筑标准提升', '新版绿色建筑标准发布，推动建筑行业绿色转型。', 'https://picsum.photos/400/200?random=76', 7, 7, 39000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('水污染治理取得进展', '重点流域水质明显改善，黑臭水体基本消除。', 'https://picsum.photos/400/200?random=77', 7, 7, 63000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('野生动物保护力度加大', '濒危物种数量回升，野生动物保护成效显著。', 'https://picsum.photos/400/200?random=78', 7, 7, 54000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('可再生能源装机容量增长', '风电、光伏等可再生能源装机容量快速增长。', 'https://picsum.photos/400/200?random=79', 7, 7, 41000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('环保产业规模扩大', '环保产业总产值突破2万亿元，成为新的经济增长点。', 'https://picsum.photos/400/200?random=710', 7, 7, 58000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('土壤污染防治推进', '土壤污染防治法实施，土壤环境质量稳中向好。', 'https://picsum.photos/400/200?random=711', 7, 7, 37000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('空气质量明显改善', '重点城市PM2.5平均浓度下降，蓝天白云成为常态。', 'https://picsum.photos/400/200?random=712', 7, 7, 69000, DATE_SUB(NOW(), INTERVAL 13 DAY)),
('生物多样性保护成效', '生物多样性保护工作取得积极成效，物种数量增加。', 'https://picsum.photos/400/200?random=713', 7, 7, 45000, DATE_SUB(NOW(), INTERVAL 14 DAY)),
('节能减排目标达成', '单位GDP能耗下降，节能减排目标如期达成。', 'https://picsum.photos/400/200?random=714', 7, 7, 51000, DATE_SUB(NOW(), INTERVAL 15 DAY));

-- 美食新闻扩展（再增加12条，总共15条）
INSERT INTO news (title, summary, image_url, category_id, user_id, read_count, publish_time) VALUES
('地方特色小吃走向全国', '多种地方特色小吃通过连锁经营走向全国，深受欢迎。', 'https://picsum.photos/400/200?random=83', 8, 8, 82000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('有机食品市场蓬勃发展', '消费者健康意识提升，有机食品市场规模快速增长。', 'https://picsum.photos/400/200?random=84', 8, 8, 68000, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('预制菜产业兴起', '预制菜产业快速发展，方便快捷受到年轻人青睐。', 'https://picsum.photos/400/200?random=85', 8, 8, 95000, DATE_SUB(NOW(), INTERVAL 6 DAY)),
('咖啡文化深入人心', '咖啡店数量激增，咖啡文化在中国快速普及。', 'https://picsum.photos/400/200?random=86', 8, 8, 73000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('烘焙行业持续火热', '烘焙DIY受到追捧，相关产品销量大增。', 'https://picsum.photos/400/200?random=87', 8, 8, 61000, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('火锅品牌竞争激烈', '火锅市场规模突破5000亿元，品牌竞争白热化。', 'https://picsum.photos/400/200?random=88', 8, 8, 108000, DATE_SUB(NOW(), INTERVAL 9 DAY)),
('新式茶饮不断创新', '新式茶饮品牌推出创新产品，引领饮品新潮流。', 'https://picsum.photos/400/200?random=89', 8, 8, 87000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('外卖行业规范发展', '外卖平台加强食品安全监管，保障消费者权益。', 'https://picsum.photos/400/200?random=810', 8, 8, 74000, DATE_SUB(NOW(), INTERVAL 11 DAY)),
('传统美食技艺传承', '多项传统美食制作技艺被列入非物质文化遗产。', 'https://picsum.photos/400/200?random=811', 8, 8, 52000, DATE_SUB(NOW(), INTERVAL 12 DAY)),
('植物肉产品上市', '植物肉产品陆续上市，为消费者提供新选择。', 'https://picsum.photos/400/200?random=812', 8, 8, 66000, DATE_SUB(NOW(), INTERVAL 13 DAY)),
('美食直播带动消费', '美食主播通过直播推广地方特产，销售额惊人。', 'https://picsum.photos/400/200?random=813', 8, 8, 98000, DATE_SUB(NOW(), INTERVAL 14 DAY)),
('餐饮业数字化转型', '餐饮企业加快数字化转型，提升运营效率。', 'https://picsum.photos/400/200?random=814', 8, 8, 79000, DATE_SUB(NOW(), INTERVAL 15 DAY));
