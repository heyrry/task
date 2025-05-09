package com.herry.task.lock;

import java.util.concurrent.Callable;

/**
 * @author herry
 * @since 2025/03/22
 */
public interface LockManager {

    /**
     * 加锁
     *
     * @param resource    资源
     * @param expiredTime 失效时间(单位秒)
     * @return 加锁结果
     */
    boolean tryLock(String resource, long waitTime, long expiredTime);

    /**
     * 解锁
     *
     * @param resource 资源
     * @return 解锁结果
     */
    boolean releaseLock(String resource);

    /**
     * 加锁执行
     *
     * @param taskName 任务名称
     * @param task     任务
     * @param lockKey  加锁Key
     * @return 结果
     */
    <T> T executeWithLock(String taskName, Callable<T> task, String lockKey);

    /**
     * 加锁执行
     *
     * @param taskName    任务名称
     * @param task        任务
     * @param lockKey     加锁Key
     * @param expiredTime 锁的过期时间（单位：秒）
     * @return 结果
     */
    <T> T executeWithLock(String taskName, Callable<T> task, String lockKey, int expiredTime);

    /**
     * 加锁并重试
     *
     * @param taskName   任务名称
     * @param task       任务
     * @param maxRetries 重试次数[1,5]
     * @param lockKey    加锁Key
     * @return 结果
     */
    <T> T executeWithRetryAndLock(String taskName, Callable<T> task, int maxRetries, String lockKey);

    /**
     * 加锁并重试
     *
     * @param taskName    任务名称
     * @param task        任务
     * @param maxRetries  重试次数[1,5]
     * @param lockKey     加锁Key
     * @param waitTime 获取锁时间（单位：秒）
     * @param expiredTime 锁的过期时间（单位：秒）
     * @return 结果
     */
    <T> T executeWithRetryAndLock(String taskName, Callable<T> task, int maxRetries, String lockKey, int waitTime, int expiredTime);
}
