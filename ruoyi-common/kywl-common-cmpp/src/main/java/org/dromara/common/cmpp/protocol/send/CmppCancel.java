package org.dromara.common.cmpp.protocol.send;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;

/**
 * CMPP_CANCEL
 */
public class CmppCancel {

    public static ByteBuf build(long msgId, int sequenceId) {
        ByteBuf body = Unpooled.buffer(8);
        body.writeLong(msgId);
        return CmppHandler.handle(CommandIdConstant.CMPP_CANCEL, body, sequenceId);
    }
}
