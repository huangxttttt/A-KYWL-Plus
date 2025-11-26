package org.dromara.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.vo.SysPhoneUrlMapImportVo;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.SysPhoneUrlMapBo;
import org.dromara.system.domain.vo.SysPhoneUrlMapVo;
import org.dromara.system.domain.SysPhoneUrlMap;
import org.dromara.system.mapper.SysPhoneUrlMapMapper;
import org.dromara.system.service.ISysPhoneUrlMapService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysPhoneUrlMap> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SysPhoneUrlMap::getId);
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), SysPhoneUrlMap::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getPhoneNumber()), SysPhoneUrlMap::getPhoneNumber, bo.getPhoneNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getTargetUrl()), SysPhoneUrlMap::getTargetUrl, bo.getTargetUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getShortcode()), SysPhoneUrlMap::getShortcode, bo.getShortcode());
        lqw.eq(StringUtils.isNotBlank(bo.getSendStatus()), SysPhoneUrlMap::getSendStatus, bo.getSendStatus());
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
        validEntityBeforeSave(update);
        String targetUrl = update.getTargetUrl();
        String shortCode = generateShortCode(targetUrl);
        update.setShortcode(StrUtil.toUpperCase(shortCode));
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysPhoneUrlMap entity) {
        //TODO 做一些数据校验,如唯一约束
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

            if (StrUtil.isBlank(vo.getPhoneNumber()) || StrUtil.isBlank(vo.getTargetUrl())) {
                continue;
            }

            String targetUrl = vo.getTargetUrl();

            // ⭐ 1. 根据 targetUrl 直接计算短码（完全不需要缓存）
            String shortCode = generateShortCode(targetUrl);

            SysPhoneUrlMap entity = new SysPhoneUrlMap();
            entity.setUserName(LoginHelper.getUsername());
            entity.setPhoneNumber(vo.getPhoneNumber());
            entity.setTargetUrl(targetUrl);

            // ⭐ 2. 设置短码（固定逻辑）
            entity.setShortcode(StrUtil.toUpperCase(shortCode));
            entity.setSendStatus("0");

            entities.add(entity);
        }

        // ⭐ 3. 一次性批量写入
        if (CollUtil.isNotEmpty(entities)) {
            this.baseMapper.insertBatch(entities);
        }

        return entities.size();
    }


    /**
     * 短码生成规则：你可以自己实现（这里给一个简单示例，占位）
     */
    private String generateShortCode(String targetUrl) {
        // 用 targetUrl 的 hash 做短码，不会因为手机号不同而变化
        return Integer.toHexString(targetUrl.hashCode()).toUpperCase(Locale.ROOT)
            .substring(0, 6);
    }
}
