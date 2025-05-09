package com.herry.task.repository.convert;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.herry.task.entity.TaskEntity;
import com.herry.task.enums.JobBizTypeEnum;
import com.herry.task.enums.TaskBizTypeEnum;
import com.herry.task.enums.TaskStatusEnum;
import com.herry.task.enums.UnitEnum;
import com.herry.task.repository.dataobject.TaskDO;
import com.herry.task.repository.dataobject.TaskWithBLOBs;
import com.herry.task.repository.dto.Task;
import com.herry.task.repository.dto.TaskResult;
import io.micrometer.common.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

/**
 * @author herry
 * @since 2025/03/30
 */
public class TaskConvertor {

    public static TaskEntity do2Entity(TaskDO dataObject) {
        if (null == dataObject) {
            return null;
        }

        TaskEntity entity = new TaskEntity();
        entity.setId(dataObject.getId());
        entity.setUnit(UnitEnum.getByValue(dataObject.getUnit()));
        entity.setJobId(dataObject.getJobId());
        entity.setStatus(TaskStatusEnum.getByName(dataObject.getStatus()));
        entity.setJobBizType(JobBizTypeEnum.getByName(dataObject.getJobBizType()));
        entity.setJobBizKey(dataObject.getJobBizKey());
        entity.setTaskBizType(TaskBizTypeEnum.getByName(dataObject.getTaskBizType()));
        entity.setTaskBizKey(dataObject.getTaskBizKey());
        entity.setExpectExecuteTime(dataObject.getExpectExecuteTime());
        entity.setContext(dataObject.getContext());
        if (StringUtils.isNotBlank(dataObject.getResult())) {
            entity.setResult(JSON.parseObject(dataObject.getResult(), TaskResult.class));
        }
        entity.setRetryTimes(dataObject.getRetryTimes());
        entity.setHashIndex(dataObject.getHashIndex());
        entity.setVersion(dataObject.getVersion());
        if (StringUtils.isBlank(dataObject.getPreTaskIds())) {
            entity.setPreTaskIds(Collections.emptySet());
        } else {
            entity.setPreTaskIds(Sets.newHashSet(JSON.parseArray(dataObject.getPreTaskIds(), Long.class)));
        }
        if (StringUtils.isBlank(dataObject.getNextTaskIds())) {
            entity.setNextTaskIds(Collections.emptySet());
        } else {
            entity.setNextTaskIds(Sets.newHashSet(JSON.parseArray(dataObject.getNextTaskIds(), Long.class)));
        }
        entity.setTaskName(String.format("%s-%s:%s-%s:%s-%d", dataObject.getUnit(), dataObject.getJobBizType(),
            dataObject.getJobBizKey(), dataObject.getTaskBizType(), dataObject.getTaskBizKey(), dataObject.getVersion()));
        return entity;
    }

    public static TaskEntity doWithBlob2Entity(TaskWithBLOBs dataObject) {
        if (null == dataObject) {
            return null;
        }
        TaskEntity entity = do2Entity(dataObject);
        if (StringUtils.isNotBlank(dataObject.getResult())) {
            entity.setResult(JSON.parseObject(dataObject.getResult(), TaskResult.class));
        }
        return entity;
    }

    public static TaskWithBLOBs entity2DO(TaskEntity entity) {
        if (null == entity) {
            return null;
        }
        TaskWithBLOBs dataObject = new TaskWithBLOBs();
        dataObject.setId(entity.getId());
        dataObject.setDeleted(entity.getDeleted());
        dataObject.setUnit(Optional.ofNullable(entity.getUnit()).map(UnitEnum::getValue).orElse(null));
        dataObject.setJobId(entity.getJobId());
        dataObject.setStatus(Optional.ofNullable(entity.getStatus()).map(TaskStatusEnum::name).orElse(null));
        dataObject.setJobBizType(Optional.ofNullable(entity.getJobBizType()).map(JobBizTypeEnum::name).orElse(null));
        dataObject.setJobBizKey(entity.getJobBizKey());
        dataObject.setTaskBizType(Optional.ofNullable(entity.getTaskBizType()).map(TaskBizTypeEnum::name).orElse(null));
        dataObject.setTaskBizKey(entity.getTaskBizKey());
        dataObject.setExpectExecuteTime(entity.getExpectExecuteTime());
        dataObject.setContext(entity.getContext());
        if (null != entity.getResult()) {
            dataObject.setResult(JSON.toJSONString(entity.getResult()));
        }
        dataObject.setRetryTimes(entity.getRetryTimes());
        dataObject.setHashIndex(entity.getHashIndex());
        dataObject.setVersion(entity.getVersion());
        if (null != entity.getPreTaskIds()) {
            dataObject.setPreTaskIds(JSON.toJSONString(entity.getPreTaskIds()));
        }
        if (null != entity.getNextTaskIds()) {
            dataObject.setNextTaskIds(JSON.toJSONString(entity.getNextTaskIds()));
        }
        return dataObject;
    }

    public static TaskEntity copyEntity(TaskEntity entity) {
        TaskEntity copy = new TaskEntity();
        copy.setId(entity.getId());
        copy.setDeleted(entity.getDeleted());
        copy.setUnit(entity.getUnit());
        copy.setJobId(entity.getJobId());
        copy.setStatus(entity.getStatus());
        copy.setJobBizType(entity.getJobBizType());
        copy.setJobBizKey(entity.getJobBizKey());
        copy.setTaskBizType(entity.getTaskBizType());
        copy.setTaskBizKey(entity.getTaskBizKey());
        copy.setExpectExecuteTime(entity.getExpectExecuteTime());
        copy.setContext(entity.getContext());
        copy.setResult(entity.getResult());
        copy.setRetryTimes(entity.getRetryTimes());
        copy.setPreTaskIds(entity.getPreTaskIds());
        copy.setNextTaskIds(entity.getNextTaskIds());
        copy.setHashIndex(entity.getHashIndex());
        copy.setVersion(entity.getVersion());
        copy.setTaskName(entity.getTaskName());
        return entity;
    }

    public static TaskEntity convert2TaskEntity(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.setUnit(task.getUnit());
        entity.setTaskBizType(task.getTaskBizType());
        entity.setTaskBizKey(task.getTaskBizKey());
        entity.setContext(task.getContext());
        entity.setExpectExecuteTime(task.getExpectExecuteTime());
        entity.setPreTaskIds(new HashSet<>());
        entity.setNextTaskIds(new HashSet<>());
        return entity;
    }
}
