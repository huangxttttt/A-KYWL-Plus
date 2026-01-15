package org.dromara.test;

import org.dromara.common.cmpp.client.CmppClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Simple unit test example.
 */
@SpringBootTest
public class SimpleUnitTest {

    @Autowired
    private CmppClient cmppClient;

    @Test
    public void test1() {
        cmppClient.sendSms("15806048221", "TEST MESSAGE");
    }

}
