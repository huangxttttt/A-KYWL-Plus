package org.dromara.common.cmpp.protocol.resp.bo;

import lombok.Data;

@Data
public class CmppCancelRespBody {
    private short successId; // 0 success, 1 fail
}
