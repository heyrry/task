package com.herry.task.enums;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author herry
 * @since 2025/03/19
 */
@Getter
@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum ResultCodeEnum {
    //成功
    SUCCESS("200", "请求成功"),

    //3xx 不会做国际化翻译
    // 前端(或接口API)输入参数缺失或错误
    PARAM_ERROR("300", "${message}"),
    // 第三方服务返回的错误信息
    CUSTOMIZE_MESSAGE_ERROR("301", "${message}"),

    //4xx 通用校验错误
    //前端(或接口API)输入参数缺失或错误
    PARAM_MAY_NOT_NULL("400", "请求参数缺失或为空，请检查后重试"),
    // 前端(或接口API)输入参数缺失操作人信息
    OPERATOR_MAY_NOT_NULL("401", "操作人信息缺失或为空，请补充后重试"),
    // 非法访问, 水平越权
    ILLEGAL_ACCESS_ERROR("402", "非法访问"),
    // 权限不足
    INSUFFICIENT_PERMISSION_ERROR("403", "权限不足"),

    //5xx 通用内部错误
    SENTINEL_ERROR("500", "系统繁忙，请稍后重试"),
    LOCK_ERROR("501", "系统繁忙，请稍后重试"),
    OPEN_SEARCH_QUERY_ERROR("502", "系统错误, 请稍后重试"),
    ODPS_ERROR("503", "系统错误，请稍后重试"),

    //999 系统错误
    //系统内部发生错误，请联系客服
    SYSTEM_ERROR("999", "系统内部错误，请联系客服或稍后重试"),
    /*-------------------------------以上为系统错误码, 不要在以上增加内容-------------------------------------------*/

    /*--------------------------------第三方业务系统： 一个模块分配1000个错误码，一旦定义，不能更改-----------------------*/
    //调用会员系统，获取不到会员信息
    ACCOUNT_NOT_FOUND("1000", "会员账号不存在"),
    //调用商品系统，更新商品信息失败
    PRODUCT_SYNC_ERROR("1001", "商品侧同步失败"),

    /*--------------------------------清关货品模块： 一个模块分配1000个错误码，一旦定义，不能更改---------------------*/
    PRODUCT_ID_MESSING_ERROR("2001", "商品Id不能为空"),
    PRODUCT_ID_EXIST_ERROR("2002", "不允许带商品Id"),
    US_HS_CODE_NOT_FOUND_ERROR("2003", "没有美国清关HS编码"),
    CN_HS_CODE_NOT_FOUND_ERROR("2004", "没有中国出口HS编码"),
    US_HS_CODE_LENGTH_NOT_VALID_ERROR("2005", "美国清关HS编码长度只能为8位或10位"),
    CN_HS_CODE_LENGTH_NOT_VALID_ERROR("2006", "中国出口HS编码只能为8位或10位"),
    CN_US_HS_CODE_PREFIX_NOT_MATCH_ERROR("2007", "美国清关HS编码和中国出口HS编码前4位不匹配"),
    CN_HS_CODE_CONTAINS_NO_NUMBER_ERROR("2008", "中国出口HS编码不能包含非数字"),
    US_HS_CODE_CONTAINS_NO_NUMBER_ERROR("2009", "美国清关HS编码不能包含非数字"),
    GOODS_UPDATE_STATUS_NOT_INVALID("2010", "非草稿状态不能编辑"),
    CUSTOMS_GOODS_NOT_EXIST("2011", "清关货品不存在"),
    PRODUCT_GOODS_NOT_EXIST("2012", "货品不存在，请刷新后重试"),
    DUPLICATE_GOODS_ERROR("2013", "货品重复"),
    PRODUCT_BIND_GOODS_STATUS_NOT_INVALID("2014", "货品状态非法，不能绑定"),
    PRODUCT_ID_NOT_MATCH_GOODS_ID("2015", "商品ID非法"),
    PRODUCT_HAVE_BIND_OTHER_CUSTOMS_GOODS("2016", "商品已绑定到其他货品，请先与其他货品解绑"),
    ALI_ID_NOT_MATCH("2020", "用户ID非法"),
    GOODS_HAVE_BIND_PRODUCT("2030", "货品已绑定商品，无法删除"),

    PRODUCT_NOT_EXIST("2040", "商品不存在"),
    PRODUCT_HS_CODE_NOT_MATCH("2041", "商品hsCode和传入的hsCode不匹配"),
    PRODUCT_HAVE_BIND_GOODS("2042", "商品已绑定货品"),
    UNSUPPORTED_INIT_TYPE("2050", "暂不支持的初始化类型"),

    /*--------------------------------任务模块： 一个模块分配1000个错误码，一旦定义，不能更改---------------------*/
    TASK_REGISTER_PARAM_ERROR("3001", "注册任务参数错误"),
    TASK_REGISTER_CYCLE_DEPENDENCY_ERROR("3002", "注册任务存在循环依赖:${message}"),
    TASK_REGISTER_COUNT_ERROR("30003", "注册任务数量超过限制:${message}"),

    ;
    private static final Map<String, ResultCodeEnum> NAME_MAP;

    static {
        Map<String, ResultCodeEnum> map = new HashMap<>();
        for (ResultCodeEnum enumObj : values()) {
            map.put(enumObj.name(), enumObj);
        }
        NAME_MAP = Collections.unmodifiableMap(map);
    }

    private final String code;

    private final String message;

    ResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultCodeEnum getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return NAME_MAP.get(name);
    }
}
