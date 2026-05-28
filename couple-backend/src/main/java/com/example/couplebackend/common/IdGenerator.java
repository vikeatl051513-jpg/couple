package com.example.couplebackend.common;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {
    private static final long EPOCH = Instant.parse("2026-01-01T00:00:00Z").toEpochMilli();
    private static final long WORKER_ID = 1L;
    private static final long WORKER_SHIFT = 12L;
    private static final long TIME_SHIFT = 22L;
    private static final long SEQUENCE_MASK = 4095L;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public synchronized long nextId() {
        long now = System.currentTimeMillis();
        if (now < lastTimestamp) {
            now = lastTimestamp;
        }
        if (now == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                now = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = now;
        return ((now - EPOCH) << TIME_SHIFT) | (WORKER_ID << WORKER_SHIFT) | sequence;
    }

    private long waitNextMillis(long timestamp) {
        long now = System.currentTimeMillis();
        while (now <= timestamp) {
            now = System.currentTimeMillis();
        }
        return now;
    }
}
