package com.herry.task.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.herry.task.dto.TaskRetryReqDTO;
import com.herry.task.enums.JobBizTypeEnum;
import com.herry.task.enums.ResultCodeEnum;
import com.herry.task.enums.TaskBizTypeEnum;
import com.herry.task.enums.UnitEnum;
import com.herry.task.repository.dto.Result;
import com.herry.task.repository.dto.Task;
import com.herry.task.repository.dto.TaskScheduleContext;
import com.herry.task.service.TaskManager;
import com.herry.task.utils.BizPreconditions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author herry
 * @since 2025/5/7
 */
@RestController
@RequestMapping("/taskController")
@Slf4j
public class TaskController {

    @Resource
    private TaskManager taskManager;


    @PostMapping("register")
    public Result<Boolean> register(@RequestBody Map<String, Object> bodyContent) {
        // 通过任务异步发送
        String uniqueKey = String.valueOf(System.currentTimeMillis());

        Task taskDTO = new Task();
        taskDTO.setUnit(UnitEnum.CN);
        taskDTO.setTaskBizType(TaskBizTypeEnum.DEMO_TASK_LISTENER);
        taskDTO.setTaskBizKey(uniqueKey);

        taskDTO.setContext(JSONObject.toJSONString(bodyContent));

        log.info("registerTask begin, uniqueKey:{}, taskDTO: {}", uniqueKey, JSON.toJSONString(taskDTO));
        Long jobId = taskManager.register(JobBizTypeEnum.PRODUCT_GOODS_EVENT_JOB, uniqueKey, Lists.newArrayList(taskDTO));
        log.info("registerTask success, uniqueKey:{}, jobId: {}", uniqueKey, jobId);
        BizPreconditions.checkArgument(null != jobId && jobId > 0, ResultCodeEnum.SYSTEM_ERROR);

        return Result.getSuccessResult(true);
    }

    @GetMapping("/schedule")
    public Result<Boolean> schedule() {
        log.info("executeTask task begin");
        TaskScheduleContext context = new TaskScheduleContext();
        taskManager.scheduleTask(context);

        return Result.getSuccessResult(true);
    }

    @PostMapping("/executeTask")
    public Result<Map<Long,Boolean>> executeTask(@RequestBody TaskRetryReqDTO reqDTO) {
        log.info("retry task, reqDTO: {}", JSON.toJSONString(reqDTO));
        Set<Long> jobIds = reqDTO.getJobIds();
        Map<Long, Boolean> resultMap = Maps.newHashMap();
        for (Long jobId : jobIds) {
            boolean result = taskManager.retryTask(jobId);
            resultMap.put(jobId, BooleanUtils.isTrue(result));
        }
        return Result.getSuccessResult(resultMap);
    }

}


