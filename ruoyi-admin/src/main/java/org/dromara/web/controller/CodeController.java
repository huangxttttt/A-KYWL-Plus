package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.system.domain.bo.SysPhoneUrlMapBo;
import org.dromara.system.domain.vo.SysPhoneUrlMapVo;
import org.dromara.system.service.ISysBlockRuleService;
import org.dromara.system.service.ISysPhoneUrlMapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SaIgnore
@RequiredArgsConstructor
@Controller
public class CodeController {

    private final ISysBlockRuleService sysBlockRuleService;
    private final ISysPhoneUrlMapService sysPhoneUrlMapService;
    @Value("${cywl.mall-url}")
    private String url;

    @GetMapping("/redirect/{code}")
    public RedirectView redirectByCode(@PathVariable String code) {
        // 兜底地址（商场首页之类）
        String fallbackUrl = url;

        // 1. 获取 IP
        String clientIp = StrUtil.trimToEmpty(ServletUtils.getClientIP());
//        String clientIp = "203.0.113.10";

        // 1.2 没缓存，真正执行规则判断
        boolean blocked = Boolean.TRUE.equals(sysBlockRuleService.checkRule(clientIp));
        if (blocked) {
            return new RedirectView(fallbackUrl);
        }

        // ==================== 2️⃣ code → targetUrl 缓存 ====================
        if (StrUtil.isBlank(code)) {
            return new RedirectView(fallbackUrl);
        }

        String codeCacheKey = "system:shorturl:code:" + code;

        // 2.1 先从 Redis 拿目标地址
        String targetUrl = RedisUtils.getCacheObject(codeCacheKey);
        if (StrUtil.isNotBlank(targetUrl)) {
            return new RedirectView(targetUrl);
        }

        // 2.2 没缓存再查库
        try {
            SysPhoneUrlMapBo queryBo = new SysPhoneUrlMapBo();
            queryBo.setShortcode(code);
            List<SysPhoneUrlMapVo> list = sysPhoneUrlMapService.queryList(queryBo);

            if (CollUtil.isNotEmpty(list)) {
                SysPhoneUrlMapVo vo = list.get(0);
                targetUrl = vo.getTargetUrl();
                if (StrUtil.isNotBlank(targetUrl)) {
                    // 查到后写缓存，比如 30 分钟
                    RedisUtils.setCacheObject(codeCacheKey, targetUrl, Duration.ofMinutes(30));
                    return new RedirectView(targetUrl);
                }
            }
        } catch (Exception e) {
            log.error("redirectByCode error, code={}, ip={}", code, clientIp, e);
        }

        // 2.3 查不到 / 异常 → 兜底
        return new RedirectView(fallbackUrl);
    }



}

