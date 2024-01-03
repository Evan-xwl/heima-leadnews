package com.heima.model.article.dtos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @author ruoling
 * @date 2024/1/3 17:20:28
 * @description
 */
@Data
public class ArticleDto extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}
