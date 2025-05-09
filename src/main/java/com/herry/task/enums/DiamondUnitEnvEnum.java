package com.herry.task.enums;

import lombok.Getter;

/**
 * @author herry
 * @since 2025/03/22
 */
@Getter
public enum DiamondUnitEnvEnum {

    /**
     * 日常
     */
    DAILY("daily", "日常"),

    PRE("pre", "预发"),

    US_EAST_PRE("rg-us-east-pre", "美东预发"),

    SG_VPC_PRE("aliyun-region-vpc-ap-southeast-1-pre", "新加坡预发"),

    DE_3_PRE("rg-de-3-pre", "德国预发"),

    CENTER("sh", "中心"),

    US_EAST("rg-us-east", "美东"),

    SG_VPC("aliyun-region-vpc-ap-southeast-1", "新加坡"),

    DE_3("rg-de-3", "德国");

    /**
     * Diamond单元标：Diamond官方定义，<a href="https://aliyuque.antfin.com/softloadblance/wpnllg/vm2s80hl6v8yqa8b">...</a>
     */
    private final String value;
    /**
     * 描述
     */
    private final String description;

    DiamondUnitEnvEnum(String unitEnv, String description) {
        this.value = unitEnv;
        this.description = description;
    }
}
