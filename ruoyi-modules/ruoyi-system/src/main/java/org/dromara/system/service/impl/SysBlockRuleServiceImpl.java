package org.dromara.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.SettingUtil;
import cn.idev.excel.util.IntUtils;
import cn.idev.excel.util.MapUtils;
import jakarta.annotation.PostConstruct;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ip.AddressUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.SysBlockRuleBo;
import org.dromara.system.domain.vo.SysBlockRuleVo;
import org.dromara.system.domain.SysBlockRule;
import org.dromara.system.mapper.SysBlockRuleMapper;
import org.dromara.system.service.ISysBlockRuleService;

import java.util.*;

/**
 * 访问拦截规则（支持IP/地区）Service业务层处理
 *
 * @author huangxt
 * @date 2025-11-20
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysBlockRuleServiceImpl implements ISysBlockRuleService {



    private final SysBlockRuleMapper baseMapper;

    /**
     * 查询访问拦截规则（支持IP/地区）
     *
     * @param id 主键
     * @return 访问拦截规则（支持IP/地区）
     */
    @Override
    public SysBlockRuleVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询访问拦截规则（支持IP/地区）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 访问拦截规则（支持IP/地区）分页列表
     */
    @Override
    public TableDataInfo<SysBlockRuleVo> queryPageList(SysBlockRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysBlockRule> lqw = buildQueryWrapper(bo);
        Page<SysBlockRuleVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的访问拦截规则（支持IP/地区）列表
     *
     * @param bo 查询条件
     * @return 访问拦截规则（支持IP/地区）列表
     */
    @Override
    public List<SysBlockRuleVo> queryList(SysBlockRuleBo bo) {
        LambdaQueryWrapper<SysBlockRule> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysBlockRule> buildQueryWrapper(SysBlockRuleBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysBlockRule> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SysBlockRule::getId);
        lqw.eq(bo.getRuleType() != null, SysBlockRule::getRuleType, bo.getRuleType());
        lqw.eq(StringUtils.isNotBlank(bo.getIpAddress()), SysBlockRule::getIpAddress, bo.getIpAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getIpCidr()), SysBlockRule::getIpCidr, bo.getIpCidr());
        lqw.eq(StringUtils.isNotBlank(bo.getCountry()), SysBlockRule::getCountry, bo.getCountry());
        lqw.eq(StringUtils.isNotBlank(bo.getProvince()), SysBlockRule::getProvince, bo.getProvince());
        lqw.eq(StringUtils.isNotBlank(bo.getCity()), SysBlockRule::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getReason()), SysBlockRule::getReason, bo.getReason());
        lqw.eq(bo.getStatus() != null, SysBlockRule::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增访问拦截规则（支持IP/地区）
     *
     * @param bo 访问拦截规则（支持IP/地区）
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SysBlockRuleBo bo) {
        SysBlockRule add = MapstructUtils.convert(bo, SysBlockRule.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            //存储缓存
            int ruleType = bo.getRuleType().intValue();
            handlerIP(true, ruleType, add);
        }
        return flag;
    }

    /**
     * 修改访问拦截规则（支持IP/地区）
     *
     * @param bo 访问拦截规则（支持IP/地区）
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SysBlockRuleBo bo) {
        SysBlockRule update = MapstructUtils.convert(bo, SysBlockRule.class);
        validEntityBeforeSave(update);
        boolean b = baseMapper.updateById(update) > 0;
        if (b) {
            int type = bo.getRuleType().intValue();
            handlerIP(false, type, update);
            handlerIP(true, type, update);
        }
        return b;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysBlockRule entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除访问拦截规则（支持IP/地区）信息
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
        List<SysBlockRule> sysBlockRules = baseMapper.selectByIds(ids);
        boolean b = baseMapper.deleteByIds(ids) > 0;
        if (b) {
            sysBlockRules.stream().forEach(sysBlockRule -> {
                int type = sysBlockRule.getRuleType().intValue();
                handlerIP(false, type, sysBlockRule);
            });
        }
        return b;
    }

    @Override
    public Boolean checkRule(String ip) {
        try {
            if (StrUtil.isBlank(ip)) {
                return false;
            }
            ip = StrUtil.trim(ip);

            // 1. 地域拦截
            String address = AddressUtils.getRealAddressByIP(ip);
            if (StrUtil.isNotBlank(address)) {
                List<String> addrs = StrUtil.split(address, '|');
                Set<String> citySet = RedisUtils.getCacheSet(CacheConstants.SYSTEM_RULE_CITY);

                if (CollUtil.isNotEmpty(addrs) && CollUtil.isNotEmpty(citySet)) {
                    if (addrs.stream().anyMatch(citySet::contains)) {
                        return true;
                    }
                }
            }

            // 2. IP 段拦截
            String beforeIp = StrUtil.subBefore(ip, StrUtil.DOT, true);
            String afterIP  = StrUtil.subAfter(ip, StrUtil.DOT, true);
            if (StrUtil.hasBlank(beforeIp, afterIP)) {
                return false;
            }

            Map<String, Set<String>> ipCacheMap = RedisUtils.getCacheMap(CacheConstants.SYSTEM_RULE_IP);
            if (CollUtil.isEmpty(ipCacheMap)) {
                return false;
            }

            Set<String> afterIpSet = ipCacheMap.get(beforeIp);
            if (CollUtil.isEmpty(afterIpSet)) {
                return false;
            }

            return afterIpSet.contains(afterIP);
        } catch (Exception e) {
            // 这里可以打日志，防止一个异常影响整个请求
            log.error("checkRule error, ip={}", ip, e);
            return false;
        }
    }


    @PostConstruct
    public void init() {
        LambdaQueryWrapper<SysBlockRule> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysBlockRule::getStatus, 0);
        List<SysBlockRule> sysBlockRules = baseMapper.selectList(queryWrapper);
        sysBlockRules.stream().forEach(sysBlockRule -> {
            int type = sysBlockRule.getRuleType().intValue();
            handlerIP(true, type, sysBlockRule);
        });
    }


    /**
     * 处理拦截存储
     *
     * @param isAdd
     * @param type
     * @param sysBlockRule
     */
    @SuppressWarnings("unchecked")
    private void handlerIP(boolean isAdd, int type, SysBlockRule sysBlockRule) {
        switch (type) {
            // IP / CIDR
            case 1:
            case 2: {
                // 从 Redis 取出一个可变 Map
                Map<String, Object> cacheMap = RedisUtils.getCacheMap(CacheConstants.SYSTEM_RULE_IP);
                if (cacheMap == null) {
                    cacheMap = new HashMap<>();
                } else if (!(cacheMap instanceof HashMap)) {
                    // 保险起见再拷一份，防止是其他不可变实现
                    cacheMap = new HashMap<>(cacheMap);
                }

                String ipAddress = sysBlockRule.getIpAddress();
                if (StrUtil.isBlank(ipAddress)) {
                    ipAddress = sysBlockRule.getIpCidr();
                }
                // 没有 IP 信息就不用处理了
                if (StrUtil.isBlank(ipAddress)) {
                    break;
                }

                String beforeIp = StrUtil.subBefore(ipAddress, StrUtil.DOT, true);
                String afterIp = StrUtil.subAfter(ipAddress, StrUtil.DOT, true);

                Set<String> afterSet = MapUtil.get(cacheMap, beforeIp, Set.class);
                if (afterSet == null) {
                    afterSet = new HashSet<>();
                } else if (!(afterSet instanceof HashSet)) {
                    // 防止 Redis 里存的是不可变 Set，这里 copy 一份
                    afterSet = new HashSet<>(afterSet);
                }

                boolean changed = isAdd ? afterSet.add(afterIp) : afterSet.remove(afterIp);
                if (changed) {
                    cacheMap.put(beforeIp, afterSet);
                    RedisUtils.setCacheMap(CacheConstants.SYSTEM_RULE_IP, cacheMap);
                }
                break;
            }

            // 国家 / 省份 / 城市
            case 3:
            case 4:
            case 5: {
                Set<String> cacheSet = RedisUtils.getCacheSet(CacheConstants.SYSTEM_RULE_CITY);
                if (cacheSet == null) {
                    cacheSet = new HashSet<>();
                } else if (!(cacheSet instanceof HashSet)) {
                    // 同样防止拿到的是不可变 Set
                    cacheSet = new HashSet<>(cacheSet);
                }

                String country = sysBlockRule.getCountry();
                if (StrUtil.isBlank(country)) {
                    country = sysBlockRule.getProvince();
                    if (StrUtil.isBlank(country)) {
                        country = sysBlockRule.getCity();
                    }
                }
                // 没有任何地区信息就不用处理
                if (StrUtil.isBlank(country)) {
                    break;
                }

                boolean changed = isAdd ? cacheSet.add(country) : cacheSet.remove(country);
                if (changed) {
                    RedisUtils.setCacheSet(CacheConstants.SYSTEM_RULE_CITY, cacheSet);
                }
                break;
            }

            default:
                break;
        }
    }


}
