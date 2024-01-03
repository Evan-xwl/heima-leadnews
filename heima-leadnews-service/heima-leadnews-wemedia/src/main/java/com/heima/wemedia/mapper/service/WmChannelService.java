package com.heima.wemedia.mapper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * @author ruoling
 * @date 2024/1/2 15:44:22
 * @description
 */
public interface WmChannelService extends IService<WmChannel> {
    public ResponseResult findAll();
}
