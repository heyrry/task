package com.herry.task.entity;

import com.herry.task.enums.JobBizTypeEnum;
import com.herry.task.enums.TaskBizTypeEnum;
import com.herry.task.enums.TaskStatusEnum;
import com.herry.task.enums.UnitEnum;
import com.herry.task.repository.dto.TaskResult;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;
import java.util.Set;

/**
 * @author herry
 * @since 2025/03/29
 */
@Data
public class TaskEntity {

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 删除
     */
    @NotNull
    private Long deleted;

    /**
     * 单元
     */
    @NotNull
    private UnitEnum unit;

    /**
     * 作业Id
     */
    @NotNull
    private Long jobId;

    /**
     * 状态
     */
    @NotNull
    private TaskStatusEnum status;

    /**
     * 作业业务类型
     */
    @NotNull
    private JobBizTypeEnum jobBizType;

    /**
     * 作业Key
     */
    @NotBlank
    private String jobBizKey;

    /**
     * 任务业务类型
     */
    @NotNull
    private TaskBizTypeEnum taskBizType;

    /**
     * 任务业务key
     */
    @NotBlank
    private String taskBizKey;

    /**
     * 预期执行时间
     */
    @NotNull
    private Date expectExecuteTime;

    /**
     * 上下文
     */
    @Length(max = 8192)
    private String context;

    /**
     * 结果
     */
    private TaskResult result;

    /**
     * 重试次数
     */
    @NotNull
    private Integer retryTimes;

    /**
     * 父任务
     */
    @NotNull
    private Set<Long> preTaskIds;

    /**
     * 子任务
     */
    @NotNull
    private Set<Long> nextTaskIds;

    /**
     * 哈希索引
     */
    @NotNull
    private Long hashIndex;

    /**
     * 乐观锁
     */
    @NotNull
    private Long version;

    /**
     * 任务名称
     */
    private String taskName;
}
