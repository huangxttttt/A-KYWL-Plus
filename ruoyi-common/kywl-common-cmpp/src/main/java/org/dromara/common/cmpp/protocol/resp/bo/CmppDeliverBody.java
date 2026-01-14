package org.dromara.common.cmpp.protocol.resp.bo;

import lombok.Data;

@Data
public class CmppDeliverBody {
    private long msgId;
    private String destId;
    private String serviceId;
    private short tpPid;
    private short tpUdhi;
    private short msgFmt;
    private String srcTerminalId;
    private short registeredDelivery;
    private short msgLength;
    private byte[] msgContent;
    private String reserved;

    // 如果是状态报告，进一步解析为：
    private CmppReport report;

    @Data
    public static class CmppReport {
        private long msgId;
        private String stat;
        private String submitTime;
        private String doneTime;
        private String destTerminalId;
        private long smscSequence;
    }
}
