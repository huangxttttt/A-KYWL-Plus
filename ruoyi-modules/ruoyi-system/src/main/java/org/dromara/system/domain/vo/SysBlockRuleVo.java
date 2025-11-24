package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.SysBlockRule;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 访问拦截规则（支持IP/地区）视图对象 t_sys_block_rule
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysBlockRule.class)
public class SysBlockRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 规则类型：1=IP，2=IP段，3=国家，4=省份，5=城市
     */
    @ExcelProperty(value = "规则类型：1=IP，2=IP段，3=国家，4=省份，5=城市")
    private Long ruleType;

    /**
     * IP地址(IPv4/IPv6)
     */
    @ExcelProperty(value = "IP地址(IPv4/IPv6)")
    private String ipAddress;

    /**
     * CIDR IP段，例如 192.168.1.0/24
     */
    @ExcelProperty(value = "CIDR IP段，例如 192.168.1.0/24")
    private String ipCidr;

    /**
     * 国家
     */
    @ExcelProperty(value = "国家")
    private String country;

    /**
     * 省/州
     */
    @ExcelProperty(value = "省/州")
    private String province;

    /**
     * 城市
     */
    @ExcelProperty(value = "城市")
    private String city;

    /**
     * 拦截原因
     */
    @ExcelProperty(value = "拦截原因")
    private String reason;

    /**
     * 规则到期时间，NULL=永久生效
     */
    @ExcelProperty(value = "规则到期时间，NULL=永久生效")
    private Date expireTime;

    /**
     * 状态：1=启用，0=禁用
     */
    @ExcelProperty(value = "状态：1=启用，0=禁用", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private Long status;


}
