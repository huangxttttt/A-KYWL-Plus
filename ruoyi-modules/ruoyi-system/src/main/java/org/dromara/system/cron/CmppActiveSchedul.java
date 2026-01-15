package org.dromara.system.cron;

import org.dromara.common.cmpp.client.CmppClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CmppActiveSchedul {

    @Autowired
    private CmppClient cmppClient;

    @Scheduled(cron = "0 */3 * * * ?")
    public void doTask() {
        cmppClient.sendActiveTest();
    }

}
