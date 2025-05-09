package com.herry.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * @author herry
 * @since 2025/03/31
 */
@Data
@ToString(callSuper = true)
public class TaskRetryReqDTO implements Serializable {

    private static final long serialVersionUID = -602036098026960405L;

    /**
     * 作业Id
     */
    @NotNull
    private Set<Long> jobIds;
}
