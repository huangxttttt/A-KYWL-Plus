package org.dromara.test;

import org.dromara.common.cmpp.client.CmppClient;
import org.dromara.system.sms.manager.CmppClientManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Simple unit test example.
 */
@SpringBootTest
public class SimpleUnitTest {

    @Autowired
    private CmppClientManager cmppClientManager;

    @Test
    public void test1() {
        String spId = "demo";
        CmppClient cmppClient = cmppClientManager.getClient(spId);
        if (cmppClient != null) {
            cmppClient.sendSms("15806048221", "TEST MESSAGE");
        }
    }

}
