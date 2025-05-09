package com.herry.task.repository.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author herry
 * @since 2025/03/30
 */
@Data
public class TaskResult {

    /**
     * 批量任务结果数据建议：
     * context: 任务上下文，为每一个DTO分配一个Id
     * {
     * List<DTO> inputList;
     * }
     * TaskResult.data
     * {
     * List<List<Long>>  successList; 记录每一次执行成功的记录
     * List<Long>  failedList; 记录失败的记录
     * }
     * 结果数据：重试会覆盖之前的结果,业务自己每一次结果带有上一次结果的信息
     */
    @Length(max = 8192)
    private String data;

    /**
     * 结果信息
     */
    @Length(max = 512)
    private String message;

    public TaskResult() {
    }

    public TaskResult(String data, String message) {
        this.data = data;
        this.message = message;
    }
}
