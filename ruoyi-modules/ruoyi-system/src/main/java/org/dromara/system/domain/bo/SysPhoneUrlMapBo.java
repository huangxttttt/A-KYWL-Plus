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
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userName;

    /**
     * 电话号码（E.164格式）
     */
    @NotBlank(message = "电话号码（E.164格式）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String phoneNumber;

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
     * 发送状态（0未发送 1已发送 2发送失败）
     */
    private String sendStatus;


}
