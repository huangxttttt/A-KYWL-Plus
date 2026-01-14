package org.dromara.common.cmpp.protocol.resp.bo;

import lombok.Data;

@Data
public class CmppConnectRespBody {
    private short status;          // 1 字节，用 short 接住无符号
    private byte[] authenticator;  // 16 字节
    private short version;         // 1 字节
}
