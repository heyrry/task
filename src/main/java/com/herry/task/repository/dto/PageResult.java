package com.herry.task.repository.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author baofeng
 * @since 2025/5/7
 */
public class PageResult<T> extends Result<List<T>> {
    private int total;
    /** @deprecated */
    @Deprecated
    private int totalCount;
    private int pageSize = 10;
    private int totalPage;
    private int currentPage;

    public PageResult() {
    }

    /** @deprecated */
    @Deprecated
    public int getTotalCount() {
        return this.total;
    }

    /** @deprecated */
    @Deprecated
    public void setTotalCount(int totalCount) {
        this.total = totalCount;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public static <T> PageResult<T> getSuccessPageResult(List<T> records, int totalCount, int currentPage, int pageSize) {
        PageResult<T> result = new PageResult();
        result.setSuccess(true);
        result.setData(records);
        result.setTotalCount(totalCount);
        result.setTotal(totalCount);
        result.setCurrentPage(currentPage);
        result.setCode("200");
        if (pageSize <= 0) {
            pageSize = 10;
        }

        result.setPageSize(pageSize);
        result.setTotalPage(countTotalPage(totalCount, pageSize));
        return result;
    }

    public static <T> PageResult<T> getFailurePageResult(String code, String message) {
        PageResult<T> result = new PageResult();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    private static int countTotalPage(int total, int pageSize) {
        int totalPage = total / pageSize;
        if (total % pageSize > 0) {
            ++totalPage;
        }

        return totalPage;
    }

    public String toString() {
        return "PageResult{total=" + this.total + ", pageSize=" + this.pageSize + ", totalPage=" + this.totalPage + ", currentPage=" + this.currentPage + "} " + super.toString();
    }

    public interface Function<P, R> {
        R apply(P var1);
    }
}
