package com.example.demo2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.demo2.fragment.NewsListFragment;
import java.util.List;

/**
 * ViewPager2适配器
 * 管理不同分类的Fragment
 */
public class CategoryPagerAdapter extends FragmentStateAdapter {
    
    private final List<Category> categories;
    
    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Category category = categories.get(position);
        return NewsListFragment.newInstance(category.getCode(), category.getName());
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    @Override
    public long getItemId(int position) {
        // 返回唯一ID，确保Fragment不会被错误地重用
        return categories.get(position).getCode().hashCode();
    }
    
    @Override
    public boolean containsItem(long itemId) {
        for (Category category : categories) {
            if (category.getCode().hashCode() == itemId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 分类数据类
     */
    public static class Category {
        private final String code;
        private final String name;
        
        public Category(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getName() {
            return name;
        }
    }
}
