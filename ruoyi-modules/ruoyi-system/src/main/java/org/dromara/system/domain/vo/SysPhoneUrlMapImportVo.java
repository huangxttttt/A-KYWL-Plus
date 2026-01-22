package org.dromara.system.domain.vo;

import lombok.Data;
import cn.idev.excel.annotation.ExcelProperty;
/**
 * 短信映射导入 VO（对应 Excel）
 */
@Data
public class SysPhoneUrlMapImportVo {

    /** 目标URL */
    @ExcelProperty("目标URL")
    private String targetUrl;

    /** URL 短编码 */
    @ExcelProperty("短码")
    private String shortcode;


}
