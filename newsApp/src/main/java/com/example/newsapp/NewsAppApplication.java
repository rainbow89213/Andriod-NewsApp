package com.example.newsapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.newsapp.mapper")
public class NewsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsAppApplication.class, args);
		System.out.println("========================================");
		System.out.println("✅ 新闻应用启动成功！");
		System.out.println("📡 API地址: http://localhost:8080/api/news");
		System.out.println("========================================");
	}

}
