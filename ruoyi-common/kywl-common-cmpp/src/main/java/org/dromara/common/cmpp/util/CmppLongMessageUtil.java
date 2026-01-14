package org.dromara.common.cmpp.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CMPP 长短信分片工具（UCS2 + UDH）
 */
public class CmppLongMessageUtil {

    // 每条短信 TP-UD 总长度 140 字节
    private static final int TP_UD_MAX_BYTES = 140;
    // UDH 头长度：05 00 03 XX MM NN 共 6 字节
    private static final int UDH_BYTES_LEN = 6;
    // 每段可用的内容字节数
    private static final int SEGMENT_CONTENT_MAX_BYTES = TP_UD_MAX_BYTES - UDH_BYTES_LEN;

    private static final Random RANDOM = new Random();

    /**
     * 按 UCS2 编码将文本拆分为多段，每段带好 UDH 头
     *
     * @param content 文本内容
     * @return 每段已经包含 UDH 的字节数组列表（每段直接作为 Msg_Content）
     */
    public static List<byte[]> splitUcs2WithUdh(String content) throws UnsupportedEncodingException {
        byte[] ucs2Bytes = content.getBytes("UTF-16BE"); // UCS2 = UTF-16BE

        // 如果不超长，直接返回（但仍然返回 List，只含一段，不带 UDH）
        if (ucs2Bytes.length <= TP_UD_MAX_BYTES) {
            List<byte[]> only = new ArrayList<>(1);
            only.add(ucs2Bytes);
            return only;
        }

        // 超长的情况：按 134 字节一段拆
        int totalSegments = (ucs2Bytes.length + SEGMENT_CONTENT_MAX_BYTES - 1) / SEGMENT_CONTENT_MAX_BYTES;

        // 1 字节的参考号（0-255）
        int ref = RANDOM.nextInt(256);

        List<byte[]> result = new ArrayList<>(totalSegments);

        for (int i = 0; i < totalSegments; i++) {
            int start = i * SEGMENT_CONTENT_MAX_BYTES;
            int end = Math.min(start + SEGMENT_CONTENT_MAX_BYTES, ucs2Bytes.length);
            int len = end - start;

            byte[] segment = new byte[UDH_BYTES_LEN + len];

            // UDH:
            // [0] 0x05 = UDHL
            // [1] 0x00 = 信息元素标识(IEI) 00 表示“长短信”
            // [2] 0x03 = IEDL，后面有 3 字节
            // [3] ref  = 参考号
            // [4] totalSegments = 总段数
            // [5] (i+1) = 当前段号
            segment[0] = 0x05;
            segment[1] = 0x00;
            segment[2] = 0x03;
            segment[3] = (byte) (ref & 0xFF);
            segment[4] = (byte) (totalSegments & 0xFF);
            segment[5] = (byte) ((i + 1) & 0xFF);

            // 复制实际内容
            System.arraycopy(ucs2Bytes, start, segment, UDH_BYTES_LEN, len);

            result.add(segment);
        }

        return result;
    }
}
