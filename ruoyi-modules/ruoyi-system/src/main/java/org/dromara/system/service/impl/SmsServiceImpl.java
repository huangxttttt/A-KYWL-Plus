package org.dromara.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.cmpp.protocol.CmppMessageListener;
import org.dromara.common.cmpp.protocol.resp.bo.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsServiceImpl implements CmppMessageListener {
    @Override
    public void onConnectResp(CmppConnectRespBody resp) {
        log.info("onConnectResp");
    }

    @Override
    public void onCancelResp(CmppCancelRespBody resp) {

    }

    @Override
    public void onSubmitResp(CmppSubmitRespBody resp) {
        log.info("onSubmitResp");
    }

    @Override
    public void onDeliver(CmppDeliverBody deliver) {
        log.info("onDeliver");
    }

    @Override
    public void onReport(CmppDeliverBody.CmppReport report) {
        log.info("onReport");
    }

    @Override
    public void onQueryResp(CmppQueryRespBody report) {

    }

    @Override
    public void onActiveTest() {
        log.info("onActiveTest");
    }

    @Override
    public void onError(Throwable t) {
        log.error("onError");
    }
}
