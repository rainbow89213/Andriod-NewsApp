# 🖥️ 新闻App后端服务

## 📋 快速开始

### 1️⃣ 准备数据库
```bash
mysql -u root -p
CREATE DATABASE news_db;
USE news_db;
source src/main/resources/schema.sql;
source src/main/resources/data.sql;
source src/main/resources/data-extended.sql;
```

### 2️⃣ 启动服务
```bash
mvn spring-boot:run
```
服务将在 http://localhost:8080 启动

## 📂 项目结构
```
newsApp/
├── src/main/java/com/example/newsapp/
│   ├── NewsAppApplication.java    # 启动类
│   ├── controller/                # API接口
│   │   └── NewsController.java    
│   ├── service/                   # 业务逻辑
│   │   └── NewsService.java       
│   ├── mapper/                    # 数据库操作
│   │   └── NewsMapper.java        
│   └── model/                     # 数据模型
│       ├── News.java              # 新闻实体
│       ├── Category.java          # 分类实体
│       ├── User.java              # 用户实体
│       └── NewsItem.java          # API返回对象
├── src/main/resources/
│   ├── application.properties     # 配置文件
│   ├── mappers/                   
│   │   └── NewsMapper.xml         # SQL映射文件
│   ├── schema.sql                 # 表结构
│   ├── data.sql                   # 基础数据
│   └── data-extended.sql          # 扩展数据
└── pom.xml                        # Maven依赖

```

## 🔌 API接口

### 获取新闻列表
```
GET /api/news?offset=0&limit=10
```

### 获取分类新闻
```
GET /api/news/category/{code}?offset=0&limit=10
```

**分类代码**：
- tech（科技）
- economy（经济）
- sports（体育）
- health（健康）
- entertainment（娱乐）
- education（教育）
- environment（环保）
- food（美食）

## 📊 数据库表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| news | 新闻表 | id, title, summary, image_url, category_id |
| category | 分类表 | id, name, code |
| user | 用户表 | id, username, nickname |

## ⚙️ 配置说明

**application.properties**
```properties
server.port=8080                           # 服务端口
spring.datasource.url=jdbc:mysql://localhost:3306/news_db
spring.datasource.username=root            # 数据库用户名
spring.datasource.password=123456          # 数据库密码
```

## 🔧 常用命令

```bash
# 清理并重新编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn package

# 查看依赖树
mvn dependency:tree
```

## 📝 技术栈
- Spring Boot 2.7.x
- MyBatis
- MySQL 5.7+
- Maven

## 🚨 注意事项
1. 确保MySQL服务已启动
2. 数据库密码需与配置文件一致
3. 端口8080不能被占用

---
**版本：** v1.0  
**更新时间：** 2024-12-01
