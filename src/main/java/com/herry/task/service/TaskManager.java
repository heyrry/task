package com.herry.task.service;


import com.herry.task.enums.JobBizTypeEnum;
import com.herry.task.repository.dto.Task;
import com.herry.task.repository.dto.TaskClearContext;
import com.herry.task.repository.dto.TaskScheduleContext;

import java.util.List;

/**
 * @author herry
 * @since 2025/03/29
 */
public interface TaskManager {

    /**
     * 注册任务：一个 job 对应多个 task, 用于低频写任务、离线任务等，使用前请与@懿名联系
     *
     * @param jobBizType 作业业务类型
     * @param jobBizKey  作业Key
     * @param taskList   任务
     * @return 作业Id
     */
    Long register(JobBizTypeEnum jobBizType, String jobBizKey, List<Task> taskList);

    /**
     * 调度任务：任务扫描，需要满足：单元、状态、重试次数条件、时间条件、前驱任务条件等
     *
     * @param context 调度上下文
     * @return 结果
     */
    boolean scheduleTask(TaskScheduleContext context);

    /**
     * 清理任务
     *
     * @param context 清理上下文
     * @return 结果
     */
    boolean clearTask(TaskClearContext context);

    /**
     * 重试任务：需要满足：单元、前驱任务条件等
     *
     * @param jobId 作业Id
     * @return 调度结果
     */
    boolean retryTask(Long jobId);

    /**
     * 删除任务
     *
     * @param jobId 作业Id
     * @return 结果
     */
    boolean deleteTask(Long jobId);
}
