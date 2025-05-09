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
public enum JobBizTypeEnum {

    /**
     * 越短越好：不能超过32
     */
    PRODUCT_GOODS_EVENT_JOB("货品事件作业");

    private static final Map<String, JobBizTypeEnum> NAME_MAP;

    static {
        Map<String, JobBizTypeEnum> map = new HashMap<>();
        for (JobBizTypeEnum enumObj : values()) {
            map.put(enumObj.name(), enumObj);
        }
        NAME_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * 描述
     */
    private final String description;

    JobBizTypeEnum(String description) {
        this.description = description;
    }

    public static JobBizTypeEnum getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return NAME_MAP.get(name);
    }
}
