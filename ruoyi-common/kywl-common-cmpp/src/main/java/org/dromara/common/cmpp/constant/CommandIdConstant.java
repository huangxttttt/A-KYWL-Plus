package org.dromara.common.cmpp.constant;

public class CommandIdConstant {

    // CMPP Command_Id 定义，参考《中国移动通信 CMPP2.0 协议》

    /** 0x00000001 — 请求连接（SP → ISMG 或 ISMG → ISMG） */
    public static final int CMPP_CONNECT = 0x00000001;

    /** 0x80000001 — 请求连接应答（ISMG → SP） */
    public static final int CMPP_CONNECT_RESP = 0x80000001;

    /** 0x00000002 — 终止连接请求 */
    public static final int CMPP_TERMINATE = 0x00000002;

    /** 0x80000002 — 终止连接响应 */
    public static final int CMPP_TERMINATE_RESP = 0x80000002;

    /** 0x00000004 — 提交短信（SP → ISMG） */
    public static final int CMPP_SUBMIT = 0x00000004;

    /** 0x80000004 — 提交短信应答（ISMG → SP） */
    public static final int CMPP_SUBMIT_RESP = 0x80000004;

    /** 0x00000005 — 短信下发（ISMG → SP，用于 MO 或状态报告） */
    public static final int CMPP_DELIVER = 0x00000005;

    /** 0x80000005 — 下发短信应答（SP → ISMG） */
    public static final int CMPP_DELIVER_RESP = 0x80000005;

    /** 0x00000006 — 发送短信状态查询（SP → ISMG） */
    public static final int CMPP_QUERY = 0x00000006;

    /** 0x80000006 — 发送短信状态查询应答（ISMG → SP） */
    public static final int CMPP_QUERY_RESP = 0x80000006;

    /** 0x00000007 — 删除短信请求（SP → ISMG） */
    public static final int CMPP_CANCEL = 0x00000007;

    /** 0x80000007 — 删除短信应答（ISMG → SP） */
    public static final int CMPP_CANCEL_RESP = 0x80000007;

    /** 0x00000008 — 链路检测（长连接保活） */
    public static final int CMPP_ACTIVE_TEST = 0x00000008;

    /** 0x80000008 — 链路检测响应 */
    public static final int CMPP_ACTIVE_TEST_RESP = 0x80000008;

    /** 0x00000009 — 消息前转（ISMG → ISMG，用于跨网关转发 MT/MO/状态报告） */
    public static final int CMPP_FWD = 0x00000009;

    /** 0x80000009 — 消息前转应答 */
    public static final int CMPP_FWD_RESP = 0x80000009;

    /** 0x00000010 — MT 路由请求（ISMG → GNS） */
    public static final int CMPP_MT_ROUTE = 0x00000010;

    /** 0x80000010 — MT 路由请求应答（GNS → ISMG） */
    public static final int CMPP_MT_ROUTE_RESP = 0x80000010;

    /** 0x00000011 — MO 路由请求（ISMG → GNS） */
    public static final int CMPP_MO_ROUTE = 0x00000011;

    /** 0x80000011 — MO 路由请求应答（GNS → ISMG） */
    public static final int CMPP_MO_ROUTE_RESP = 0x80000011;

    /** 0x00000012 — 获取路由请求（ISMG → GNS，获取所有 MO/MT 路由） */
    public static final int CMPP_GET_ROUTE = 0x00000012;

    /** 0x80000012 — 获取路由请求应答（GNS → ISMG） */
    public static final int CMPP_GET_ROUTE_RESP = 0x80000012;

    /** 0x00000013 — MT 路由更新（ISMG → GNS） */
    public static final int CMPP_MT_ROUTE_UPDATE = 0x00000013;

    /** 0x80000013 — MT 路由更新应答（GNS → ISMG） */
    public static final int CMPP_MT_ROUTE_UPDATE_RESP = 0x80000013;

    /** 0x00000014 — MO 路由更新（ISMG → GNS） */
    public static final int CMPP_MO_ROUTE_UPDATE = 0x00000014;

    /** 0x80000014 — MO 路由更新应答（GNS → ISMG） */
    public static final int CMPP_MO_ROUTE_UPDATE_RESP = 0x80000014;

    /** 0x00000015 — 推送 MT 路由更新（GNS → ISMG） */
    public static final int CMPP_PUSH_MT_ROUTE_UPDATE = 0x00000015;

    /** 0x80000015 — 推送 MT 路由更新应答（ISMG → GNS） */
    public static final int CMPP_PUSH_MT_ROUTE_UPDATE_RESP = 0x80000015;

    /** 0x00000016 — 推送 MO 路由更新（GNS → ISMG） */
    public static final int CMPP_PUSH_MO_ROUTE_UPDATE = 0x00000016;

    /** 0x80000016 — 推送 MO 路由更新应答（ISMG → GNS） */
    public static final int CMPP_PUSH_MO_ROUTE_UPDATE_RESP = 0x80000016;

}
