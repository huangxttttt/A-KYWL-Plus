package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysBlockRule;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 访问拦截规则（支持IP/地区）业务对象 t_sys_block_rule
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysBlockRule.class, reverseConvertGenerate = false)
public class SysBlockRuleBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 规则类型：1=IP，2=IP段，3=国家，4=省份，5=城市
     */
    @NotNull(message = "规则类型：1=IP，2=IP段，3=国家，4=省份，5=城市不能为空", groups = { AddGroup.class, EditGroup.class })
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
     * 状态：1=启用，0=禁用
     */
    @NotNull(message = "状态：1=启用，0=禁用不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;


}
