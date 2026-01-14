package org.dromara.common.cmpp.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SequenceId {
    private static final AtomicLong SEQ = new AtomicLong(0);

    public static int next() {
        long v = SEQ.incrementAndGet();
        // CMPP Sequence_Id 必须落在 0 ~ 0xFFFFFFFF (UInt32)
        long seq = v & 0xFFFFFFFFL;
        return (int) seq;
    }
}
