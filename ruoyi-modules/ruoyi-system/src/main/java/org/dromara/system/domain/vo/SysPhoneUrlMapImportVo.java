package org.dromara.system.domain.vo;

import lombok.Data;
import cn.idev.excel.annotation.ExcelProperty;
/**
 * 短信映射导入 VO（对应 Excel）
 */
@Data
public class SysPhoneUrlMapImportVo {

    /** 用户账号 */
    @ExcelProperty("用户账号")
    private String userName;

    /** 手机号码（E.164格式） */
    @ExcelProperty("手机号码")
    private String phoneNumber;

    /** 目标URL */
    @ExcelProperty("目标URL")
    private String targetUrl;
}
