package com.example.newsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用启动类
 */
@SpringBootApplication
public class NewsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsBackendApplication.class, args);
        System.out.println("新闻后端服务已启动，访问地址: http://localhost:8080");
    }
}
