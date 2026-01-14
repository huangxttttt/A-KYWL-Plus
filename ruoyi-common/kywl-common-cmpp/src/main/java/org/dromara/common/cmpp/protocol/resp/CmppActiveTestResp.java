package org.dromara.common.cmpp.protocol.resp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;

/**
 * CMPP_ACTIVE_TEST_RESP
 * 消息体: 1 字节 Reserved
 */
public class CmppActiveTestResp {

    public static ByteBuf build(int sequenceId) {
        ByteBuf body = Unpooled.buffer(1);
        body.writeByte(0); // Reserved
        return CmppHandler.handle(CommandIdConstant.CMPP_ACTIVE_TEST_RESP, body, sequenceId);
    }
}
