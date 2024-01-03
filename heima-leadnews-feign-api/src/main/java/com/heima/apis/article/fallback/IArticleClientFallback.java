package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author ruoling
 * @date 2024/1/3 20:50:54
 * @description
 */
@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto articleDto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "服务超时");
    }
}
