package org.dromara.common.cmpp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.cmpp.config.CmppProperties;
import org.dromara.common.cmpp.protocol.CmppMessageListener;
import org.dromara.common.cmpp.protocol.send.CmppConnect;
import org.dromara.common.cmpp.protocol.send.CmppSubmit;
import org.dromara.common.cmpp.protocol.send.CmppQuery;
import org.dromara.common.cmpp.protocol.send.CmppCancel;
import org.dromara.common.cmpp.protocol.send.CmppTerminate;
import org.dromara.common.cmpp.protocol.send.CmppActiveTest;
import org.dromara.common.cmpp.util.SequenceId;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * CMPP 客户端，负责与 ISMG 建立连接并发送消息
 */
@Slf4j
public class CmppClient {

    private final CmppProperties cmppProperties;  // 用于读取配置
    private final CmppClientHandler cmppClientHandler;  // 用于读取配置

    private EventLoopGroup group;
    private Channel channel;


    @Autowired
    public CmppClient(CmppProperties cmppProperties, CmppClientHandler cmppClientHandler) {
        this.cmppProperties = cmppProperties;
        this.cmppClientHandler = cmppClientHandler;
    }

    /**
     * 启动 CMPP 客户端，连接到 ISMG（运营商短信网关）
     */
    public void start() {

        // 创建 NIO 线程池，Netty 需要使用 EventLoopGroup 来处理事件
        this.group = new NioEventLoopGroup();

        // 启动 Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(cmppClientHandler);  // 添加处理器（你自己的 handler）
                }
            });

        // 连接到 ISMG
        bootstrap.connect(cmppProperties.getHost(), cmppProperties.getPort())
            .addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("CMPP 连接成功！ {}:{}", cmppProperties.getHost(), cmppProperties.getPort());
                    channel = future.channel();
                    sendConnect();  // 发送 CONNECT 消息，开始通信
                } else {
                    log.error("CMPP 连接失败", future.cause());
                    // 这里可以按需加重连逻辑
                }
            });
    }

    /**
     * 是否已经连接且可用
     */
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    /**
     * 发送 CMPP_CONNECT 消息，建立连接
     */
    private void sendConnect() {
        if (!isConnected()) {
            log.warn("通道未连接，无法发送 CMPP_CONNECT");
            return;
        }
        int seq = SequenceId.next();
        ByteBuf buf = CmppConnect.build(cmppProperties, seq);
        channel.writeAndFlush(buf);
        log.info("发送 CMPP_CONNECT 消息，seq={}", seq);
    }

    /**
     * 向 ISMG 发送短信（CMPP_SUBMIT），默认需要状态报告
     *
     * @param phone   目标手机号
     * @param content 短信内容
     */
    public void sendSms(String phone, String content) {
        sendSms(phone, content, true);
    }

    /**
     * 单条发送
     *
     * @param phone
     * @param content
     * @param needReport
     */
    public void sendSms(String phone, String content, boolean needReport) {
        if (!isConnected()) {
            log.warn("通道未连接，无法发送短信");
            return;
        }
        try {
            List<ByteBuf> packets = CmppSubmit.buildListSingle(cmppProperties, phone, content, needReport);
            for (ByteBuf buf : packets) {
                channel.writeAndFlush(buf);
            }
            log.info("发送短信，共 {} 包，phone={}，content={}", packets.size(), phone, content);
        } catch (Exception e) {
            log.error("发送短信失败: {} -> {}", phone, content, e);
        }
    }

    /**
     * 批量发送
     *
     * @param phones
     * @param content
     * @param needReport
     */
    public void sendSmsBatch(List<String> phones, String content, boolean needReport) {
        if (!isConnected()) {
            log.warn("通道未连接，无法批量发送短信");
            return;
        }
        try {
            List<ByteBuf> packets = CmppSubmit.buildListBatch(cmppProperties, phones, content, needReport);
            for (ByteBuf buf : packets) {
                channel.writeAndFlush(buf);
            }
            log.info("批量发送短信，共 {} 包，手机号数量={}，content={}",
                packets.size(), phones.size(), content);
        } catch (Exception e) {
            log.error("批量发送短信失败", e);
        }
    }


    /**
     * 发送 CMPP_QUERY（业务统计查询）
     *
     * @param date      查询日期，格式：YYYYMMDD，例如 20251210
     * @param queryType 0：总数查询；1：按业务类型查询
     * @param queryCode 当 queryType=1 时，为 Service_Id；否则可为 null
     */
    public void sendQuery(String date, int queryType, String queryCode) {
        if (!isConnected()) {
            log.warn("通道未连接，无法发送 CMPP_QUERY");
            return;
        }
        int seq = SequenceId.next();
        ByteBuf buf = CmppQuery.build(date, queryType, queryCode, seq);
        channel.writeAndFlush(buf);
        log.info("发送 CMPP_QUERY，seq={}，date={}，type={}，code={}", seq, date, queryType, queryCode);
    }

    /**
     * 发送 CMPP_CANCEL（删除短信）
     *
     * @param msgId 要删除的短信 Msg_Id（来自 SUBMIT_RESP 或状态报告）
     */
    public void sendCancel(long msgId) {
        if (!isConnected()) {
            log.warn("通道未连接，无法发送 CMPP_CANCEL");
            return;
        }
        int seq = SequenceId.next();
        ByteBuf buf = CmppCancel.build(msgId, seq);
        channel.writeAndFlush(buf);
        log.info("发送 CMPP_CANCEL，seq={}，msgId={}", seq, msgId);
    }

    /**
     * 发送 CMPP_ACTIVE_TEST（心跳）
     * 一般由定时任务调用，例如每隔 3 分钟发送一次
     */
    public void sendActiveTest() {
        if (!isConnected()) {
            log.warn("通道未连接，无法发送 CMPP_ACTIVE_TEST");
            return;
        }
        int seq = SequenceId.next();
        ByteBuf buf = CmppActiveTest.build(seq);
        channel.writeAndFlush(buf);
        log.debug("发送 CMPP_ACTIVE_TEST，seq={}", seq);
    }

    /**
     * 发送 CMPP_TERMINATE 主动断开连接
     */
    public void sendTerminate() {
        if (!isConnected()) {
            log.warn("通道未连接或已关闭，无需发送 CMPP_TERMINATE");
            close(); // 防止资源泄露，顺便关一下 group
            return;
        }
        int seq = SequenceId.next();
        ByteBuf buf = CmppTerminate.build(seq);
        channel.writeAndFlush(buf).addListener((ChannelFutureListener) future -> {
            log.info("发送 CMPP_TERMINATE，seq={}", seq);
            close();
        });
    }

    /**
     * 关闭连接及线程池
     */
    public void close() {
        try {
            if (channel != null) {
                channel.close();
                log.info("CMPP 通道已关闭");
            }
        } catch (Exception e) {
            log.warn("关闭 channel 时异常", e);
        }
        if (group != null) {
            group.shutdownGracefully();
            log.info("CMPP NioEventLoopGroup 已关闭");
        }
    }
}
