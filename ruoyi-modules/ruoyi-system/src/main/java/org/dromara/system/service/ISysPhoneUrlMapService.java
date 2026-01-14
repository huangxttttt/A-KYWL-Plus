package org.dromara.system.service;

import org.dromara.system.domain.vo.SysPhoneUrlMapImportVo;
import org.dromara.system.domain.vo.SysPhoneUrlMapVo;
import org.dromara.system.domain.bo.SysPhoneUrlMapBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 短信映射Service接口
 *
 * @author huangxt
 * @date 2025-11-20
 */
public interface ISysPhoneUrlMapService {

    /**
     * 查询短信映射
     *
     * @param id 主键
     * @return 短信映射
     */
    SysPhoneUrlMapVo queryById(Long id);

    /**
     * 分页查询短信映射列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 短信映射分页列表
     */
    TableDataInfo<SysPhoneUrlMapVo> queryPageList(SysPhoneUrlMapBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的短信映射列表
     *
     * @param bo 查询条件
     * @return 短信映射列表
     */
    List<SysPhoneUrlMapVo> queryList(SysPhoneUrlMapBo bo);

    /**
     * 新增短信映射
     *
     * @param bo 短信映射
     * @return 是否新增成功
     */
    Boolean insertByBo(SysPhoneUrlMapBo bo);

    /**
     * 修改短信映射
     *
     * @param bo 短信映射
     * @return 是否修改成功
     */
    Boolean updateByBo(SysPhoneUrlMapBo bo);

    /**
     * 校验并批量删除短信映射信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);



    /**
     * 根据导入的 Excel 数据批量新增
     */
    int importPhoneUrlMaps(List<SysPhoneUrlMapImportVo> importList);

    /**
     * 更新审核状态
     *
     * @param id        主键
     * @param auditFlag 审核标志（0 待审核，1 已通过）
     * @return 是否更新成功
     */
    Boolean updateAuditFlag(Long id, String auditFlag);
}
