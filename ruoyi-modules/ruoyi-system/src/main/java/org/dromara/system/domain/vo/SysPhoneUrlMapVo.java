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
     * 用户账号
     */
    @ExcelProperty(value = "用户账号")
    private String userName;

    /**
     * 电话号码（E.164格式）
     */
    @ExcelProperty(value = "电话号码", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "E=.164格式")
    private String phoneNumber;

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

    /**
     * 发送状态（0未发送 1已发送 2发送失败）
     */
    @ExcelProperty(value = "发送状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_sms_send")
    private String sendStatus;


}
