package com.heima.article.feign;

import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ruoling
 * @date 2024/1/3 17:30:32
 * @description
 */
@RestController
public class ArticleClient implements IArticleClient {

    @Resource
    ApArticleService apArticleService;

    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(ArticleDto articleDto) {
        return apArticleService.saveArticle(articleDto);
    }
}
