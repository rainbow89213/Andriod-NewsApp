package com.example.demo2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * RetrofitClient - Retrofit 客户端单例类
 * 
 * 作用：
 * 1. 创建和配置 Retrofit 实例
 * 2. 使用单例模式确保全局只有一个实例
 * 3. 配置 OkHttp 客户端（超时、日志等）
 */
public class RetrofitClient {
    
    // Retrofit 实例（单例）
    private static Retrofit retrofit = null;
    
    // Base URL - 服务器地址
    // 注意：
    // 1. 真机测试时使用服务器的实际 IP 地址
    // 2. 模拟器测试时使用 10.0.2.2 代表宿主机的 localhost
    // 3. 结尾必须有斜杠 /
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    
    /**
     * 获取 Retrofit 实例
     * 
     * @return Retrofit 实例
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // 创建日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // 配置 OkHttp 客户端
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)  // 添加日志拦截器
                    .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
                    .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时
                    .build();
            
            // 创建 Retrofit 实例
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // 设置 Base URL
                    .client(okHttpClient)  // 设置 OkHttp 客户端
                    .addConverterFactory(GsonConverterFactory.create())  // 添加 Gson 转换器
                    .build();
        }
        return retrofit;
    }
    
    /**
     * 获取 API 服务实例
     * 
     * @return NewsApiService 实例
     */
    public static NewsApiService getNewsApiService() {
        return getClient().create(NewsApiService.class);
    }
}
