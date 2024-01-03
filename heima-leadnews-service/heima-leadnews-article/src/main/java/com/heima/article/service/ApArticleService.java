package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

import java.util.List;

/**
 * @author ruoling
 * @date 2023/12/25 16:47:48
 * @description
 */
public interface ApArticleService extends IService<ApArticle> {
    ResponseResult queryArticleList(ArticleHomeDto articleHomeDto, Short type);
    ResponseResult saveArticle(ArticleDto articleDto);
}
