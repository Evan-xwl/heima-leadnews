package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author ruoling
 * @date 2023/12/25 16:50:26
 * @description
 */
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Resource
    private ApArticleMapper apArticleMapper;

    @Override
    public ResponseResult queryArticleList(ArticleHomeDto articleHomeDto, Short type) {
        // 校验参数
        // 1. 验证参数分页size
        if(articleHomeDto.getSize() == null || articleHomeDto.getSize() == 0){
            articleHomeDto.setSize(10);
        }
        articleHomeDto.setSize(Math.min(ArticleConstants.MAX_PAGE_SIZE, articleHomeDto.getSize()));

        // 2. 验证时间
        if(articleHomeDto.getMinBehotTime() == null){
            articleHomeDto.setMinBehotTime(new Date());
        }
        if(articleHomeDto.getMaxBehotTime() == null){
            articleHomeDto.setMaxBehotTime(new Date());
        }

        // 3. 验证频道类型
        if(StringUtils.isBlank(articleHomeDto.getTag())){
            articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 4. 验证type
        if(type == null || (type != ArticleConstants.LOADTYPE_LOAD_MORE && type != ArticleConstants.LOADTYPE_LOAD_NEW)){
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        List<ApArticle> apArticles = apArticleMapper.loadArticleList(articleHomeDto, type);
        return ResponseResult.okResult(apArticles);
    }
}
