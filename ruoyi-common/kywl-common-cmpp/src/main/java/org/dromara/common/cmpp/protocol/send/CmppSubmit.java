package org.dromara.common.cmpp.protocol.send;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.dromara.common.cmpp.config.CmppProperties;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;
import org.dromara.common.cmpp.util.CmppLongMessageUtil;
import org.dromara.common.cmpp.util.SequenceId;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CMPP_SUBMIT（支持单发 + 群发 + 长短信）
 */
public class CmppSubmit {

    /**
     * 单个手机号发送（自动处理长短信）
     */
    public static List<ByteBuf> buildListSingle(CmppProperties props,
                                                String destTerminal,
                                                String content,
                                                boolean needReport) throws Exception {

        List<byte[]> segments = CmppLongMessageUtil.splitUcs2WithUdh(content);
        List<ByteBuf> packets = new ArrayList<>(segments.size());

        int pkTotal = segments.size();

        for (int i = 0; i < pkTotal; i++) {
            byte[] segBytes = segments.get(i);
            boolean isLong = (pkTotal > 1);
            int pkNumber = i + 1;

            int seq = SequenceId.next();
            ByteBuf body = buildBody(props,
                    List.of(destTerminal),
                    segBytes,
                    needReport,
                    isLong,
                    pkTotal,
                    pkNumber);

            ByteBuf packet = CmppHandler.handle(CommandIdConstant.CMPP_SUBMIT, body, seq);
            packets.add(packet);
        }

        return packets;
    }

    /**
     * 多个手机号群发（仅当 needReport=false 时使用）
     *
     * @param props        CMPP 配置
     * @param destTerminals 多个手机号
     * @param content      短信内容
     * @param needReport   是否需要状态报告（true 时会自动按单发处理）
     */
    public static List<ByteBuf> buildListBatch(CmppProperties props,
                                               List<String> destTerminals,
                                               String content,
                                               boolean needReport) throws Exception {

        // 如果要状态报告，必须逐条发
        if (needReport || destTerminals == null || destTerminals.size() <= 1) {
            List<ByteBuf> all = new ArrayList<>();
            if (destTerminals != null) {
                for (String phone : destTerminals) {
                    all.addAll(buildListSingle(props, phone, content, needReport));
                }
            }
            return all;
        }

        // 不需要状态报告，才按协议允许群发
        List<byte[]> segments = CmppLongMessageUtil.splitUcs2WithUdh(content);
        List<ByteBuf> packets = new ArrayList<>(segments.size());

        int pkTotal = segments.size();

        for (int i = 0; i < pkTotal; i++) {
            byte[] segBytes = segments.get(i);
            boolean isLong = (pkTotal > 1);
            int pkNumber = i + 1;

            int seq = SequenceId.next();
            ByteBuf body = buildBody(props,
                    destTerminals,
                    segBytes,
                    false,   // 群发时不要求状态报告
                    isLong,
                    pkTotal,
                    pkNumber);

            ByteBuf packet = CmppHandler.handle(CommandIdConstant.CMPP_SUBMIT, body, seq);
            packets.add(packet);
        }

        return packets;
    }

    /**
     * 构建 SUBMIT 消息体
     *
     * @param destTerminals 目标手机号列表
     * @param segBytes      已编码 / 已含 UDH 的内容
     * @param isLong        是否为长短信的一段
     * @param pkTotal       总分段数
     * @param pkNumber      当前分段序号
     */
    private static ByteBuf buildBody(CmppProperties props,
                                     List<String> destTerminals,
                                     byte[] segBytes,
                                     boolean needReport,
                                     boolean isLong,
                                     int pkTotal,
                                     int pkNumber) {

        ByteBuf body = Unpooled.buffer();

        // Msg_Id 8 字节：SP 侧填 0，由 ISMG 在 SUBMIT_RESP 中生成真正 Msg_Id
        body.writeLong(0L);

        // Pk_total & Pk_number
        body.writeByte(isLong ? pkTotal : 1);
        body.writeByte(isLong ? pkNumber : 1);

        // Registered_Delivery
        body.writeByte(needReport ? 1 : 0);

        // Msg_level：默认 1
        body.writeByte(1);

        // Service_Id：10 Octet String
        body.writeBytes(StrUtil.padAfter(props.getServiceId(), 10, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // Fee_UserType：2 表示对 SP 计费
        body.writeByte(2);

        // Fee_terminal_Id：21，对 SP 计费时可空
        body.writeBytes(StrUtil.padAfter("", 21, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // TP_pId：0 普通短信
        body.writeByte(0);

        // TP_udhi：长短信段时为 1，否则 0
        body.writeByte(isLong ? 1 : 0);

        // Msg_Fmt：8 = UCS2
        body.writeByte(8);

        // Msg_src：SP_Id
        body.writeBytes(StrUtil.padAfter(props.getSpId(), 6, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // FeeType
        body.writeBytes(StrUtil.padAfter(props.getFeeType(), 2, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // FeeCode
        body.writeBytes(StrUtil.padAfter(props.getFeeCode(), 6, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // Valid_Time
        body.writeBytes(StrUtil.padAfter("", 17, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // At_Time
        body.writeBytes(StrUtil.padAfter("", 17, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // Src_Id：接入号
        body.writeBytes(StrUtil.padAfter(props.getSrcId(), 21, '\0')
                .getBytes(StandardCharsets.ISO_8859_1));

        // DestUsr_tl：接收用户数量
        int userCount = destTerminals == null ? 0 : destTerminals.size();
        body.writeByte(userCount);

        // Dest_terminal_Id：21 * DestUsr_tl
        if (destTerminals != null) {
            for (String phone : destTerminals) {
                body.writeBytes(StrUtil.padAfter(phone, 21, '\0')
                        .getBytes(StandardCharsets.ISO_8859_1));
            }
        }

        // Msg_Length
        body.writeByte(segBytes.length);

        // Msg_Content
        body.writeBytes(segBytes);

        // Reserve：8 字节保留
        body.writeBytes(new byte[8]);

        return body;
    }
}
