package org.dromara.common.cmpp.protocol.send;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;

/**
 * CMPP_ACTIVE_TEST
 * 无消息体
 */
public class CmppActiveTest {

    public static ByteBuf build(int sequenceId) {
        ByteBuf body = Unpooled.buffer(0);
        return CmppHandler.handle(CommandIdConstant.CMPP_ACTIVE_TEST, body, sequenceId);
    }
}
