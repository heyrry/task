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
public enum TaskBizTypeEnum {

    /**
     * DEMO_TASK_LISTENER
     */
    DEMO_TASK_LISTENER("DEMO_TASK_LISTENER");

    private static final Map<String, TaskBizTypeEnum> NAME_MAP;

    static {
        Map<String, TaskBizTypeEnum> map = new HashMap<>();
        for (TaskBizTypeEnum enumObj : values()) {
            map.put(enumObj.name(), enumObj);
        }
        NAME_MAP = Collections.unmodifiableMap(map);
    }

    private final String description;

    TaskBizTypeEnum(String description) {
        this.description = description;
    }

    public static TaskBizTypeEnum getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return NAME_MAP.get(name);
    }
}
