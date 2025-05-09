package com.herry.task.repository.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author baofeng
 * @since 2025/5/7
 */
@Data
public class PageQuery implements Serializable {
    private int currentPage = 1;

    private int pageSize = 10;

    private int totalPage;

    private String orderBy;

    private String sortDirection = "asc";
}
