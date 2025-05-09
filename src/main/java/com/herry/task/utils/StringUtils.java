package com.herry.task.utils;

import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author herry
 * @since 2025/03/24
 */
public class StringUtils {

    private static final Pattern SIMPLE_METHOD_PARAM_PATTERN = Pattern.compile("\\((.*?)\\)");

    private static final Pattern SIMPLE_METHOD_NAME_PATTERN = Pattern.compile("\\w+\\.\\w+\\((?:\\w*)[^\\)]*\\)");

    /**
     * @param methodName public com.alibaba.icbu.scm.api.common.utils.StringUtils.getApiName(java.lang.String)
     * @return StringUtils.getApiName(String)
     */
    public static String getApiName(String methodName) {
        if (null == methodName) {
            return null;
        }
        Matcher matcher = SIMPLE_METHOD_NAME_PATTERN.matcher(methodName);
        if (matcher.find()) {
            //StringUtils.getApiName(java.lang.String)
            String output = matcher.group();
            //StringUtils.getApiName(java.lang.String)
            output = SIMPLE_METHOD_PARAM_PATTERN.matcher(output).replaceAll(matchResult -> {
                String[] types = matchResult.group(1).split(",");
                StringJoiner joiner = new StringJoiner(",");
                for (String type : types) {
                    joiner.add(type.substring(type.lastIndexOf(".") + 1));
                }
                return "(" + joiner + ")";
            }).replaceAll("\b(\\w+\\.)*?(\\w+)\b", "$2");
            return output;
        } else {
            return methodName;
        }
    }

    /**
     * 字符串格式化
     *
     * @param template  模板：下单成功，操作人：${operatorName}
     * @param variables 变量：{"operatorName":"张三"}
     * @return 字符串格式化结果：下单成功，操作人：张三
     */
    public static String format(String template, Map<String, String> variables) {
        if (org.apache.commons.lang3.StringUtils.isBlank(template) || !template.contains("$")) {
            return template;
        }

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String regex = "\\$\\{" + entry.getKey() + "}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(template);
            template = matcher.replaceAll(entry.getValue());
        }
        return template;
    }
}
