package org.dromara.common.cmpp.protocol.send;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.cmpp.config.CmppProperties;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CmppConnect {


    public static ByteBuf build(CmppProperties props, int sequenceId) {

        // 时间戳 MMDDHHMMSS
        String ts = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("MMddHHmmss"));

        // AuthenticatorSource = MD5(SP_ID + 9字节0 + sharedSecret + timestamp)
        byte[] authBytes = MD5.create().digest(
            (props.getSpId()
                + StrUtil.repeat('\0', 9)
                + props.getSharedSecret()
                + ts).getBytes(StandardCharsets.UTF_8)
        );

        ByteBuf body = Unpooled.buffer();
        body.writeBytes(StrUtil.padAfter(props.getSpId(), 6, ' ').getBytes());
        body.writeBytes(authBytes);
        body.writeByte(props.getVersion());
        body.writeInt(Integer.parseInt(ts));

        ByteBuf packet = CmppHandler.handle(CommandIdConstant.CMPP_CONNECT, body, sequenceId);
        return packet;
    }
}
