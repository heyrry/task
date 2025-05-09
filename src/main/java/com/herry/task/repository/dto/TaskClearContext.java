package com.herry.task.repository.dto;

import lombok.Data;

/**
 * @author herry
 * @since 2025/03/31
 */
@Data
public class TaskClearContext {

    /**
     * expectExecuteTime <= now() - maxExpectExecuteTimeDay
     */
    private Integer maxExpectExecuteTimeDay;

    public TaskClearContext() {
        this.maxExpectExecuteTimeDay = 365;
    }
}
