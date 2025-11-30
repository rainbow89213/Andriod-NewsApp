package com.example.demo2.api;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * ApiClient - API客户端配置类
 * 
 * 作用：配置和创建Retrofit实例，管理网络请求的全局设置
 * 使用单例模式确保全局只有一个Retrofit实例
 */
public class ApiClient {
    
    private static final String TAG = "ApiClient";
    
    // 后端服务器基础URL
    // 注意：使用10.0.2.2是Android模拟器访问本机localhost的特殊IP
    // 如果使用真机调试，需要改为电脑的实际IP地址
    private static final String BASE_URL = "http://10.0.2.2:8080/";  // 修改为8080端口，与后端一致
    
    // Retrofit实例（单例）
    private static Retrofit retrofit = null;
    
    // API服务接口实例（单例）
    private static NewsApiService newsApiService = null;
    
    /**
     * 获取Retrofit实例（懒加载单例）
     * 
     * @return Retrofit实例
     */
    public static synchronized Retrofit getRetrofitClient() {
        if (retrofit == null) {
            Log.d(TAG, "📡 初始化Retrofit客户端");
            Log.d(TAG, "  - 基础URL: " + BASE_URL);
            
            // 创建日志拦截器（用于调试）
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                message -> Log.d(TAG, "📝 HTTP: " + message)
            );
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // 创建OkHttpClient，配置超时和拦截器
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)      // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)         // 读取超时
                    .writeTimeout(30, TimeUnit.SECONDS)        // 写入超时
                    .addInterceptor(loggingInterceptor)        // 添加日志拦截器
                    .addInterceptor(chain -> {
                        // 添加通用请求头
                        okhttp3.Request original = chain.request();
                        okhttp3.Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .header("User-Agent", "NewsApp/1.0 Android")
                                .method(original.method(), original.body())
                                .build();
                        
                        Log.d(TAG, "🔗 请求URL: " + request.url());
                        return chain.proceed(request);
                    })
                    .build();
            
            // 创建Retrofit实例
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())  // Gson转换器
                    .build();
            
            Log.d(TAG, "✅ Retrofit客户端初始化完成");
        }
        
        return retrofit;
    }
    
    /**
     * 获取新闻API服务接口（懒加载单例）
     * 
     * @return NewsApiService实例
     */
    public static synchronized NewsApiService getNewsApiService() {
        if (newsApiService == null) {
            Log.d(TAG, "📰 创建NewsApiService实例");
            newsApiService = getRetrofitClient().create(NewsApiService.class);
            Log.d(TAG, "✅ NewsApiService创建完成");
        }
        return newsApiService;
    }
    
    /**
     * 获取基础URL（供其他组件使用）
     * 
     * @return 基础URL
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }
    
    /**
     * 清除单例实例（用于测试或重新配置）
     */
    public static synchronized void reset() {
        Log.d(TAG, "🔄 重置ApiClient");
        retrofit = null;
        newsApiService = null;
    }
    
    /**
     * 更新基础URL（用于动态配置服务器地址）
     * 注意：调用此方法后需要调用reset()重置实例
     * 
     * @param newBaseUrl 新的基础URL
     */
    public static void updateBaseUrl(String newBaseUrl) {
        Log.d(TAG, "📡 更新基础URL: " + newBaseUrl);
        // 这里可以保存到SharedPreferences
        // 然后调用reset()重新创建Retrofit实例
        reset();
    }
}
