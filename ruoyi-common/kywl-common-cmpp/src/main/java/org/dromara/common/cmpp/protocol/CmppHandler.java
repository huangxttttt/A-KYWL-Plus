package org.dromara.common.cmpp.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

@Data
public class CmppHandler {

    /**
     * 消息总长度(含消息头及消息体)
     */
    private int totalLength;
    /**
     * 命令或响应类型
     */
    private int commandId;
    /**
     * 消息流水号,顺序累加,步长为1,循环使用（一对请求和应答消息的流水号必须相同）
     */
    private int sequenceId;

    /**
     * 消息构建
     *
     * @param commandId
     * @param body
     * @return
     */
    public static ByteBuf handle(int commandId, ByteBuf body, int seq) {
        int totalLength = 12 + body.readableBytes();
        ByteBuf packet = Unpooled.buffer(totalLength);
        packet.writeInt(totalLength);
        packet.writeInt(commandId);
        packet.writeInt(seq);
        packet.writeBytes(body);
        return packet;
    }
}
