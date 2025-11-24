package org.dromara.system.service;

import org.dromara.system.domain.vo.SysBlockRuleVo;
import org.dromara.system.domain.bo.SysBlockRuleBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 访问拦截规则（支持IP/地区）Service接口
 *
 * @author huangxt
 * @date 2025-11-20
 */
public interface ISysBlockRuleService {

    /**
     * 查询访问拦截规则（支持IP/地区）
     *
     * @param id 主键
     * @return 访问拦截规则（支持IP/地区）
     */
    SysBlockRuleVo queryById(Long id);

    /**
     * 分页查询访问拦截规则（支持IP/地区）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 访问拦截规则（支持IP/地区）分页列表
     */
    TableDataInfo<SysBlockRuleVo> queryPageList(SysBlockRuleBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的访问拦截规则（支持IP/地区）列表
     *
     * @param bo 查询条件
     * @return 访问拦截规则（支持IP/地区）列表
     */
    List<SysBlockRuleVo> queryList(SysBlockRuleBo bo);

    /**
     * 新增访问拦截规则（支持IP/地区）
     *
     * @param bo 访问拦截规则（支持IP/地区）
     * @return 是否新增成功
     */
    Boolean insertByBo(SysBlockRuleBo bo);

    /**
     * 修改访问拦截规则（支持IP/地区）
     *
     * @param bo 访问拦截规则（支持IP/地区）
     * @return 是否修改成功
     */
    Boolean updateByBo(SysBlockRuleBo bo);

    /**
     * 校验并批量删除访问拦截规则（支持IP/地区）信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 检查规则
     *
     * @param ip
     * @return
     */
    Boolean checkRule(String ip);
}
