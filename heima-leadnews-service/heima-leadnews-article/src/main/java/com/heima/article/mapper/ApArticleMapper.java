package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ruoling
 * @date 2023/12/25 15:57:38
 * @description
 */
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    /**
     * 根据传入的参数查询文章信息
     * @param articleHomeDto (最大时间-最小时间-分页信息-频道类型)
     * @param type (1表示查询最新的(大于maxBehotTime), 2表示查询更多(小于minBehotTime))
     * @return
     */
    public List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDto articleHomeDto, Short type);
}
