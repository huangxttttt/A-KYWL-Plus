package org.dromara.system.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
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
import org.dromara.system.domain.vo.SysBlockRuleVo;
import org.dromara.system.domain.bo.SysBlockRuleBo;
import org.dromara.system.service.ISysBlockRuleService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 访问拦截规则（支持IP/地区）
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/rule")
public class SysBlockRuleController extends BaseController {

    private final ISysBlockRuleService sysBlockRuleService;

    /**
     * 查询访问拦截规则（支持IP/地区）列表
     */
    @SaCheckPermission("system:rule:list")
    @GetMapping("/list")
    public TableDataInfo<SysBlockRuleVo> list(SysBlockRuleBo bo, PageQuery pageQuery) {
        return sysBlockRuleService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出访问拦截规则（支持IP/地区）列表
     */
    @SaCheckPermission("system:rule:export")
    @Log(title = "访问拦截规则（支持IP/地区）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysBlockRuleBo bo, HttpServletResponse response) {
        List<SysBlockRuleVo> list = sysBlockRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "访问拦截规则（支持IP/地区）", SysBlockRuleVo.class, response);
    }

    /**
     * 获取访问拦截规则（支持IP/地区）详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:rule:query")
    @GetMapping("/{id}")
    public R<SysBlockRuleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sysBlockRuleService.queryById(id));
    }

    /**
     * 新增访问拦截规则（支持IP/地区）
     */
    @SaCheckPermission("system:rule:add")
    @Log(title = "访问拦截规则（支持IP/地区）", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysBlockRuleBo bo) {
        return toAjax(sysBlockRuleService.insertByBo(bo));
    }

    /**
     * 修改访问拦截规则（支持IP/地区）
     */
    @SaCheckPermission("system:rule:edit")
    @Log(title = "访问拦截规则（支持IP/地区）", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysBlockRuleBo bo) {
        return toAjax(sysBlockRuleService.updateByBo(bo));
    }

    /**
     * 删除访问拦截规则（支持IP/地区）
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:rule:remove")
    @Log(title = "访问拦截规则（支持IP/地区）", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysBlockRuleService.deleteWithValidByIds(List.of(ids), true));
    }
}
