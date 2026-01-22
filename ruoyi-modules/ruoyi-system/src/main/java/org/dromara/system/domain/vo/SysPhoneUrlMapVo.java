package org.dromara.system.domain.vo;

import org.dromara.system.domain.SysPhoneUrlMap;
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
 * 短信映射视图对象 t_sys_phone_url_map
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysPhoneUrlMap.class)
public class SysPhoneUrlMapVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 目标URL
     */
    @ExcelProperty(value = "目标URL")
    private String targetUrl;

    /**
     * 短码
     */
    @ExcelProperty(value = "短码")
    private String shortcode;



    @ExcelProperty(value = "创建时间")
    private Date createTime;


    /**
     * 租户编号
     */
    private String tenantId;



    private String msgId;

    private String phoneNumber;

    /**
     * 发送状态（0未发送 1已发送 2发送失败）
     */
    private String  sendStatus;

}
