package org.dromara.system.sms.cron;

import org.dromara.common.cmpp.client.CmppClient;
import org.dromara.system.sms.manager.CmppClientManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CmppActiveSchedul {

    private final CmppClientManager cmppClientManager;

    public CmppActiveSchedul(CmppClientManager cmppClientManager) {
        this.cmppClientManager = cmppClientManager;
    }

    @Scheduled(cron = "0 */3 * * * ?")
    public void doTask() {
        for (CmppClient client : cmppClientManager.listClients()) {
            if (client.isConnected()) {
                client.sendActiveTest();
            }
        }
    }

}
