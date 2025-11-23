package com.example.demo2;

/**
 * 卡片曝光状态
 * 
 * 用于追踪每个卡片的曝光状态，避免重复触发事件
 */
public class ExposureState {
    
    // 是否已经触发"露出"事件
    private boolean hasAppeared = false;
    
    // 是否已经触发"露出超过50%"事件
    private boolean hasHalfVisible = false;
    
    // 是否已经触发"完整露出"事件
    private boolean hasFullyVisible = false;
    
    // 当前是否可见
    private boolean isCurrentlyVisible = false;
    
    // 最后的可见比例
    private float lastVisiblePercent = 0f;
    
    public boolean hasAppeared() {
        return hasAppeared;
    }
    
    public void setAppeared(boolean appeared) {
        this.hasAppeared = appeared;
    }
    
    public boolean hasHalfVisible() {
        return hasHalfVisible;
    }
    
    public void setHalfVisible(boolean halfVisible) {
        this.hasHalfVisible = halfVisible;
    }
    
    public boolean hasFullyVisible() {
        return hasFullyVisible;
    }
    
    public void setFullyVisible(boolean fullyVisible) {
        this.hasFullyVisible = fullyVisible;
    }
    
    public boolean isCurrentlyVisible() {
        return isCurrentlyVisible;
    }
    
    public void setCurrentlyVisible(boolean currentlyVisible) {
        this.isCurrentlyVisible = currentlyVisible;
    }
    
    public float getLastVisiblePercent() {
        return lastVisiblePercent;
    }
    
    public void setLastVisiblePercent(float lastVisiblePercent) {
        this.lastVisiblePercent = lastVisiblePercent;
    }
    
    /**
     * 重置状态（当卡片消失后）
     */
    public void reset() {
        hasAppeared = false;
        hasHalfVisible = false;
        hasFullyVisible = false;
        isCurrentlyVisible = false;
        lastVisiblePercent = 0f;
    }
}
