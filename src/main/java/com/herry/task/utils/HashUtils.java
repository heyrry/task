package com.herry.task.utils;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author herry
 * @since 2025/3/20
 */
@SuppressWarnings("ALL")
public class HashUtils {

    private static final Logger logger = LoggerFactory.getLogger(HashUtils.class);

    /**
     * 方案和报价中使用的hash算法
     *
     * @param value 待hash的值
     * @return hashCode
     */
    public static Long hash(String value) {
        if (StringUtils.isBlank(value)) {
            return 0L;
        }
        return Hashing.farmHashFingerprint64().hashString(value, StandardCharsets.UTF_8).asLong();
    }


}
