package org.dromara.common.cmpp.protocol;

import org.dromara.common.cmpp.protocol.resp.bo.*;

public interface CmppMessageListener {

    /**
     * 登录响应
     */
    void onConnectResp(CmppConnectRespBody resp);

    /**
     * 取消响应
     */
    void onCancelResp(CmppCancelRespBody resp);

    /**
     * 提交短信响应
     */
    void onSubmitResp(CmppSubmitRespBody resp);

    /**
     * 下行 DELIVER（MO 或 状态报告）
     */
    void onDeliver(CmppDeliverBody deliver);

    /**
     * 状态报告（你也可以单独拆）
     */
    void onReport(CmppDeliverBody.CmppReport report);

    /**
     * 查询返回
     */
    void onQueryResp(CmppQueryRespBody report);

    /**
     * 链路检测
     */
    void onActiveTest();

    /**
     *  心跳 回复
     */
    void onActiveTestResp();

    /**
     * 其他异常
     */
    void onError(Throwable t);
}
