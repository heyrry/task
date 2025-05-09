package com.herry.task.utils;

import com.herry.task.exception.BizException;
import com.herry.task.lock.LockProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author herry
 * @since 2025/03/20
 */
public class RetryUtils {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);

    /**
     * 执行任务
     *
     * @param taskName   任务名称
     * @param task       任务
     * @param maxRetries [0-5]
     */
    public static <T> T executeWithRetry(String taskName, Callable<T> task, int maxRetries) {
        return executeWithRetryAndLock(taskName, task, maxRetries, new LockProvider() {});
    }

    public static <T> T executeWithRetryAndLock(String taskName, Callable<T> task, int maxRetries, LockProvider lockProvider) {
        if (maxRetries < 0 || maxRetries > 5) {
            throw new IllegalArgumentException("maxRetries must be between 0 and 5");
        }
        boolean retry = maxRetries > 0;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            boolean locked;
            try {
                locked = lockProvider.tryLock();
                if (locked) {
                    T result = task.call();
                    if (Boolean.FALSE.equals(result)) {
                        throw new RuntimeException("the task return false, taskName: " + taskName);
                    }
                    return result;
                } else {
                    throw new RuntimeException(taskName + " lock failed");
                }
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("{}, task execution failed, retryTimes: {}, ", taskName, attempt, e);
                }
                if (attempt < maxRetries) {
                    try {
                        long sleepMillis = RandomUtils.getSleepMillis(attempt, maxRetries);
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException ie) {
                        logger.error("{}, task execution error, retryTimes: {}", taskName, attempt, ie);
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("the thread was interrupted during sleep, task: " + taskName, ie);
                    }
                } else {
                    if (!retry) {
                        //不重试场景，抛出异常，业务自己捕货异常
                        logger.error("{}, task execution error, retryTimes: {}", taskName, attempt, e);
                        if (e instanceof BizException) {
                            throw (BizException)e;
                        } else {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // 重试场景，很重要，不抛异常，业务根据返回值判断是否做告警
                        return null;
                    }
                }
            } finally {
                lockProvider.unlock();
            }
        }
        return null;
    }
}
