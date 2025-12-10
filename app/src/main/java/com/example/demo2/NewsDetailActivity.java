package com.example.demo2;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.demo2.fragment.NewsDetailFragment;

/**
 * 新闻详情Activity（手机模式）
 * 在手机上全屏显示新闻详情
 */
public class NewsDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_NEWS_ITEM = "extra_news_item";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        
        // 设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // 显示返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("新闻详情");
        }
        
        // 获取传递的新闻对象
        NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra(EXTRA_NEWS_ITEM);
        
        // 如果是第一次创建Activity
        if (savedInstanceState == null && newsItem != null) {
            // 创建并添加详情Fragment
            NewsDetailFragment fragment = NewsDetailFragment.newInstance(newsItem);
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理返回按钮点击
        if (item.getItemId() == android.R.id.home) {
            // 使用OnBackPressedDispatcher代替onBackPressed
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
