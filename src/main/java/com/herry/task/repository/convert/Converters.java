package com.herry.task.repository.convert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author herry
 * @since 2025/03/21
 */
public class Converters {

    /**
     * List转换
     */
    public static <T, R> List<R> convertList(List<T> list, Function<T, R> convertFun) {
        if (null == list || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(convertFun).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
