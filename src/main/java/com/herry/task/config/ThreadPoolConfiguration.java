package com.herry.task.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.herry.task.pool.MdcThreadPoolTaskExecutor;
import com.herry.task.pool.ThreadPoolExecutorName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author herry
 * @since 2025/03/27
 */
@Configuration
public class ThreadPoolConfiguration {

    @Bean(name = ThreadPoolExecutorName.TASK_EXECUTOR)
    public MdcThreadPoolTaskExecutor taskExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("task-schedule-thread-pool-%d").setDaemon(true).setPriority(Thread.NORM_PRIORITY).build();
        return new MdcThreadPoolTaskExecutor(10, 20, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(500), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean(name = ThreadPoolExecutorName.ASYNC_TASK)
    public MdcThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("async-task-thread-pool-%d").setDaemon(true).setPriority(Thread.NORM_PRIORITY).build();
        return new MdcThreadPoolTaskExecutor(10, 20, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(500), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean(name = ThreadPoolExecutorName.CLEAR_TASK)
    public MdcThreadPoolTaskExecutor clearTaskExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("clear-task-thread-pool-%d").setDaemon(true).setPriority(Thread.NORM_PRIORITY).build();
        return new MdcThreadPoolTaskExecutor(5, 8, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(500), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
