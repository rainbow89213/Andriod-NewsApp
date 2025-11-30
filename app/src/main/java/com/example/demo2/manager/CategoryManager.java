package com.example.demo2.manager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * CategoryManager - 分类标签管理器
 * 
 * 职责：
 * - 管理分类标签的创建和显示
 * - 处理分类切换逻辑
 * - 维护当前选中的分类状态
 */
public class CategoryManager {
    
    private static final String TAG = "CategoryManager";
    
    // 上下文
    private Context context;
    
    // 分类容器
    private LinearLayout categoryContainer;
    
    // 当前选中的分类
    private String currentCategory = null;
    
    // 分类标签列表
    private List<TextView> categoryTabs = new ArrayList<>();
    
    // 分类切换监听器
    private OnCategoryChangeListener categoryChangeListener;
    
    /**
     * 分类切换监听接口
     */
    public interface OnCategoryChangeListener {
        void onCategoryChanged(String category);
    }
    
    /**
     * 构造函数
     */
    public CategoryManager(Context context, LinearLayout categoryContainer) {
        this.context = context;
        this.categoryContainer = categoryContainer;
    }
    
    /**
     * 初始化分类标签栏
     */
    public void initCategoryTabs() {
        // 定义分类列表
        String[] categories = {
            "全部", "科技", "经济", "体育", "健康", 
            "娱乐", "教育", "环境", "美食"
        };
        
        String[] categoryCodes = {
            null, "tech", "economy", "sports", "health", 
            "entertainment", "education", "environment", "food"
        };
        
        // 为每个分类创建标签
        for (int i = 0; i < categories.length; i++) {
            final String categoryName = categories[i];
            final String categoryCode = categoryCodes[i];
            
            TextView tab = createCategoryTab(categoryName);
            
            // 设置点击监听
            tab.setOnClickListener(v -> {
                selectCategory(categoryCode);
                if (categoryChangeListener != null) {
                    categoryChangeListener.onCategoryChanged(categoryCode);
                    Log.d(TAG, "🔄 分类切换完成，当前分类: " + (categoryCode == null ? "[全部]" : categoryCode));
                }
            });
            
            categoryContainer.addView(tab);
            categoryTabs.add(tab);
        }
        
        // 默认选中[全部]
        if (!categoryTabs.isEmpty()) {
            selectCategoryTab(categoryTabs.get(0));
        }
        
        Log.d(TAG, "✅ 分类标签初始化完成，共 " + categories.length + " 个");
    }
    
    /**
     * 创建单个分类标签
     */
    private TextView createCategoryTab(String text) {
        TextView tab = new TextView(context);
        tab.setText(text);
        tab.setTextSize(14);
        tab.setTextColor(Color.parseColor("#666666"));
        tab.setBackgroundColor(Color.parseColor("#F5F5F5"));
        tab.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        
        // 设置圆角背景
        tab.setBackground(context.getDrawable(android.R.drawable.btn_default));
        
        // 设置布局参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(8), 0, 0, 0);
        tab.setLayoutParams(params);
        
        return tab;
    }
    
    /**
     * 选中指定分类
     */
    public void selectCategory(String categoryCode) {
        currentCategory = categoryCode;
        Log.d(TAG, "📑 切换到分类: " + (categoryCode == null ? "[全部]" : categoryCode));
        
        // 更新所有标签的选中状态
        String[] categoryCodes = {
            null, "tech", "economy", "sports", "health", 
            "entertainment", "education", "environment", "food"
        };
        
        for (int i = 0; i < categoryTabs.size() && i < categoryCodes.length; i++) {
            if ((categoryCode == null && categoryCodes[i] == null) ||
                (categoryCode != null && categoryCode.equals(categoryCodes[i]))) {
                selectCategoryTab(categoryTabs.get(i));
            } else {
                unselectCategoryTab(categoryTabs.get(i));
            }
        }
    }
    
    /**
     * 选中标签样式
     */
    private void selectCategoryTab(TextView tab) {
        tab.setTextColor(Color.WHITE);
        tab.setBackgroundColor(Color.parseColor("#4CAF50"));
    }
    
    /**
     * 取消选中标签样式
     */
    private void unselectCategoryTab(TextView tab) {
        tab.setTextColor(Color.parseColor("#666666"));
        tab.setBackgroundColor(Color.parseColor("#F5F5F5"));
    }
    
    /**
     * 获取当前选中的分类
     */
    public String getCurrentCategory() {
        return currentCategory;
    }
    
    /**
     * 设置分类切换监听器
     */
    public void setOnCategoryChangeListener(OnCategoryChangeListener listener) {
        this.categoryChangeListener = listener;
    }
    
    /**
     * dp转px
     */
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
