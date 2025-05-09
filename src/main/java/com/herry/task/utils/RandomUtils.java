package com.herry.task.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author herry
 * @since 2025/03/20
 */
public class RandomUtils {

    /**
     * @param attempt    初始值：0
     * @param maxRetries 最大值：5
     * @return 毫秒
     */
    public static long getSleepMillis(int attempt, int maxRetries) {
        long sleepMillis = 0;
        if (attempt == maxRetries - 1) {
            //最后一次：1000ms
            sleepMillis = 1000L;
        } else {
            // 0        1      2       3       4        5
            //200ms   400ms   600ms   800ms   1000ms   1200ms
            sleepMillis = (attempt + 1) * 200L;
        }

        //增加随机数
        int random = RandomUtils.generateRandomInt(10, 100);

        return sleepMillis + random;
    }

    public static int generateRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }
}
