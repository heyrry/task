package com.herry.task.enums;

import lombok.Getter;

/**
 * @author herry
 * @since 2025/03/22
 */
@Getter
public enum EnvironmentEnum {

    /**
     * 日常
     */
    DAILY(EnvKeyEnum.DAILY, DiamondUnitEnvEnum.DAILY, EnvSignEnum.DAILY, UnitEnum.CN),

    /**
     * 中国预发
     */
    CN_PRE(EnvKeyEnum.CN_PRE, DiamondUnitEnvEnum.PRE, EnvSignEnum.PRE, UnitEnum.CN),

    /**
     * 美东预发
     */
    USEAST_PRE(EnvKeyEnum.USEAST_PRE, DiamondUnitEnvEnum.US_EAST_PRE, EnvSignEnum.PRE, UnitEnum.US),

    /**
     * 新加坡预发
     */
    SG_PRE(EnvKeyEnum.SG_PRE, DiamondUnitEnvEnum.SG_VPC_PRE, EnvSignEnum.PRE, UnitEnum.SG),

    /**
     * 德国预发
     */
    DE_PRE(EnvKeyEnum.DE_PRE, DiamondUnitEnvEnum.DE_3_PRE, EnvSignEnum.PRE, UnitEnum.DE),

    /**
     * 中国线上
     */
    CN(EnvKeyEnum.CN, DiamondUnitEnvEnum.CENTER, EnvSignEnum.PROD, UnitEnum.CN),

    /**
     * 美东线上
     */
    USEAST(EnvKeyEnum.USEAST, DiamondUnitEnvEnum.US_EAST, EnvSignEnum.PROD, UnitEnum.US),

    /**
     * 新加坡线上
     */
    SG(EnvKeyEnum.SG, DiamondUnitEnvEnum.SG_VPC, EnvSignEnum.PROD, UnitEnum.SG),

    /**
     * 德国线上
     */
    DE(EnvKeyEnum.DE, DiamondUnitEnvEnum.DE_3, EnvSignEnum.PROD, UnitEnum.DE);

    /**
     * 环境标识: 自定义，配置在Aone环境特性，Java系统属性，自定义系统顺序性
     */
    private final EnvKeyEnum envKey;

    /**
     * Diamond单元标：Diamond官方定义，<a href="https://aliyuque.antfin.com/softloadblance/wpnllg/vm2s80hl6v8yqa8b">...</a>
     */
    private final DiamondUnitEnvEnum diamondUnitEnv;

    /**
     * 环境级别：Aone官方，配置在Aone环境，环境配置，基础设置，环境级别(envSign)
     */
    private final EnvSignEnum envSign;

    /**
     * 单元: 自定义
     */
    private final UnitEnum unit;

    EnvironmentEnum(EnvKeyEnum envKey, DiamondUnitEnvEnum diamondUnitEnv, EnvSignEnum envSign, UnitEnum unit) {
        this.envKey = envKey;
        this.diamondUnitEnv = diamondUnitEnv;
        this.envSign = envSign;
        this.unit = unit;
    }
}
