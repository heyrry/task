package com.herry.task.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author herry
 * @since 2025/03/29
 */
@Getter
public enum TaskStatusEnum {

    /**
     * 任务新建或者无前置依赖
     */
    READY("就绪"),

    /**
     * 正在运行
     */
    RUNNING("运行"),

    /**
     * 任务有前置依赖
     */
    WAITING("阻塞"),

    /**
     * 执行失败, 可能会重试
     */
    FAILED("失败"),

    /**
     * 执行终止，不会重试
     */
    TERMINATED("终止"),

    /**
     * 执行成功
     */
    SUCCESS("成功"),
    ;

    private static final Map<String, TaskStatusEnum> NAME_MAP;

    static {
        Map<String, TaskStatusEnum> map = new HashMap<>();
        for (TaskStatusEnum enumObj : values()) {
            map.put(enumObj.name(), enumObj);
        }
        NAME_MAP = Collections.unmodifiableMap(map);
    }

    private final String description;

    TaskStatusEnum(String description) {
        this.description = description;
    }

    public static TaskStatusEnum getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return NAME_MAP.get(name);
    }
}
