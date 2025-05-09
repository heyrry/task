package com.herry.task.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author herry
 * @since 2025/03/20
 */
@Getter
public enum UnitEnum {

    /**
     * 中国
     */
    CN("cn", "中国"),

    US("us", "美东"),

    SG("sg", "新加坡"),

    DE("de", "德国");

    private static final Map<String, UnitEnum> VALUE_MAP;

    static {
        Map<String, UnitEnum> map = new HashMap<>();
        for (UnitEnum enumObj : values()) {
            map.put(enumObj.getValue(), enumObj);
        }
        VALUE_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * 单元: 自定义
     */
    private final String value;
    /**
     * 描述
     */
    private final String description;

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    UnitEnum(String unit, String description) {
        this.value = unit;
        this.description = description;
    }

    public static UnitEnum getByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return VALUE_MAP.get(value);
    }
}
