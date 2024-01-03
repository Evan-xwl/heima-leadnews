package com.heima.wemedia.mapper.service;

/**
 * @author ruoling
 * @date 2024/1/3 20:21:34
 * @description
 */
public interface WmNewsAutoScanService {

    /**
     * 自媒体文章审核
     * @param id  自媒体文章id
     */
    void autoScanWmNews(Integer id);
}
