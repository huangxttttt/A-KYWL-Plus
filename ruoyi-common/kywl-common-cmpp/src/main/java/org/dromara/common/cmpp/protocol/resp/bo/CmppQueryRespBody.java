package org.dromara.common.cmpp.protocol.resp.bo;

import lombok.Data;

@Data
public class CmppQueryRespBody {
    /**
     * 查询日期 YYYYMMDD
     * 例如：20251210
     */
    private String time;

    /**
     * 查询类型：
     * 0：按当日总量查询
     * 1：按具体业务类型（Service_Id）查询
     */
    private short queryType;

    /**
     * 查询码：
     * - 当 queryType = 0 时，此字段无效
     * - 当 queryType = 1 时，本字段为 Service_Id（10字节）
     */
    private String queryCode;

    // ======================== MT（Mobile Terminated）统计 ========================

    /**
     * MT_TLMsg
     * 从 SP 接收的短信总条数（MT 提交量）
     * 包含成功与失败
     */
    private long mtTlMsg;

    /**
     * MT_Tlusr
     * 从 SP 接收用户的数量（参与发送的用户数）
     */
    private long mtTlUsr;

    /**
     * MT_Scs
     * ISMG 成功转发给短信中心（SMC）的短信数量
     * 即成功提交给运营商侧短信中心的条数
     */
    private long mtScs;

    /**
     * MT_WT
     * 仍在等待转发的短信数量（队列等待处理）
     */
    private long mtWt;

    /**
     * MT_FL
     * 转发失败的短信数量（提交 SMC 失败）
     */
    private long mtFl;

    // ======================== MO（Mobile Originated）统计 ========================

    /**
     * MO_Scs
     * ISMG 成功转发给 SP 的上行短信数量（MO 到达 SP 的数量）
     */
    private long moScs;

    /**
     * MO_WT
     * 等待发送给 SP 的上行短信数量（积压在队列中的 MO）
     */
    private long moWt;

    /**
     * MO_FL
     * 向 SP 发送失败的上行短信数量
     */
    private long moFl;
}
