package org.dromara.common.cmpp.protocol.send;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;

/**
 * CMPP_TERMINATE
 * 无消息体
 */
public class CmppTerminate {

    public static ByteBuf build(int sequenceId) {
        ByteBuf body = Unpooled.buffer(0);
        return CmppHandler.handle(CommandIdConstant.CMPP_TERMINATE, body, sequenceId);
    }
}
