package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @author ruoling
 * @date 2023/12/28 10:55:05
 * @description
 */
@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Resource
    private FileStorageService fileStorageService;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 1. 校验参数
        if(multipartFile == null || multipartFile.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2. 图片存到minio
        // 生成随机文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        // 获得源文件名
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("成功上传图片{}到minio中", fileId);
        }catch (Exception e){
            log.error("minio上传文件失败");
        }
        // 3. 数据存入Mysql
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getWmUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setType((short) 0);
        wmMaterial.setIsCollection((short) 0);
        save(wmMaterial);
        // 4. 返回结果
        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult findList(WmMaterialDto wmMaterialDto) {
        // 1.检查参数
        wmMaterialDto.checkParam();
        // 2.分页查询
        IPage page = new Page(wmMaterialDto.getPage(), wmMaterialDto.getSize());
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(wmMaterialDto.getIsCollection() != null && wmMaterialDto.getIsCollection() == 1){
            //查询收藏
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection, wmMaterialDto.getIsCollection());
        }
        // 按照用户查询
        lambdaQueryWrapper.eq(WmMaterial::getUserId, WmThreadLocalUtil.getWmUser().getId());

        // 按照时间降序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);

        page = page(page, lambdaQueryWrapper);
        // 3.返回结果
        ResponseResult result = new PageResponseResult(wmMaterialDto.getPage(), wmMaterialDto.getSize(), (int) page.getTotal());
        result.setData(page.getRecords());
        return result;
    }
}
