package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ruoling
 * @date 2024/1/2 16:09:21
 * @description
 */
@Service
@Transactional
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        // 1.参数校验
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();

        Integer userId = WmThreadLocalUtil.getWmUser().getId();
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2.分页条件查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 状态
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }

        // 频道
        if (dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }

        // 时间
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }

        // 关键词模糊查询
        if (StringUtils.isNotBlank(dto.getKeyword())) {
            lambdaQueryWrapper.like(WmNews::getTitle, dto.getKeyword());
        }

        // 绑定用户
        lambdaQueryWrapper.eq(WmNews::getUserId, userId);

        // 按时间倒序返回
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);

        page = page(page, lambdaQueryWrapper);
        ResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        result.setData(page.getRecords());
        return result;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        //1.参数校验
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.保存或者修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        //2.1将dto中的图片(list)转成String
        if (dto.getImages() != null && dto.getImages().size() != 0) {
            String images = StringUtils.join(dto.getImages(), ",");
            //前端传来的封面图片设置（如果选择自动，这里应该是null，
            // 后面的saveRelativeInfoForCover方法会设置
            wmNews.setImages(images);
        }
        //2.2判断封面类型
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }
        saveOrUpdateWmNews(wmNews);

        //3.判断是否为草稿
        //3.1是草稿直接返回
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //3.2不是草稿则处理内容图片与素材的关系
        //4.获取前端传来的图片
        List<String> materials = extractURLInfo(dto.getContent());

        saveRelativeInfoForContent(materials, wmNews.getId());
        //5.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        saveRelativeInfoForCover(dto, wmNews, materials);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 第一个功能：如果当前封面类型为自动，则设置封面类型的数据(由于无论是新增文章还是修改文章，都已经存储了，所以这里都是更新封面信息（type和images）)
     * 匹配规则：
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     * <p>
     * 第二个功能：保存封面图片与素材的关系
     *
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {

        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            //多图
            if (materials.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else if (materials.size() >= 1 && materials.size() < 3) {
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if (images != null && images.size() > 0) {
                wmNews.setImages(StringUtils.join(images, ","));
            }
            updateById(wmNews);
        }
        // 无论是否是自动设置封面，都需要更新封面与文章关系表（无图除外）
        if (images != null && images.size() > 0) {
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }

    }

    /***
     * 处理文章内容与素材的关系
     * @param materials
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    @Resource
    private WmMaterialMapper wmMaterialMapper;

    /***
     * 保存文章(内容)图片与素材的关系到数据库中
     * @param materials 文章内容中的图片
     * @param newsId
     * @param wmContentReference
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short wmContentReference) {
        if (materials != null && materials.size() != 0) {
            List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

            // 判断素材是否有效
            if (wmMaterials == null || wmMaterials.size() == 0) {
                //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                // 意味着内容中使用的图片必须来自素材库，否则将会出现查询不到所有图片的情况，继而出现异常
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            if (materials.size() != wmMaterials.size()) {
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            List<Integer> idList = wmMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            //批量保存
            wmNewsMaterialMapper.saveRelations(idList, newsId, wmContentReference);
        }
    }

    /***
     * 提取content中的图片信息(URL),封装成List返回
     * @param content
     * @return
     */
    public List<String> extractURLInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String url = (String) map.get("value");
                materials.add(url);
            }
        }
        return materials;
    }

    @Resource
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 保存或修改文章
     *
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        //补全属性
        wmNews.setUserId(WmThreadLocalUtil.getWmUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);//默认上架

        if (wmNews.getId() == null) {
            //保存
            save(wmNews);
        } else {
            //修改
            //删除文章图片与素材的关系(删除文章id为wmNews.id的所有记录)
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        }
    }
}
