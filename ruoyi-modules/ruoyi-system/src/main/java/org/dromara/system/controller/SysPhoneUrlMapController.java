package org.dromara.system.controller;

import java.util.List;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.FastExcel;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.domain.vo.SysPhoneUrlMapImportVo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.system.domain.vo.SysPhoneUrlMapVo;
import org.dromara.system.domain.bo.SysPhoneUrlMapBo;
import org.dromara.system.service.ISysPhoneUrlMapService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 短信映射
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/sms")
@Slf4j
public class SysPhoneUrlMapController extends BaseController {

    private final ISysPhoneUrlMapService sysPhoneUrlMapService;

    /**
     * 查询短信映射列表
     */
    @SaCheckPermission("system:sms:list")
    @GetMapping("/list")
    public TableDataInfo<SysPhoneUrlMapVo> list(SysPhoneUrlMapBo bo, PageQuery pageQuery) {
        return sysPhoneUrlMapService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出短信映射列表
     */
    @SaCheckPermission("system:sms:export")
    @Log(title = "短信映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysPhoneUrlMapBo bo, HttpServletResponse response) {
        List<SysPhoneUrlMapVo> list = sysPhoneUrlMapService.queryList(bo);
        ExcelUtil.exportExcel(list, "短信映射", SysPhoneUrlMapVo.class, response);
    }

    /**
     * 获取短信映射详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:sms:query")
    @GetMapping("/{id}")
    public R<SysPhoneUrlMapVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sysPhoneUrlMapService.queryById(id));
    }

    /**
     * 新增短信映射
     */
    @SaCheckPermission("system:sms:add")
    @Log(title = "短信映射", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysPhoneUrlMapBo bo) {
        return toAjax(sysPhoneUrlMapService.insertByBo(bo));
    }

    /**
     * 修改短信映射
     */
    @SaCheckPermission("system:sms:edit")
    @Log(title = "短信映射", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysPhoneUrlMapBo bo) {
        return toAjax(sysPhoneUrlMapService.updateByBo(bo));
    }

    /**
     * 删除短信映射
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:sms:remove")
    @Log(title = "短信映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysPhoneUrlMapService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * Excel 导入手机号 & 目标地址
     */
    @SaCheckPermission("system:sms:import")
    @Log(title = "映射导入", businessType = BusinessType.IMPORT)
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> importData(@RequestPart("file") MultipartFile file) {
        try {
            // 使用 FastExcel 读取 Excel，映射到 SysPhoneUrlMapImportVo
            List<SysPhoneUrlMapImportVo> importList =
                FastExcel.read(file.getInputStream(), SysPhoneUrlMapImportVo.class, null)
                    .sheet()           // 默认第一个 sheet
                    .headRowNumber(1)  // 第一行是表头，从第二行开始是数据
                    .doReadSync();

            int count = sysPhoneUrlMapService.importPhoneUrlMaps(importList);
            return R.ok("导入成功，新增 " + count + " 条数据");
        } catch (Exception e) {
            // 建议把 file.getOriginalFilename() 也打进去，方便排查
            log.error("导入手机URL映射失败，文件名：{}", file.getOriginalFilename(), e);
            return R.fail("导入失败：" + e.getMessage());
        }
    }
}
