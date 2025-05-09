package com.herry.task.utils;

import com.alibaba.fastjson.JSON;
import com.herry.task.enums.ResultCodeEnum;
import com.herry.task.repository.convert.Converters;
import com.herry.task.repository.dto.PageQuery;
import com.herry.task.repository.dto.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author herry
 * @since 2025/03/19
 */
public class Pages {

    /**
     * 分页最大循环次数
     */
    private static final Integer MAX_LOOP_TIMES = 5000;

    private static final Logger logger = LoggerFactory.getLogger(Pages.class);

    /**
     * 分页查询
     */
    public static <Q extends PageQuery, T, R> PageResult<R> queryList(Q query, Function<Q, Integer> totalFun, Function<Q, List<T>> dataFun, Function<T, R> converterFun) {
        if (null == query) {
            return PageResult.getFailurePageResult("450", "Missing Parameters");
        }
        try {
            Integer total = totalFun.apply(query);
            List<T> dataList = total > 0 ? dataFun.apply(query) : Collections.emptyList();
            List<R> resultList = Converters.convertList(dataList, converterFun);
            return PageResult.getSuccessPageResult(resultList, total, query.getCurrentPage(), query.getPageSize());
        } catch (Exception e) {
            logger.error("queryList error, query {}", JSON.toJSONString(query), e);
            return PageResult.getFailurePageResult(ResultCodeEnum.SYSTEM_ERROR.getCode(), ResultCodeEnum.SYSTEM_ERROR.getMessage());
        }
    }

    public static <Q extends PageQuery, T> void loopExecute(String taskName, Q query, Function<Q, List<T>> dataFun, Consumer<List<T>> consumer) {
        loopExecute(taskName, query, dataFun, consumer, true);
    }

    public static <Q extends PageQuery, T> void loopExecute(String taskName, Q query, Function<Q, List<T>> dataFun, Consumer<List<T>> consumer, boolean withLog) {
        int loops = 0;
        int totalCount = 0;
        long start = System.currentTimeMillis();
        try {
            while (true) {
                if (++loops > MAX_LOOP_TIMES) {
                    logger.error("loopExecute exceed maximum loop times, taskName: {}, loops: {}, totalCount: {}, costTime: {}, query: {}",
                        taskName, loops, totalCount, System.currentTimeMillis() - start, JSON.toJSONString(query));
                    break;
                }
                List<T> result = dataFun.apply(query);
                if (CollectionUtils.isEmpty(result)) {
                    break;
                }
                totalCount += result.size();
                consumer.accept(result);
                if (result.size() < query.getPageSize()) {
                    break;
                }
                query.setCurrentPage(query.getCurrentPage() + 1);
            }
        } finally {
            if(withLog){
                logger.info("loopExecute end, taskName: {}, loops: {}, totalCount: {}, costTime: {}, query: {}",
                    taskName, loops, totalCount, System.currentTimeMillis() - start, JSON.toJSONString(query));
            }
        }
    }

    /**
     * 结果转换
     */
    public static <T, R> PageResult<R> convert(PageResult<T> from, Function<T, R> converterFun) {
        if (null == from) {
            return PageResult.getFailurePageResult(ResultCodeEnum.SYSTEM_ERROR.getCode(), ResultCodeEnum.SYSTEM_ERROR.getMessage());
        }
        if (from.isSuccess()) {
            List<R> dataList = Optional.ofNullable(from.getData()).orElse(Collections.emptyList()).stream().map(converterFun).collect(Collectors.toList());
            return PageResult.getSuccessPageResult(dataList, from.getTotal(), from.getCurrentPage(), from.getPageSize());
        } else {
            return PageResult.getFailurePageResult(from.getCode(), from.getMessage());
        }
    }

    /**
     * 遍历补充数据
     */
    public static <T> void forEach(PageResult<T> result, Consumer<T> consumer) {
        Optional.ofNullable(result).map(PageResult::getData).orElse(Collections.emptyList()).stream().filter(Objects::nonNull).forEach(consumer);
    }

    /**
     * 拷贝分页参数
     */
    public static void copy(PageQuery source, PageQuery target) {
        if (null == source || null == target) {
            return;
        }
        target.setCurrentPage(source.getCurrentPage());
        target.setPageSize(source.getPageSize());
        target.setTotalPage(source.getTotalPage());
        target.setOrderBy(source.getOrderBy());
        target.setSortDirection(source.getSortDirection());
    }

    /**
     * 空
     */
    public static <T> PageResult<T> empty(PageQuery query) {
        return empty(query, ResultCodeEnum.SUCCESS);
    }

    /**
     * 空
     */
    public static <T> PageResult<T> empty(PageQuery query, ResultCodeEnum resultCode) {
        PageResult<T> result = new PageResult<>();
        result.setSuccess(ResultCodeEnum.SUCCESS.equals(resultCode));
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setTotal(0);
        result.setTotalPage(0);
        result.setData(Collections.emptyList());
        result.setPageSize(query.getPageSize());
        result.setCurrentPage(query.getCurrentPage());
        return result;
    }
}
