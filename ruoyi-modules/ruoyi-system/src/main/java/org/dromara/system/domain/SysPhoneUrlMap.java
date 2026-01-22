package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 短信映射对象 t_sys_phone_url_map
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_phone_url_map")
public class SysPhoneUrlMap extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 目标URL
     */
    private String targetUrl;

    /**
     * 短码
     */
    private String shortcode;


    private String msgId;

    private String phoneNumber;


    /**
     * 发送状态（0未发送 1已发送 2发送失败）
     */
    private String  sendStatus;

}
