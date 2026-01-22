package org.dromara.system.sms.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.cmpp.client.CmppClient;
import org.dromara.system.sms.manager.CmppClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/cmpp")
public class SMSController {

    private final CmppClientManager cmppClientManager;

    @GetMapping("/send")
    public R send(@RequestParam("spId") String spId,
                  @RequestParam("phone") String phone,
                  @RequestParam("content") String content) {
        CmppClient cmppClient = cmppClientManager.getClient(spId);
        if (cmppClient == null) {
            return R.fail("CMPP SP config not found or disabled");
        }
        cmppClient.sendSms(phone, content);
        return R.ok();
    }
}
