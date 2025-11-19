package com.example.newsapp.controller;

import com.example.newsapp.model.NewsItem;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻 API 控制器
 * 
 * @RestController - 标识这是一个 REST API 控制器
 * @RequestMapping - 定义基础路径
 * @CrossOrigin - 允许跨域请求（允许 Android 应用访问）
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NewsController {
    
    /**
     * 获取新闻列表
     * 
     * @GetMapping - 处理 GET 请求
     * @return 新闻列表
     */
    @GetMapping("/news")
    public List<NewsItem> getNewsList() {
        List<NewsItem> newsList = new ArrayList<>();
        
        // 模拟新闻数据
        // 在实际项目中，这些数据应该从数据库获取
        
        newsList.add(new NewsItem(
            "科技前沿：人工智能技术取得重大突破",
            "最新研究表明，深度学习算法在图像识别领域达到了新的里程碑，准确率提升至99.8%，为自动驾驶和医疗诊断带来新希望。",
            "https://picsum.photos/400/200?random=1",
            "2小时前",
            "1.2万阅读"
        ));
        
        newsList.add(new NewsItem(
            "经济观察：全球市场迎来复苏信号",
            "多个国际机构发布报告显示，全球经济正在逐步恢复，预计今年GDP增长率将达到3.5%，消费者信心指数持续上升。",
            "https://picsum.photos/400/200?random=2",
            "5小时前",
            "8.6万阅读"
        ));
        
        newsList.add(new NewsItem(
            "体育快讯：国足晋级世界杯预选赛下一轮",
            "在昨晚进行的关键比赛中，国家队以2:1战胜对手，成功晋级世界杯预选赛下一轮，全国球迷欢欣鼓舞。",
            "https://picsum.photos/400/200?random=3",
            "8小时前",
            "15.3万阅读"
        ));
        
        newsList.add(new NewsItem(
            "健康生活：专家建议每天运动30分钟",
            "世界卫生组织最新指南指出，每天至少进行30分钟中等强度运动，可以显著降低心血管疾病风险，提高生活质量。",
            "https://picsum.photos/400/200?random=4",
            "12小时前",
            "5.7万阅读"
        ));
        
        newsList.add(new NewsItem(
            "娱乐八卦：知名导演新片获国际大奖",
            "在刚刚结束的国际电影节上，华语导演的最新作品斩获最佳影片奖，这是华语电影在国际舞台上的又一次突破。",
            "https://picsum.photos/400/200?random=5",
            "1天前",
            "23.4万阅读"
        ));
        
        newsList.add(new NewsItem(
            "教育资讯：在线教育平台用户突破千万",
            "随着数字化教育的普及，国内领先的在线教育平台宣布注册用户突破1000万，课程覆盖从小学到职业培训的各个领域。",
            "https://picsum.photos/400/200?random=6",
            "1天前",
            "4.2万阅读"
        ));
        
        newsList.add(new NewsItem(
            "环保行动：多地启动碳中和试点项目",
            "为应对气候变化，全国多个城市启动碳中和试点项目，通过新能源、绿色建筑等措施，力争2030年实现碳达峰目标。",
            "https://picsum.photos/400/200?random=7",
            "2天前",
            "6.8万阅读"
        ));
        
        newsList.add(new NewsItem(
            "美食推荐：网红餐厅排队3小时仍火爆",
            "位于市中心的一家网红餐厅持续火爆，即使需要排队3小时，食客们依然络绎不绝，招牌菜品更是一位难求。",
            "https://picsum.photos/400/200?random=8",
            "2天前",
            "9.1万阅读"
        ));
        
        return newsList;
    }
}
