package com.heima.model.article.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @author ruoling
 * @date 2023/12/25 15:43:41
 * @description
 */

@Data
public class ArticleHomeDto {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    // 分页size
    Integer size;
    // 频道ID
    String tag;
}

