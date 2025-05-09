package com.herry.task.utils;

import com.herry.task.enums.ResultCodeEnum;
import com.herry.task.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author herry
 * @since 2025/03/19
 */
@Slf4j
public class BizPreconditions {

    /**
     * 检查表达式为真
     *
     * @param expression 表达式
     * @param resultCode 结果码
     */
    public static void checkArgument(boolean expression, ResultCodeEnum resultCode) {
        if (!expression) {
            throw new BizException(resultCode);
        }
    }

    /**
     * 检查参数是不为空
     *
     * @param reference  参数
     * @param resultCode 结果码
     * @param <T>        参数类型
     */
    public static <T> void checkNotNull(T reference, ResultCodeEnum resultCode) {
        if (reference == null) {
            throw new BizException(resultCode);
        }
    }

    /**
     * 检查参数是不为空
     *
     * @param reference  参数
     * @param resultCode 结果码
     */
    public static void checkNotBlank(String reference, ResultCodeEnum resultCode) {
        if (StringUtils.isBlank(reference)) {
            throw new BizException(resultCode);
        }
    }

    /**
     * 检查表达式为真
     *
     * @param expression 表达式
     * @param resultCode 结果码
     * @param logger     打印日志
     */
    public static void checkArgument(boolean expression, ResultCodeEnum resultCode, Runnable logger) {
        if (!expression) {
            logger.run();
            throw new BizException(resultCode);
        }
    }

    /**
     * 检查参数是不为空
     *
     * @param reference  参数
     * @param resultCode 结果码
     * @param logger     打印日志
     * @param <T>        参数类型
     */
    public static <T> void checkNotNull(T reference, ResultCodeEnum resultCode, Runnable logger) {
        if (reference == null) {
            logger.run();
            throw new BizException(resultCode);
        }
    }

    /**
     * 检查参数是不为空
     *
     * @param reference  参数
     * @param resultCode 结果码
     * @param logger     打印日志
     */
    public static void checkNotBlank(String reference, ResultCodeEnum resultCode, Runnable logger) {
        if (StringUtils.isBlank(reference)) {
            logger.run();
            throw new BizException(resultCode);
        }
    }
}
