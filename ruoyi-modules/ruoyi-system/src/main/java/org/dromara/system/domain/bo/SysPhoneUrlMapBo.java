package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysPhoneUrlMap;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 短信映射业务对象 t_sys_phone_url_map
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysPhoneUrlMap.class, reverseConvertGenerate = false)
public class SysPhoneUrlMapBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 目标URL
     */
    @NotBlank(message = "目标URL不能为空", groups = { AddGroup.class, EditGroup.class })
    private String targetUrl;

    /**
     * 短码
     */
    @NotBlank(message = "短码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String shortcode;

    /**
     * 审核标志（0待审核 1已通过）
     */
    @NotBlank(message = "审核标志不能为空", groups = { AddGroup.class, EditGroup.class })
    private String auditFlag;


}
