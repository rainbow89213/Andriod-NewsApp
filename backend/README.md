# 新闻列表后端服务

这是一个基于 Spring Boot 的 RESTful API 后端服务，为 Android 新闻应用提供数据接口。

## 技术栈

- **Spring Boot 3.2.0** - Java Web 框架
- **Maven** - 项目构建工具
- **Java 17** - 编程语言
- **Lombok** - 简化 Java 代码

## 项目结构

```
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/newsbackend/
│       │       ├── NewsBackendApplication.java  # 启动类
│       │       ├── controller/
│       │       │   └── NewsController.java      # API 控制器
│       │       └── model/
│       │           └── NewsItem.java            # 数据模型
│       └── resources/
│           └── application.properties           # 配置文件
└── pom.xml                                      # Maven 配置
```

## API 接口

### 获取新闻列表

- **URL**: `/api/news`
- **方法**: `GET`
- **响应格式**: JSON
- **响应示例**:

```json
[
  {
    "title": "科技前沿：人工智能技术取得重大突破",
    "summary": "最新研究表明，深度学习算法在图像识别领域达到了新的里程碑...",
    "imageUrl": "https://picsum.photos/400/200?random=1",
    "publishTime": "2小时前",
    "readCount": "1.2万阅读"
  }
]
```

## 如何运行

### 前置要求

- Java 17 或更高版本
- Maven 3.6 或更高版本

### 运行步骤

1. **进入后端目录**
   ```bash
   cd backend
   ```

2. **使用 Maven 运行**
   ```bash
   mvn spring-boot:run
   ```

3. **或者先编译再运行**
   ```bash
   mvn clean package
   java -jar target/news-backend-1.0.0.jar
   ```

4. **验证服务**
   
   服务启动后，访问: http://localhost:8080/api/news
   
   应该能看到 JSON 格式的新闻列表。

## 配置说明

在 `application.properties` 中可以修改：

- `server.port` - 服务器端口（默认 8080）
- `logging.level.*` - 日志级别

## Android 端配置

在 Android 项目中，需要修改 `RetrofitClient.java` 的 BASE_URL：

- **模拟器测试**: `http://10.0.2.2:8080/`
- **真机测试**: `http://你的电脑IP:8080/`

## 扩展功能

### 连接数据库

1. 添加数据库依赖（MySQL 示例）：
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
   </dependency>
   ```

2. 配置数据库连接：
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/news_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

### 添加分页功能

使用 Spring Data JPA 的 `Pageable` 接口实现分页。

### 添加搜索功能

在 Controller 中添加搜索参数，实现关键词搜索。

## 注意事项

1. **跨域配置**: 已通过 `@CrossOrigin` 注解允许所有来源访问，生产环境应限制具体域名
2. **图片 URL**: 当前使用 picsum.photos 提供的随机图片，实际项目应使用真实图片
3. **数据持久化**: 当前数据在内存中，重启后丢失，建议连接数据库
4. **安全性**: 生产环境应添加认证授权机制

## 许可证

MIT License
