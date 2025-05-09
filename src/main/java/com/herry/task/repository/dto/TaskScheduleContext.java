package com.herry.task.repository.dto;

import lombok.Data;

/**
 * @author herry
 * @since 2025/03/31
 */
@Data
public class TaskScheduleContext {

    /**
     * 最大重试次数
     */
    private Integer maxRetryTimes;

    /**
     * expectExecuteTime >= now() - minExpectExecuteTimeDay
     */
    private Integer minExpectExecuteTimeDay;

    /**
     * expectExecuteTime <= now() + maxExpectExecuteTimeSeconds
     */
    private Integer maxExpectExecuteTimeSeconds;

    public TaskScheduleContext() {
        this.maxRetryTimes = 3;
        this.minExpectExecuteTimeDay = 7;
        this.maxExpectExecuteTimeSeconds = 10;
    }
}
