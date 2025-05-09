package com.herry.task.mapper;

import com.herry.task.repository.dataobject.TaskDO;
import com.herry.task.repository.dataobject.TaskWithBLOBs;
import com.herry.task.repository.dto.TaskQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author herry
 * @since 2025/03/30
 */
@Mapper
public interface TaskMapper {

    /**
     * 批量插入
     *
     * @param recordList 记录列表
     * @return 插入记录数
     */
    int batchInsert(@Param("recordList") List<TaskDO> recordList);

    /**
     * 根据Id查询任务
     *
     * @param taskId 任务Id
     * @return 任务
     */
    TaskWithBLOBs findById(@Param("taskId") Long taskId);

    /**
     * 根据作业Id查询任务
     *
     * @param jobId 作业Id
     * @return 任务
     */
    List<TaskWithBLOBs> findByJobId(@Param("jobId") Long jobId);

    /**
     * 根据作业Id查询任务列表
     *
     * @param jobIds 作业Id列表
     * @return 任务列表
     */
    List<TaskDO> findByJobIds(@Param("jobIds") List<Long> jobIds);

    /**
     * 专用方法：使用前自行确认是否满足需求。根据Id更新，用于调度。
     *
     * @param dataObject 数据对象
     * @return 更新条数
     */
    int updateById(TaskWithBLOBs dataObject);

    /**
     * 专用方法：使用前自行确认是否满足需求。查询jobIdList，用于判断重复注册。
     *
     * @param queryDTO 查询条件
     * @return 只有jobId字段
     */
    List<TaskDO> findByJobKey(TaskQuery queryDTO);

    /**
     * 专用方法：使用前自行确认是否满足需求。查询job_biz_type, job_id, expect_execute_time，用于扫描、调度
     *
     * @param queryDTO 查询条件
     * @return job_biz_type, job_id, expect_execute_time
     */
    List<TaskDO> scanJobList(TaskQuery queryDTO);

    /**
     * 专用方法：使用前自行确认是否满足需求。查询基础字段，用于清理
     *
     * @param queryDTO 查询条件
     * @return 基础字段
     */
    List<TaskDO> queryList(TaskQuery queryDTO);
}
