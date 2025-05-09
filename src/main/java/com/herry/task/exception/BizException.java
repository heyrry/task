package com.herry.task.exception;

import com.google.common.collect.Maps;
import com.herry.task.enums.ResultCodeEnum;
import com.herry.task.utils.StringUtils;
import lombok.Getter;

import java.util.Map;

/**
 * @author herry
 * @since 2025/03/19
 */
@Getter
public class BizException extends RuntimeException {

    /**
     * 编码
     */
    private final String code;

    /**
     * 信息
     */
    private final String message;

    /**
     * 国际化变量
     */
    private Map<String, Object> variables;

    private BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    private BizException(String code, String message, Map<String, Object> variables) {
        super(message);
        this.code = code;
        this.message = message;
        this.variables = variables;
    }

    public BizException(ResultCodeEnum resultCode) {
        this(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 除参数校验场景，不建议使用
     */
    public BizException(String message) {
        this(ResultCodeEnum.PARAM_ERROR.getCode(), message, null);
    }

    public static BizException of(ResultCodeEnum resultCode, Map<String, Object> variables) {
        if (null == variables || variables.isEmpty()) {
            return new BizException(resultCode.getCode(), resultCode.getMessage(), variables);
        } else {
            return new BizException(resultCode.getCode(), StringUtils.format(resultCode.getMessage(), Maps.transformValues(variables, String::valueOf)), variables);
        }
    }
}
