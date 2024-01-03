package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @author ruoling
 * @date 2024/1/2 10:30:52
 * @description
 */
@Data
public class WmMaterialDto extends PageRequestDto {
    /**1->收藏
     * 0->未收藏*/
    private Short isCollection;
}
