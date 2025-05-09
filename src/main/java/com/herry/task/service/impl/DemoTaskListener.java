package com.herry.task.service.impl;

import com.alibaba.fastjson.JSON;
import com.herry.task.entity.TaskEntity;
import com.herry.task.enums.TaskBizTypeEnum;
import com.herry.task.repository.dto.TaskResult;
import com.herry.task.service.TaskListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author herry
 * @since 2025/04/12
 */
@Slf4j
@Component
public class DemoTaskListener implements TaskListener {

    @Override
    public TaskBizTypeEnum taskBizType() {
        return TaskBizTypeEnum.DEMO_TASK_LISTENER;
    }

    @Override
    public boolean onSchedule(TaskEntity task) {
        log.info("schedule begin, jobBizKey: {}, task:{}", task.getJobBizKey(), JSON.toJSONString(task));

        // TODO 获取任务内容

        return true;
    }


}
