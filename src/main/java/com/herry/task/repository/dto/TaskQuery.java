package com.herry.task.repository.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author herry
 * @since 2025/03/30
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskQuery extends PageQuery {

    /**
     * 单元
     */
    private String unit;

    /**
     * 作业业务类型
     */
    private String jobBizType;

    /**
     * 作业Key
     */
    private String jobBizKey;

    /**
     * 状态
     */
    private String status;

    /**
     * 预期执行时间  minExpectExecuteTime <= expect_execute_time
     */
    private Date minExpectExecuteTime;

    /**
     * 预期执行时间 expect_execute_time <= maxExpectExecuteTime
     */
    private Date maxExpectExecuteTime;

    /**
     * 最大重试次数 retry_times <= maxRetryTimes
     */
    private Integer maxRetryTimes;

    /**
     * 哈希索引
     */
    private Long hashIndex;
}
