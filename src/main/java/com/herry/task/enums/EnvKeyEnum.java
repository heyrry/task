package com.herry.task.enums;

import lombok.Getter;

/**
 * @author herry
 * @since 2025/03/20
 */
@Getter
public enum EnvKeyEnum {

    /**
     * 日常
     */
    DAILY("daily", "日常"),

    CN_PRE("cn-pre", "中国预发"),

    USEAST_PRE("useast-pre", "美东预发"),

    SG_PRE("sg-pre", "新加坡预发"),

    DE_PRE("de-pre", "德国预发"),

    CN("cn-prod", "中国线上"),

    USEAST("useast-prod", "美东线上"),

    SG("sg-prod", "新加坡线上"),

    DE("de-prod", "德国线上");

    /**
     * 环境标识: 自定义，配置在Aone环境特性，Java系统属性，自定义系统顺序性
     */
    private final String value;

    /**
     * 描述
     */
    private final String description;

    EnvKeyEnum(String key, String description) {
        this.value = key;
        this.description = description;
    }

    public static EnvKeyEnum getEnum(String envKey) {
        for (EnvKeyEnum keyEnum : EnvKeyEnum.values()) {
            if (keyEnum.getValue().equals(envKey)) {
                return keyEnum;
            }
        }
        return null;
    }

}
