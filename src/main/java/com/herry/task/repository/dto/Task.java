package com.herry.task.repository.dto;

import com.google.common.collect.Sets;
import com.herry.task.enums.TaskBizTypeEnum;
import com.herry.task.enums.UnitEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * @author herry
 * @since 2025/03/29
 */
@Data
public class Task {

    /**
     * 单元，当前只支持中国
     */
    @NotNull
    private UnitEnum unit;

    /**
     * 业务类型
     */
    @NotNull
    private TaskBizTypeEnum taskBizType;

    /**
     * 业务Id: unit + taskBizType + taskBizKey 唯一。unit + taskBizType + taskBizKey相同，其他参数必须要相同
     */
    @NotBlank
    private String taskBizKey;

    /**
     * 上下文：建议用json对象，不要用json数组(扩展性差)
     */
    @Length(max = 8192)
    private String context;

    /**
     * 预期执行时间
     */
    private Date expectExecuteTime;

    /**
     * 子任务
     */
    private Set<Task> nextTasks;

    public Task() {
    }

    public Task(TaskBizTypeEnum bizType, String bizId) {
        this.taskBizType = bizType;
        this.taskBizKey = bizId;
        this.nextTasks = Sets.newHashSet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof Task task)) {return false;}
        return unit == task.unit && taskBizType == task.taskBizType && Objects.equals(taskBizKey, task.taskBizKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, taskBizType, taskBizKey);
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
            "unit=" + unit +
            ", bizType=" + taskBizType +
            ", bizKey='" + taskBizKey + '\'' +
            ", context='" + context + '\'' +
            ", expectExecuteTime=" + expectExecuteTime +
            '}';
    }
}
