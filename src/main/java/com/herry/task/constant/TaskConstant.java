package com.herry.task.constant;

/**
 * @author herry
 * @since 2025/03/31
 */
public interface TaskConstant {

    /**
     * 任务调度锁
     */
    String TASK_SCHEDULE_LOCK_KEY_JOB_ID_PREFIX = "TASK_SCHEDULE_JOB_ID";

    /**
     * 构造Key的分隔符
     */
    String KEY_SEPARATOR = "|";

    /**
     * 依赖任务与依赖任务之间的分隔符
     */
    String DEPENDENCY_TO_DEPENDENCY_SEPARATOR = "#";

    /**
     * 任务与依赖任务之间的分隔符
     */
    String TASK_TO_DEPENDENCY_SEPARATOR = "~";

    /**
     * 任务与任务之间的分隔符
     */
    String TASK_TO_TASK_SEPARATOR = "@";
}
