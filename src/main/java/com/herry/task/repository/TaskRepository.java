package com.herry.task.repository;


import com.herry.task.entity.TaskEntity;
import com.herry.task.repository.dto.TaskQuery;

import java.util.List;

/**
 * @author herry
 * @since 2025/03/29
 */
public interface TaskRepository {

    /**
     * 存储任务列表
     *
     * @param tasks 任务列表
     */
    void saveTaskList(List<TaskEntity> tasks);

    /**
     * 根据任务Id查询任务
     *
     * @param taskId 任务Id
     * @return 任务
     */
    TaskEntity findByTaskId(Long taskId);

    /**
     * 根据作业Id列表查询任务列表
     *
     * @param jobIds 作业Id列表
     * @return 任务列表
     */
    List<TaskEntity> findByJobIds(List<Long> jobIds);

    /**
     * 根据作业Id列表查询任务列表
     *
     * @param jobId 作业Id
     * @return 任务列表
     */
    List<TaskEntity> findByJobId(Long jobId);

    /**
     * 专用方法：使用前自行确认是否满足需求。根据Id更新，用于调度。
     * 如果传入retryTimes，意味着重试，重试次数加一, 如果不传retryTimes，意味着更新，重试次数不变
     *
     * @param task 任务
     * @return 更新结果
     */
    boolean updateTask(TaskEntity task);

    /**
     * 专用方法：使用前自行确认是否满足需求。查询jobIdList，用于判断重复注册。
     *
     * @param queryDTO 查询条件
     * @return 任务列表
     */
    List<Long> findByJobKey(TaskQuery queryDTO);

    /**
     * 专用方法：使用前自行确认是否满足需求。查询job_biz_type, job_id, expect_execute_time，用于扫描，调度或者清理。
     *
     * @param queryDTO 查询条件
     * @return 分页列表
     */
    List<TaskEntity> scanJobList(TaskQuery queryDTO);

    /**
     * 专用方法：使用前自行确认是否满足需求。查询基础字段，用于清理
     *
     * @param queryDTO 查询条件
     * @return 基础字段
     */
    List<TaskEntity> queryList(TaskQuery queryDTO);
}
