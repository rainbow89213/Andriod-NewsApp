package com.example.demo2.exposure;

/**
 * 卡片曝光事件监听器接口
 */
public interface CardExposureListener {
    
    /**
     * 卡片开始露出（可见性 > 0%）
     * @param event 曝光事件
     */
    void onCardAppear(CardExposureEvent event);
    
    /**
     * 卡片露出超过50%
     * @param event 曝光事件
     */
    void onCardHalfVisible(CardExposureEvent event);
    
    /**
     * 卡片完整露出（可见性 = 100%）
     * @param event 曝光事件
     */
    void onCardFullyVisible(CardExposureEvent event);
    
    /**
     * 卡片消失（可见性 = 0%）
     * @param event 曝光事件
     */
    void onCardDisappear(CardExposureEvent event);
}
