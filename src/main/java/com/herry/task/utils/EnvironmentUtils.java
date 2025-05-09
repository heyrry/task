package com.herry.task.utils;

import com.google.common.collect.Lists;
import com.herry.task.enums.DiamondUnitEnvEnum;
import com.herry.task.enums.EnvKeyEnum;
import com.herry.task.enums.EnvSignEnum;
import com.herry.task.enums.EnvironmentEnum;
import com.herry.task.enums.UnitEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author herry
 * @since 2025/03/22
 */
public class EnvironmentUtils {

    private static final EnvKeyEnum ENV_KEY;

    private static final EnvironmentEnum ENVIRONMENT;

    private static final Map<EnvKeyEnum, EnvironmentEnum> ENV_KEY_MAP;

    private static final List<DiamondUnitEnvEnum> DAILY_ENV_LIST;

    private static final List<DiamondUnitEnvEnum> PRE_ENV_LIST;

    private static final List<DiamondUnitEnvEnum> PROD_ENV_LIST;

    static {
        DAILY_ENV_LIST = Lists.newArrayList(DiamondUnitEnvEnum.DAILY);
        PRE_ENV_LIST = Lists.newArrayList(DiamondUnitEnvEnum.PRE, DiamondUnitEnvEnum.US_EAST_PRE, DiamondUnitEnvEnum.SG_VPC_PRE);
        PROD_ENV_LIST = Lists.newArrayList(DiamondUnitEnvEnum.CENTER, DiamondUnitEnvEnum.US_EAST, DiamondUnitEnvEnum.SG_VPC);

        Map<EnvKeyEnum, EnvironmentEnum> map = new HashMap<>();
        for (EnvironmentEnum type : EnvironmentEnum.values()) {
            map.put(type.getEnvKey(), type);
        }
        ENV_KEY_MAP = Collections.unmodifiableMap(map);

        ENV_KEY = EnvKeyEnum.getEnum(System.getProperty("env.key"));
        ENVIRONMENT = ENV_KEY_MAP.get(ENV_KEY);
    }

    public static EnvironmentEnum getEnvironment() {
        return ENVIRONMENT;
    }

    public static EnvKeyEnum getEnvKey() {
        return Optional.ofNullable(EnvironmentUtils.getEnvironment()).map(EnvironmentEnum::getEnvKey).orElse(null);
    }

    public static EnvSignEnum getEnvSign() {
        return Optional.ofNullable(EnvironmentUtils.getEnvironment()).map(EnvironmentEnum::getEnvSign).orElse(null);
    }

    public static UnitEnum getUnit() {
        return Optional.ofNullable(EnvironmentUtils.getEnvironment()).map(EnvironmentEnum::getUnit).orElse(null);
    }

    public static EnvironmentEnum getEnvironment(EnvKeyEnum envKey) {
        return ENV_KEY_MAP.get(envKey);
    }

    public static List<DiamondUnitEnvEnum> getDiamondEnvList() {
        return getDiamondEnvList(getEnvironment().getEnvSign());
    }

    public static List<DiamondUnitEnvEnum> getDiamondEnvList(EnvSignEnum envSign) {
        if (EnvSignEnum.PROD.equals(envSign)) {
            return PROD_ENV_LIST;
        } else if (EnvSignEnum.PRE.equals(envSign)) {
            return PRE_ENV_LIST;
        } else {
            return DAILY_ENV_LIST;
        }
    }
}
