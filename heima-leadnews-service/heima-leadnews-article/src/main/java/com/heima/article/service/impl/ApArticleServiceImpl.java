package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.nntp.Article;
import org.springframework.beans.BeanUtils;
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

    @Resource
    private ApArticleContentMapper contentMapper;

    @Resource
    private ApArticleConfigMapper configMapper;

    @Resource
    ArticleFreemarkerService articleFreemarkerService;
    /***
     * 保存app端的文章
     * @param articleDto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto articleDto) {
        //1.校验参数
        if(articleDto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApArticle article = new ApArticle();
        BeanUtils.copyProperties(articleDto, article);
        //2.判断新增还是修改
        if(articleDto.getId() == null){
            //新增
            //插入文章信息（以下三个表均是一对一）
            save(article);
            //插入文章配置信息
            ApArticleConfig apArticleConfig = new ApArticleConfig(article.getId());
            configMapper.insert(apArticleConfig);
            //插入文章内容信息
            ApArticleContent articleContent = new ApArticleContent();
            articleContent.setContent(articleDto.getContent());
            articleContent.setArticleId(article.getId());
            contentMapper.insert(articleContent);

        }else {
            //修改
            updateById(article);
            //修改文章内容表
            ApArticleContent articleContent = contentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, articleDto.getId()));
            articleContent.setContent(articleDto.getContent());
            contentMapper.updateById(articleContent);
//            contentMapper.update(articleContent, Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, articleDto.getId()));
        }

        // 保存app文章后,异步生成静态mino文件
        articleFreemarkerService.buildArticleToMinIO(article, articleDto.getContent());
        return ResponseResult.okResult(article.getId());
    }
}
