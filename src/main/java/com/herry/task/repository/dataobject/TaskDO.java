package com.herry.task.repository.dataobject;


import lombok.Data;

import java.util.Date;

/**
 * @author herry
 * @since 2025/03/30
 */
@Data
public class TaskDO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 删除
     */
    private Long deleted;

    /**
     * 单元
     */
    private String unit;

    /**
     * 作业Id
     */
    private Long jobId;

    /**
     * 状态
     */
    private String status;

    /**
     * 作业业务类型
     */
    private String jobBizType;

    /**
     * 作业Key
     */
    private String jobBizKey;

    /**
     * 任务业务类型
     */
    private String taskBizType;

    /**
     * 任务业务key
     */
    private String taskBizKey;

    /**
     * 预期执行时间
     */
    private Date expectExecuteTime;

    /**
     * 上下文
     */
    private String context;

    /**
     * 结果
     */
    private String result;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 父任务
     */
    private String preTaskIds;

    /**
     * 子任务
     */
    private String nextTaskIds;

    /**
     * 哈希索引
     */
    private Long hashIndex;

    /**
     * 乐观锁
     */
    private Long version;

    public Long getJobId() {
        return jobId;
    }
}
