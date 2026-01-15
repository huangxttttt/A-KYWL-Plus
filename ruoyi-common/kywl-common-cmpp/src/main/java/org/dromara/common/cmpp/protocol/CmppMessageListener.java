package org.dromara.common.cmpp.protocol;

import org.dromara.common.cmpp.protocol.resp.bo.CmppConnectRespBody;
import org.dromara.common.cmpp.protocol.resp.bo.CmppDeliverBody;
import org.dromara.common.cmpp.protocol.resp.bo.CmppSubmitRespBody;

public interface CmppMessageListener {

    /** 登录响应 */
    void onConnectResp(CmppConnectRespBody resp);

    /** 提交短信响应 */
    void onSubmitResp(CmppSubmitRespBody resp);

    /** 下行 DELIVER（MO 或 状态报告） */
    void onDeliver(CmppDeliverBody deliver);

    /** 状态报告（你也可以单独拆） */
    void onReport(CmppDeliverBody.CmppReport report);

    /** 链路检测 */
    void onActiveTest();

    /** 其他异常 */
    void onError(Throwable t);
}
