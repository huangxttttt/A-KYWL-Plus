package org.dromara.common.cmpp.protocol.send;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.dromara.common.cmpp.constant.CommandIdConstant;
import org.dromara.common.cmpp.protocol.CmppHandler;

/**
 * CMPP_QUERY
 * 查询某天的短信发送统计（总数 / 按业务类型）
 *
 * 协议字段：
 * Time       8  Octet String  YYYYMMDD
 * Query_Type 1  Unsigned Int  0：总数查询 1：按业务类型查询
 * Query_Code 10 Octet String  当 Query_Type=1 时填 Service_Id，其他情况可空
 * Reserve    8  Octet String  保留
 */
public class CmppQuery {

    /**
     * 构造 CMPP_QUERY 包
     *
     * @param date       查询日期，格式：YYYYMMDD，例如 "20251210"
     * @param queryType  0：总数查询；1：按业务类型查询
     * @param queryCode  当 queryType=1 时，填业务类型 Service_Id；否则可传 null / ""
     * @param sequenceId 消息流水号 Sequence_Id
     */
    public static ByteBuf build(String date,
                                int queryType,
                                String queryCode,
                                int sequenceId) {

        ByteBuf body = Unpooled.buffer();

        // Time: 8 字节 YYYYMMDD，右边补空格
        body.writeBytes(StrUtil.padAfter(date, 8, ' ').getBytes());

        // Query_Type: 1 字节
        body.writeByte(queryType);

        // Query_Code: 10 字节
        String qc = (queryType == 1) ? (queryCode == null ? "" : queryCode) : "";
        body.writeBytes(StrUtil.padAfter(qc, 10, ' ').getBytes());

        // Reserve: 8 字节，保留
        body.writeBytes(StrUtil.padAfter("", 8, ' ').getBytes());

        // 加上公共头
        return CmppHandler.handle(CommandIdConstant.CMPP_QUERY, body, sequenceId);
    }
}
