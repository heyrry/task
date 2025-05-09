package com.herry.task.utils;

/**
 * @author herry
 * @since 2025/5/7
 */
public class UniqueIdGenerator {
    private static long lastTimestamp = 0;
    private static int sequence = 0;

    /**
     * 生成12位唯一ID
     * 注意，只能保证单机环境下全局唯一
     * @return 12位唯一ID
     */
    public static synchronized long generate12DigitId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence++;
        } else {
            sequence = 0;
            lastTimestamp = timestamp;
        }

        // 时间戳后8位 + 4位序列号
        long id = (timestamp % 100000000) * 10000 + sequence;

        // 确保是12位数
        if (id < 100000000000L) {
            id += 100000000000L;
        }

        return id;
    }
}
