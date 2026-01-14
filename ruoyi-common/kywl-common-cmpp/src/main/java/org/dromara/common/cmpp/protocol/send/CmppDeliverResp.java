package org.dromara.common.cmpp.protocol.send;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;

/**
 * CMPP_DELIVER_RESP
 */
public class CmppDeliverResp {

    /**
     * @param msgId      下发时携带的 Msg_Id（从 DELIVER 里解析出来）
     * @param result     0 正常，其它错误码
     * @param sequenceId Sequence_Id
     */
    public static ByteBuf build(long msgId, int result, int sequenceId) {
        ByteBuf body = Unpooled.buffer(9);
        body.writeLong(msgId);
        body.writeByte(result);
        return CmppHandler.handle(CommandIdConstant.CMPP_DELIVER_RESP, body, sequenceId);
    }
}
