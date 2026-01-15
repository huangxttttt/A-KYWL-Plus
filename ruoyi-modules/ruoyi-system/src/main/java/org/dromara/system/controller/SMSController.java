package org.dromara.system.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.cmpp.client.CmppClient;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.SysBlockRuleBo;
import org.dromara.system.domain.vo.SysBlockRuleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/cmpp")
public class SMSController {

    private final CmppClient cmppClient;

    @GetMapping("/send")
    public R send() {
        cmppClient.sendSms("15806048221", "test 11111");
        return R.ok();
    }
}
