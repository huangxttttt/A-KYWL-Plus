package org.dromara.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysPhoneUrlMap;
import org.dromara.system.domain.bo.SysPhoneUrlMapBo;
import org.dromara.system.domain.vo.SysPhoneUrlMapImportVo;
import org.dromara.system.domain.vo.SysPhoneUrlMapVo;
import org.dromara.system.mapper.SysPhoneUrlMapMapper;
import org.dromara.system.service.ISysPhoneUrlMapService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 短信映射Service业务层处理
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysPhoneUrlMapServiceImpl implements ISysPhoneUrlMapService {

    private final SysPhoneUrlMapMapper baseMapper;

    /**
     * 查询短信映射
     *
     * @param id 主键
     * @return 短信映射
     */
    @Override
    public SysPhoneUrlMapVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询短信映射列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 短信映射分页列表
     */
    @Override
    public TableDataInfo<SysPhoneUrlMapVo> queryPageList(SysPhoneUrlMapBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysPhoneUrlMap> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(SysPhoneUrlMap::getCreateTime);
        Page<SysPhoneUrlMapVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的短信映射列表
     *
     * @param bo 查询条件
     * @return 短信映射列表
     */
    @Override
    public List<SysPhoneUrlMapVo> queryList(SysPhoneUrlMapBo bo) {
        LambdaQueryWrapper<SysPhoneUrlMap> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysPhoneUrlMap> buildQueryWrapper(SysPhoneUrlMapBo bo) {
        LambdaQueryWrapper<SysPhoneUrlMap> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SysPhoneUrlMap::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getTargetUrl()), SysPhoneUrlMap::getTargetUrl, bo.getTargetUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getShortcode()), SysPhoneUrlMap::getShortcode, bo.getShortcode());
        return lqw;
    }

    /**
     * 新增短信映射
     *
     * @param bo 短信映射
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SysPhoneUrlMapBo bo) {
        SysPhoneUrlMap add = MapstructUtils.convert(bo, SysPhoneUrlMap.class);
        normalizeShortCode(add);

        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改短信映射
     *
     * @param bo 短信映射
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SysPhoneUrlMapBo bo) {
        SysPhoneUrlMap update = MapstructUtils.convert(bo, SysPhoneUrlMap.class);
        normalizeShortCode(update);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysPhoneUrlMap entity) {
        LambdaQueryWrapper<SysPhoneUrlMap> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysPhoneUrlMap::getTargetUrl, entity.getTargetUrl())
            .or()
            .eq(SysPhoneUrlMap::getShortcode, entity.getShortcode());
        wrapper.ne(entity.getId() != null, SysPhoneUrlMap::getId, entity.getId());
        Long exists = baseMapper.selectCount(wrapper);
        if (exists != null && exists > 0) {
            throw new ServiceException("目标URL或短码已存在");
        }
    }

    /**
     * 校验并批量删除短信映射信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importPhoneUrlMaps(List<SysPhoneUrlMapImportVo> importList) {
        if (CollUtil.isEmpty(importList)) {
            return 0;
        }

        List<SysPhoneUrlMap> entities = new ArrayList<>(importList.size());

        for (SysPhoneUrlMapImportVo vo : importList) {

            if (StrUtil.isBlank(vo.getTargetUrl()) || StrUtil.isBlank(vo.getShortcode())) {
                continue;
            }

            SysPhoneUrlMap entity = new SysPhoneUrlMap();
            entity.setTargetUrl(vo.getTargetUrl());
            entity.setShortcode(StrUtil.toUpperCase(vo.getShortcode()));

            try {
                validEntityBeforeSave(entity);
            } catch (ServiceException e) {
                log.warn("跳过重复的映射: url={}, shortCode={}", entity.getTargetUrl(), entity.getShortcode());
                continue;
            }

            entities.add(entity);
        }

        // ⭐ 3. 一次性批量写入
        if (CollUtil.isNotEmpty(entities)) {
            this.baseMapper.insertBatch(entities);
        }

        return entities.size();
    }

    private void normalizeShortCode(SysPhoneUrlMap entity) {
        if (StrUtil.isNotBlank(entity.getShortcode())) {
            entity.setShortcode(StrUtil.toUpperCase(entity.getShortcode()));
        }
    }

    @Override
    public Boolean updateAuditFlag(Long id, String auditFlag) {
        if (!"0".equals(auditFlag) && !"1".equals(auditFlag)) {
            throw new ServiceException("审核标志仅支持 0 或 1");
        }
        SysPhoneUrlMap current = baseMapper.selectById(id);
        if (current == null) {
            throw new ServiceException("记录不存在或已删除");
        }
        return baseMapper.updateById(current) > 0;
    }
}
