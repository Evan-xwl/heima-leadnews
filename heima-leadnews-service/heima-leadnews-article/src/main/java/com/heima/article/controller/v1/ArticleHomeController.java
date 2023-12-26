package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ruoling
 * @date 2023/12/25 15:45:19
 * @description
 */
@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {

    @Resource
    private ApArticleService apArticleService;

    @ApiOperation(value = "加载文章首页信息")
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.queryArticleList(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }

    @ApiOperation(value = "加载更多文章")
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto articleHomeDto){
        return apArticleService.queryArticleList(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }

    @ApiOperation(value = "加载最新文章")
    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto articleHomeDto){
        return apArticleService.queryArticleList(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }

}
