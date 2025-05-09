package com.herry.task.service;


import com.herry.task.entity.TaskEntity;
import com.herry.task.enums.TaskBizTypeEnum;

/**
 * @author herry
 * @since 2025/03/29
 */
public interface TaskListener {

    /**
     * 任务业务类型
     *
     * @return 任务业务类型
     */
    TaskBizTypeEnum taskBizType();

    /**
     * 调度任务
     *
     * @param task 任务，不要更改任务信息
     * @return 结果
     */
    boolean onSchedule(TaskEntity task);
}
