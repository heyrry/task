package com.herry.task.lock;

/**
 * @author herry
 * @since 2025/03/20
 */
public interface LockProvider {

    /**
     * 加锁
     *
     * @return 结果
     */
    default boolean tryLock() {
        return true;
    }

    /**
     * 解锁
     */
    default void unlock() {

    }
}
