package org.dromara.common.cmpp.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;
import org.dromara.common.cmpp.protocol.CmppMessageListener;
import org.dromara.common.cmpp.protocol.resp.CmppActiveTestResp;
import org.dromara.common.cmpp.protocol.resp.bo.*;
import org.dromara.common.cmpp.protocol.send.CmppDeliverResp;
import org.dromara.common.cmpp.util.SequenceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class CmppClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    private CmppMessageListener cmppMessageListener;

   /* private static CmppClientHandler clientHandler;


    @PostConstruct
    public void init() {
        clientHandler = this;
    }*/

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf frame) throws Exception {
        // 1. 先解析公共头部
        CmppHandler header = new CmppHandler();
        header.setTotalLength(frame.readInt());
        header.setCommandId(frame.readInt());
        header.setSequenceId(frame.readInt());

        int cmd = header.getCommandId();

        switch (cmd) {
            case CommandIdConstant.CMPP_CONNECT_RESP:
                handleConnectResp(ctx, header, frame);
                break;
            case CommandIdConstant.CMPP_SUBMIT_RESP:
                handleSubmitResp(ctx, header, frame);
                break;
            case CommandIdConstant.CMPP_DELIVER:
                handleDeliver(ctx, header, frame);
                break;
            case CommandIdConstant.CMPP_ACTIVE_TEST:
                handleActiveTest(ctx, header, frame);
                break;
            case CommandIdConstant.CMPP_ACTIVE_TEST_RESP:
                handleActiveTestResp(ctx, header, frame);
                break;
            case CommandIdConstant.CMPP_QUERY_RESP:
                handleQueryResp(ctx, header, frame);
                break;
            case CommandIdConstant.CMPP_CANCEL_RESP:
                handleCancelResp(ctx, header, frame);
                break;
            case CommandIdConstant.CMPP_TERMINATE_RESP:
                handleTerminateResp(ctx, header, frame);
                break;
            default:
                log.warn("收到未知命令 commandId = {}", cmd);
                frame.skipBytes(frame.readableBytes());
        }
    }


    private void handleCancelResp(ChannelHandlerContext ctx,
                                  CmppHandler header,
                                  ByteBuf body) {

        CmppCancelRespBody resp = new CmppCancelRespBody();
        resp.setSuccessId(body.readUnsignedByte());

        log.info("收到 CMPP_CANCEL_RESP seq={} success={}",
            header.getSequenceId(), resp.getSuccessId());
        cmppMessageListener.onCancelResp(resp);
    }


    private void handleConnectResp(ChannelHandlerContext ctx,
                                   CmppHandler header,
                                   ByteBuf body) {

        CmppConnectRespBody resp = new CmppConnectRespBody();
        resp.setStatus(body.readUnsignedByte()); // 1 字节无符号
        byte[] auth = new byte[16];
        body.readBytes(auth);
        resp.setAuthenticator(auth);
        resp.setVersion(body.readUnsignedByte());

        log.info("CMPP_CONNECT_RESP seq={} status={}",
            header.getSequenceId(), resp.getStatus());

        if (resp.getStatus() != 0) {
            log.error("登录失败，status={}", resp.getStatus());
            // 根据需要做重连 / 关链等处理
        } else {
            log.info("登录成功，开始正常收发");
        }

        cmppMessageListener.onConnectResp(resp);
    }


    private void handleQueryResp(ChannelHandlerContext ctx,
                                 CmppHandler header,
                                 ByteBuf body) {

        CmppQueryRespBody resp = new CmppQueryRespBody();

        resp.setTime(readFixedString(body, 8));
        resp.setQueryType(body.readUnsignedByte());
        resp.setQueryCode(readFixedString(body, 10));

        resp.setMtTlMsg(body.readUnsignedInt());
        resp.setMtTlUsr(body.readUnsignedInt());
        resp.setMtScs(body.readUnsignedInt());
        resp.setMtWt(body.readUnsignedInt());
        resp.setMtFl(body.readUnsignedInt());

        resp.setMoScs(body.readUnsignedInt());
        resp.setMoWt(body.readUnsignedInt());
        resp.setMoFl(body.readUnsignedInt());

        log.info("收到 CMPP_QUERY_RESP seq={} result={}",
            header.getSequenceId(), resp);
        cmppMessageListener.onQueryResp(resp);
    }


    private void handleTerminateResp(ChannelHandlerContext ctx,
                                     CmppHandler header,
                                     ByteBuf body) {

        log.info("收到 CMPP_TERMINATE_RESP seq={}", header.getSequenceId());
        // TERMINATE_RESP 无消息体，无需解析
    }

    private void handleDeliver(ChannelHandlerContext ctx,
                               CmppHandler header,
                               ByteBuf body) throws Exception {

        CmppDeliverBody deliver = new CmppDeliverBody();

        deliver.setMsgId(body.readLong());
        deliver.setDestId(readFixedString(body, 21));
        deliver.setServiceId(readFixedString(body, 10));
        deliver.setTpPid(body.readUnsignedByte());
        deliver.setTpUdhi(body.readUnsignedByte());
        deliver.setMsgFmt(body.readUnsignedByte());
        deliver.setSrcTerminalId(readFixedString(body, 21));
        deliver.setRegisteredDelivery(body.readUnsignedByte());
        short msgLength = (short) body.readUnsignedByte();
        deliver.setMsgLength(msgLength);

        byte[] msgBytes = new byte[msgLength];
        body.readBytes(msgBytes);
        deliver.setMsgContent(msgBytes);

        deliver.setReserved(readFixedString(body, 8));

        if (deliver.getRegisteredDelivery() == 1) {
            // 状态报告
            CmppDeliverBody.CmppReport report = parseReport(msgBytes);
            deliver.setReport(report);
            log.info("收到状态报告 seq={} report={}", header.getSequenceId(), report);
            //SUBMIT_RESP.Msg_Id
            // TODO: 根据 report.getStat() 更新你库里这条短信的状态
            //subimt之后，submitresp会返回msg_id，我可以将这个id更新到我的短信记录中，然后收到报告的时候也会收到msgid，这个id与之前submitresp收到的id一样，所以我可以利用这个作为关联更新短信状态

        } else {
            // 上行短信（MO）
            String content;
            if (deliver.getMsgFmt() == 8) { // UCS2
                content = new String(msgBytes, "UTF-16BE");
            } else {
                content = new String(msgBytes); // 简单处理，按需要再细分
            }
            log.info("收到上行短信 seq={} from={} to={} content={}",
                header.getSequenceId(),
                deliver.getSrcTerminalId(),
                deliver.getDestId(),
                content);

            // TODO: 把上行转给你的业务系统
        }
        cmppMessageListener.onDeliver(deliver);

        // ⭐ 必须回 DELIVER_RESP
        int seq = SequenceId.next();
        ByteBuf resp = CmppDeliverResp.build(deliver.getMsgId(), 0, seq);
        ctx.writeAndFlush(resp);
    }

    private void handleActiveTest(ChannelHandlerContext ctx,
                                  CmppHandler header,
                                  ByteBuf body) {
        // ACTIVE_TEST 无消息体，直接回 RESP 即可
        int seq = SequenceId.next();
        ctx.writeAndFlush(CmppActiveTestResp.build(seq));
//        cmppMessageListener.onActiveTest();
    }

    private void handleActiveTestResp(ChannelHandlerContext ctx,
                                      CmppHandler header,
                                      ByteBuf body) {
        // body 只有 1 字节 Reserved，可以忽略
        if (body.isReadable()) {
            body.readByte();
        }
        log.debug("收到 ACTIVE_TEST_RESP seq={}", header.getSequenceId());
        cmppMessageListener.onActiveTestResp();
    }


    private void handleSubmitResp(ChannelHandlerContext ctx,
                                  CmppHandler header,
                                  ByteBuf body) {

        CmppSubmitRespBody resp = new CmppSubmitRespBody();
        resp.setMsgId(body.readLong());
        resp.setResult(body.readUnsignedByte());

        log.info("CMPP_SUBMIT_RESP seq={} msgId={} result={}",
            header.getSequenceId(), resp.getMsgId(), resp.getResult());

        // 这里可以用 sequenceId 或 msgId 去唤醒你之前发送那条短信对应的 Future
        // pendingMap.remove(header.getSequenceId()).complete(resp);
        cmppMessageListener.onSubmitResp(resp);
    }


    private String readFixedString(ByteBuf buf, int len) {
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        // 去掉尾部 0 或空格
        int end = bytes.length;
        while (end > 0 && (bytes[end - 1] == 0 || bytes[end - 1] == ' ')) {
            end--;
        }
        return new String(bytes, 0, end);
    }


    private CmppDeliverBody.CmppReport parseReport(byte[] content) {
        ByteBuf buf = io.netty.buffer.Unpooled.wrappedBuffer(content);
        CmppDeliverBody.CmppReport r = new CmppDeliverBody.CmppReport();

        r.setMsgId(buf.readLong());
        byte[] statBytes = new byte[7];
        buf.readBytes(statBytes);
        r.setStat(new String(statBytes).trim());

        byte[] submit = new byte[10];
        buf.readBytes(submit);
        r.setSubmitTime(new String(submit).trim());

        byte[] done = new byte[10];
        buf.readBytes(done);
        r.setDoneTime(new String(done).trim());

        byte[] dest = new byte[21];
        buf.readBytes(dest);
        r.setDestTerminalId(new String(dest).trim());

        r.setSmscSequence(buf.readUnsignedInt());

        buf.release();
        return r;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("连接异常", cause);
        ctx.close();
    }

}
