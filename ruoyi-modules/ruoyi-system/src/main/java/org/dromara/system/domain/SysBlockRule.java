package org.dromara.system.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 访问拦截规则（支持IP/地区）对象 t_sys_block_rule
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_block_rule")
public class SysBlockRule extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 规则类型：1=IP，2=IP段，3=国家，4=省份，5=城市
     */
    private Long ruleType;

    /**
     * IP地址(IPv4/IPv6)
     */
    private String ipAddress;

    /**
     * CIDR IP段，例如 192.168.1.0/24
     */
    private String ipCidr;

    /**
     * 国家
     */
    private String country;

    /**
     * 省/州
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 拦截原因
     */
    private String reason;


    /**
     * 状态：0=启用，1=禁用
     */
    private Long status;


}
