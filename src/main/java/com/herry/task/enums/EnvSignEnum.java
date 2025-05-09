package com.herry.task.enums;

import lombok.Getter;

/**
 * <a href="https://aliyuque.antfin.com/aone/ke0u5e/zp987u5k9dgdr2lq">...</a>
 *
 * @author herry
 * @since 2025/03/20
 */
@Getter
public enum EnvSignEnum {

    /**
     * 日常环境
     */
    DAILY("testing", "日常环境"),

    PRE("staging", "预发环境"),

    PROD("production", "生产环境");

    /**
     * 环境级别：Aone官方，配置在Aone环境，环境配置，基础设置，环境级别(envSign)
     */
    private final String value;

    /**
     * 描述
     */
    private final String description;

    EnvSignEnum(String envSign, String description) {
        this.value = envSign;
        this.description = description;
    }
}
