package com.herry.task.lock;

import com.herry.task.utils.RetryUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author herry
 * @since 2025/03/22
 */
@Component
public class DistributeLockManager implements LockManager {

    private static final Logger logger = LoggerFactory.getLogger(DistributeLockManager.class);

    /**
     * 默认等待锁时间 100s
     */
    private static final Integer DEFAULT_WAIT_TIME = 100;

    /**
     * 默认自动释放锁时间 900s
     */
    private static final Integer DEFAULT_EXPIRED_TIME = 900;

    private final RedissonClient redissonClient;

    public DistributeLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取可重入锁
     * @param lockKey 锁名称
     * @param waitTime 等待时间（秒）
     * @param leaseTime 锁持有时间（秒）
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("tryLock error", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     * @param lockKey 锁名称
     */
    @Override
    public boolean releaseLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
                return true;
            } catch (Exception e) {
                logger.error("releaseLock error", e);
                return false;
            }
        }
        return false;
    }

    @Override
    public <T> T executeWithLock(String taskName, Callable<T> task, String lockKey) {
        return executeWithRetryAndLock(taskName, task, 0, lockKey);
    }

    @Override
    public <T> T executeWithLock(String taskName, Callable<T> task, String lockKey, int expiredTime) {
        return executeWithRetryAndLock(taskName, task, 0, lockKey, DEFAULT_WAIT_TIME, expiredTime);
    }

    @Override
    public <T> T executeWithRetryAndLock(String taskName, Callable<T> task, int maxRetries, String lockKey) {
        return executeWithRetryAndLock(taskName, task, maxRetries, lockKey, DEFAULT_WAIT_TIME, DEFAULT_EXPIRED_TIME);
    }

    @Override
    public <T> T executeWithRetryAndLock(String taskName, Callable<T> task, int maxRetries, String lockKey, int waitTime, int expiredTime) {
        return RetryUtils.executeWithRetryAndLock(taskName, task, maxRetries, new LockProvider() {
            @Override
            public boolean tryLock() {
                return DistributeLockManager.this.tryLock(lockKey, waitTime, expiredTime);
            }

            @Override
            public void unlock() {
                DistributeLockManager.this.releaseLock(lockKey);
            }
        });
    }
}
