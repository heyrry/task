package com.herry.task.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.herry.task.constant.TaskConstant;
import com.herry.task.entity.TaskEntity;
import com.herry.task.enums.JobBizTypeEnum;
import com.herry.task.enums.ResultCodeEnum;
import com.herry.task.enums.TaskBizTypeEnum;
import com.herry.task.enums.TaskStatusEnum;
import com.herry.task.enums.UnitEnum;
import com.herry.task.exception.BizException;
import com.herry.task.lock.LockManager;
import com.herry.task.pool.MdcThreadPoolTaskExecutor;
import com.herry.task.pool.ThreadPoolExecutorName;
import com.herry.task.repository.TaskRepository;
import com.herry.task.repository.convert.TaskConvertor;
import com.herry.task.repository.dto.Task;
import com.herry.task.repository.dto.TaskClearContext;
import com.herry.task.repository.dto.TaskQuery;
import com.herry.task.repository.dto.TaskResult;
import com.herry.task.repository.dto.TaskScheduleContext;
import com.herry.task.service.TaskListener;
import com.herry.task.service.TaskManager;
import com.herry.task.utils.EnvironmentUtils;
import com.herry.task.utils.HashUtils;
import com.herry.task.utils.Pages;
import com.herry.task.utils.UniqueIdGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author herry
 * @since 2025/03/29
 */
@Component
@Slf4j
public class DefaultTaskManager implements TaskManager {

    /**
     * 单次注册最大任务数量
     */
    private static final Integer MAX_TASK_COUNT = 100;

    private final Map<TaskBizTypeEnum, TaskListener> listenerMap = new ConcurrentHashMap<>(16);

    @Autowired
    private List<TaskListener> listeners;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LockManager lockManager;

    @Autowired
    @Qualifier(ThreadPoolExecutorName.TASK_EXECUTOR)
    private MdcThreadPoolTaskExecutor taskExecutor;

    @Autowired
    @Qualifier(ThreadPoolExecutorName.CLEAR_TASK)
    private MdcThreadPoolTaskExecutor clearTaskExecutor;

    @PostConstruct
    public void init() {
        if (null == listeners) {
            return;
        }
        for (TaskListener listener : listeners) {
            TaskBizTypeEnum taskBizType = listener.taskBizType();
            if (null != taskBizType) {
                listenerMap.put(taskBizType, listener);
            }
        }
    }

    @Override
    public Long register(JobBizTypeEnum jobBizType, String jobBizKey, List<Task> taskList) {
        if (null == jobBizType || StringUtils.isBlank(jobBizKey) || CollectionUtils.isEmpty(taskList)) {
            log.error("register task params error, jobBizType: {}, jobBizKey: {}", Optional.ofNullable(jobBizType).map(JobBizTypeEnum::name).orElse(null), jobBizKey);
            throw new BizException(ResultCodeEnum.TASK_REGISTER_PARAM_ERROR);
        }

        //判断是否有循环依赖
        String dependencyPath = hasCyclicDependency(taskList);
        if (StringUtils.isNotBlank(dependencyPath)) {
            log.error("register tasks cycle dependency, jobBizType: {}, jobBizKey: {}, dependency: {}", jobBizType.name(), jobBizKey, dependencyPath);
            throw BizException.of(ResultCodeEnum.TASK_REGISTER_CYCLE_DEPENDENCY_ERROR, ImmutableMap.of("message", dependencyPath));
        }

        //将依赖的任务打平处理
        List<TaskEntity> tasks = flatTaskBfs(taskList);
        if (tasks.size() > MAX_TASK_COUNT) {
            throw BizException.of(ResultCodeEnum.TASK_REGISTER_COUNT_ERROR, ImmutableMap.of("message", tasks.size()));
        }

        //补充基本信息
        Long hashIndex = setBasicInfo(jobBizType, jobBizKey, tasks);

        //判断是否重复注册
        Long existJobId = existJobId(jobBizType, jobBizKey, hashIndex, tasks);
        if (null != existJobId) {
            log.warn("register task, job already exists, jobBizType: {}, jobBizKey: {}, existJobId: {}", jobBizType.name(), jobBizKey, existJobId);
            return existJobId;
        }

        //设置Id
        Long jobId = setId(tasks);

        //存储
        taskRepository.saveTaskList(tasks);
        return jobId;
    }

    @Override
    public boolean scheduleTask(TaskScheduleContext context) {
        Date now = new Date();
        //状态是有顺序的
        List<TaskStatusEnum> statusList = Lists.newArrayList(TaskStatusEnum.FAILED, TaskStatusEnum.READY, TaskStatusEnum.WAITING);
        for (JobBizTypeEnum jobBizType : JobBizTypeEnum.values()) {
            //按类型异步
            taskExecutor.execute(() -> {
                for (TaskStatusEnum status : statusList) {
                    //按状态同步
                    TaskQuery query = buildScanQuery(context, jobBizType, status, now);
                    schedule(context, query);
                }
            });
        }
        return true;
    }

    /**
     * 构造扫描条件
     */
    private TaskQuery buildScanQuery(TaskScheduleContext context, JobBizTypeEnum jobBizType, TaskStatusEnum taskStatus, Date now) {
        TaskQuery queryDTO = new TaskQuery();
        queryDTO.setStatus(taskStatus.name());
        // TODO 待实现
        queryDTO.setUnit(EnvironmentUtils.getUnit().getValue());
        queryDTO.setJobBizType(jobBizType.name());
        queryDTO.setMinExpectExecuteTime(DateUtils.addDays(now, -context.getMinExpectExecuteTimeDay()));
        queryDTO.setMaxExpectExecuteTime(DateUtils.addSeconds(now, context.getMaxExpectExecuteTimeSeconds()));
        queryDTO.setMaxRetryTimes(context.getMaxRetryTimes());
        return queryDTO;
    }

    private void schedule(TaskScheduleContext context, TaskQuery query) {
        Pages.loopExecute("任务系统扫描任务", query, taskQuery -> taskRepository.scanJobList(taskQuery), taskList -> {
            for (TaskEntity task : taskList) {
                Long jobId = task.getJobId();
                String lockKey = Joiner.on(TaskConstant.KEY_SEPARATOR).join(Lists.newArrayList(TaskConstant.TASK_SCHEDULE_LOCK_KEY_JOB_ID_PREFIX, jobId));
                String taskName = "任务系统调度任务: " + jobId;
                int maxRetryTimes = context.getMaxRetryTimes();
                Function<TaskEntity, Boolean> shouldScheduleFun = t -> shouldSchedule(context, t);
                Callable<Boolean> callable = () -> schedule(context, jobId, shouldScheduleFun);
                lockManager.executeWithRetryAndLock(taskName, callable, maxRetryTimes, lockKey);
            }
        }, false);
    }

    @Override
    public boolean clearTask(TaskClearContext context) {
        //至少保留7天
        int day = Math.max(context.getMaxExpectExecuteTimeDay(), 7);
        List<TaskQuery> queryList = Lists.newArrayList();
        for (TaskStatusEnum taskStatus : TaskStatusEnum.values()) {
            for (JobBizTypeEnum jobBizType : JobBizTypeEnum.values()) {
                for (UnitEnum unit : UnitEnum.values()) {
                    TaskQuery query = new TaskQuery();
                    query.setStatus(taskStatus.name());
                    query.setJobBizType(jobBizType.name());
                    query.setUnit(unit.name());
                    query.setMaxExpectExecuteTime(DateUtils.addDays(new Date(), -day));
                    queryList.add(query);
                }
            }
        }
        for (TaskQuery query : queryList) {
            clearTaskExecutor.execute(() -> {
                Pages.loopExecute("任务系统清理" + day + "天前任务: " + buildQueryName(query), query, queryDTO -> taskRepository.queryList(queryDTO), taskList -> {
                    for (TaskEntity task : taskList) {
                        Long jobId = task.getJobId();
                        deleteTask(jobId);
                    }
                });
            });
        }
        return true;
    }

    @Override
    public boolean retryTask(Long jobId) {
        TaskScheduleContext context = new TaskScheduleContext();

        Function<TaskEntity, Boolean> shouldScheduleFun = task -> {
            String unitEnvValue = Optional.ofNullable(EnvironmentUtils.getUnit()).map(UnitEnum::getValue).orElse(null);
            String taskEnvValue = Optional.ofNullable(task.getUnit()).map(UnitEnum::getValue).orElse(null);
            return StringUtils.equals(unitEnvValue, taskEnvValue);
        };

        return schedule(context, jobId, shouldScheduleFun);
    }

    @Override
    public boolean deleteTask(Long jobId) {
        List<TaskEntity> tasks = taskRepository.findByJobId(jobId);
        if (CollectionUtils.isEmpty(tasks)) {
            return true;
        }

        boolean result = true;
        for (TaskEntity task : tasks) {
            TaskEntity updateEntity = new TaskEntity();
            updateEntity.setId(task.getId());
            updateEntity.setDeleted(task.getId());
            updateEntity.setVersion(task.getVersion());
            result = result && taskRepository.updateTask(updateEntity);
        }
        log.info("delete task, jobId: {}, count: {}, result: {}", jobId, tasks.size(), result);
        return result;
    }

    /**
     * 调度任务
     */
    private boolean schedule(TaskScheduleContext context, Long jobId, Function<TaskEntity, Boolean> shouldScheduleFun) {
        List<TaskEntity> jobTaskList = taskRepository.findByJobId(jobId);
        String jobName = jobTaskList.stream().findFirst().map(this::buildJobName).orElse(null);
        log.info("schedule job start, jobId: {}, jobName: {}", jobId, jobName);

        boolean result = true;
        Set<Long> scheduledTaskIds = Sets.newHashSet();
        List<TaskEntity> scheduleTaskList = getNoPreTaskTask(jobTaskList, scheduledTaskIds);
        if (CollectionUtils.isEmpty(scheduleTaskList)) {
            return true;
        }

        int loop = 1;
        while (CollectionUtils.isNotEmpty(scheduleTaskList)) {
            if (loop++ > MAX_TASK_COUNT) {
                log.error("schedule task exceeded max loop times, jobId: {}", jobId);
                break;
            }

            //遍历任务并执行
            for (TaskEntity task : scheduleTaskList) {
                scheduledTaskIds.add(task.getId());
                if (log.isDebugEnabled()) {
                    log.debug("schedule task start, jobId: {}, taskId: {}, taskName: {}", task.getJobId(), task.getId(), task.getTaskName());
                }
                boolean shouldSchedule = shouldScheduleFun.apply(task);
                if (shouldSchedule) {
                    boolean rt = scheduleNow(context, task);
                    if (!rt) {
                        log.error("schedule task end, jobId: {}, taskId: {}, result: {}, taskName: {}", task.getJobId(), task.getId(), result, task.getTaskName());
                    }
                    result = result && rt;
                }
            }

            //再次判断，前驱任务执行完毕，判断是否有后继任务可以执行
            scheduleTaskList = getNoPreTaskTask(jobTaskList, scheduledTaskIds);
        }
        log.info("schedule job end, jobId: {}, result: {}, jobName: {}", jobId, result, jobName);
        return result;
    }

    /**
     * 立即调度，不判断条件(调用者判断条件)
     */
    private boolean scheduleNow(TaskScheduleContext context, TaskEntity task) {
        TaskStatusEnum status = TaskStatusEnum.RUNNING;
        TaskResult taskResult = new TaskResult();
        boolean retry = task.getRetryTimes() > 0 || TaskStatusEnum.FAILED.equals(task.getStatus());

        TaskListener listener = listenerMap.get(task.getTaskBizType());
        if (null == listener) {
            log.error("schedule task start failed, listener not found, jobId: {}, taskId: {}, taskName: {}", task.getJobId(), task.getId(), task.getTaskName());
            status = TaskStatusEnum.TERMINATED;
            taskResult.setMessage("未配置任务监听器");
        } else {
            try {
                boolean result = start(task, status);
                if (!result) {
                    log.error("schedule task start failed, update start status false, jobId: {}, taskId: {}, taskName: {}", task.getJobId(), task.getId(), task.getTaskName());
                    return false;
                }

                TaskEntity scheduleTask = TaskConvertor.copyEntity(task);
                result = listener.onSchedule(scheduleTask);

                //记录结果
                taskResult.setData(Optional.ofNullable(scheduleTask.getResult()).map(TaskResult::getData).orElse(null));
                taskResult.setMessage(Optional.ofNullable(scheduleTask.getResult()).map(TaskResult::getMessage).orElse(null));
                if (result) {
                    status = TaskStatusEnum.SUCCESS;
                    taskResult.setMessage(Optional.ofNullable(taskResult.getMessage()).orElse("成功"));
                } else {
                    status = TaskStatusEnum.FAILED;
                    taskResult.setMessage(Optional.ofNullable(taskResult.getMessage()).orElse("失败"));
                    log.error("schedule task end failed, task return false, jobId: {}, taskId: {}, taskName: {}", task.getJobId(), task.getId(), task.getTaskName());
                }
            } catch (Exception e) {
                log.error("schedule task system error, jobId: {}, taskId: {}, taskName: {}", task.getJobId(), task.getId(), task.getTaskName(), e);
                status = TaskStatusEnum.FAILED;
                taskResult.setMessage(e.getMessage());
            }
        }
        if (task.getRetryTimes() >= context.getMaxRetryTimes() - 1 && (!TaskStatusEnum.SUCCESS.equals(status))) {
            log.error("schedule task end failed, retry times exceeded max retry times, jobId: {}, taskId: {}, taskName: {}",
                    task.getJobId(), task.getId(), task.getTaskName());
        }
        try {
            return finish(task, status, taskResult, retry);
        } catch (Exception e) {
            log.error("schedule task end system error, jobId: {}, taskId: {}, taskName: {}", task.getJobId(), task.getId(), task.getTaskName(), e);
            return false;
        }
    }

    /**
     * 无前驱任务并且非成功的任务
     */
    private List<TaskEntity> getNoPreTaskTask(List<TaskEntity> jobTaskList, Set<Long> scheduledTaskIds) {
        Map<Long, TaskEntity> taskMap = jobTaskList.stream().collect(Collectors.toMap(TaskEntity::getId, Function.identity()));
        List<TaskEntity> scheduleList = Lists.newArrayList();
        for (TaskEntity task : jobTaskList) {
            if (scheduledTaskIds.contains(task.getId())) {
                continue;
            }
            //判断前驱任务状态
            Set<Long> preTaskIds = task.getPreTaskIds();
            if (CollectionUtils.isEmpty(preTaskIds)) {
                scheduleList.add(task);
                continue;
            }
            boolean preTaskSuccess = preTaskIds.stream().allMatch(taskId -> TaskStatusEnum.SUCCESS.equals(taskMap.get(taskId).getStatus()));
            if (preTaskSuccess) {
                //具有副作用，会在内存中更新任务状态，避免重新查询数据库
                if (TaskStatusEnum.WAITING.equals(task.getStatus())) {
                    task.setStatus(TaskStatusEnum.READY);
                }
                scheduleList.add(task);
            }
        }
        return scheduleList;
    }

    /**
     * 判断状态、重试次数、预期执行时间
     */
    private boolean shouldSchedule(TaskScheduleContext context, TaskEntity task) {
        boolean unit = StringUtils.equals(EnvironmentUtils.getUnit().getValue(), task.getUnit().getValue());
        boolean ready = TaskStatusEnum.READY.equals(task.getStatus());
        boolean retry = TaskStatusEnum.FAILED.equals(task.getStatus()) && task.getRetryTimes() < context.getMaxRetryTimes();
        boolean executeTime = null == task.getExpectExecuteTime() || task.getExpectExecuteTime().before(DateUtils.addSeconds(new Date(), context.getMaxExpectExecuteTimeSeconds()));
        boolean result = unit && (ready || retry) && executeTime;
        if (log.isDebugEnabled()) {
            log.debug("should schedule task: {}, jobId: {}, taskId: {}, unit: {}, ready: {}, retry: {}, executeTime: {}, taskName: {}",
                result, task.getJobId(), task.getId(), unit, ready, retry, executeTime, task.getTaskName());
        }
        return result;
    }

    /**
     * 开始执行任务
     */
    private boolean start(TaskEntity task, TaskStatusEnum status) {
        TaskEntity updateEntity = new TaskEntity();
        updateEntity.setId(task.getId());
        updateEntity.setStatus(status);
        updateEntity.setVersion(task.getVersion());
        boolean result = taskRepository.updateTask(updateEntity);
        if (result) {
            //具有副作用，会在内存中更新任务状态，避免重新查询数据库
            task.setStatus(status);
            task.setVersion(task.getVersion() + 1);
        }
        return result;
    }

    /**
     * 完成执行任务
     */
    private boolean finish(TaskEntity task, TaskStatusEnum status, TaskResult taskResult, boolean retry) {
        TaskEntity updateEntity = new TaskEntity();
        updateEntity.setId(task.getId());
        updateEntity.setStatus(status);
        updateEntity.setResult(taskResult);
        //更新传入retryTimes sql会自动+1
        updateEntity.setRetryTimes(retry ? task.getRetryTimes() : null);
        updateEntity.setVersion(task.getVersion());
        boolean result = taskRepository.updateTask(updateEntity);
        if (result) {
            //具有副作用，会在内存中更新任务状态，避免重新查询数据库
            task.setStatus(status);
            task.setResult(taskResult);
            task.setVersion(task.getVersion() + 1);
            task.setRetryTimes(retry ? task.getRetryTimes() + 1 : task.getRetryTimes());
        }
        return TaskStatusEnum.SUCCESS.equals(status);
    }

    /**
     * 补充基本信息
     *
     * @return 哈希索引
     */
    private Long setBasicInfo(JobBizTypeEnum jobBizType, String jobKey, List<TaskEntity> tasks) {
        Date now = new Date();
        for (TaskEntity task : tasks) {
            task.setJobBizType(jobBizType);
            task.setJobBizKey(jobKey);
            task.setStatus(CollectionUtils.isEmpty(task.getPreTaskIds()) ? TaskStatusEnum.READY : TaskStatusEnum.WAITING);
            if (null == task.getExpectExecuteTime()) {
                task.setExpectExecuteTime(now);
            }
        }

        Long hashIndex = buildHashIndex(tasks);
        for (TaskEntity task : tasks) {
            task.setHashIndex(hashIndex);
        }
        return hashIndex;
    }

    /**
     * 设置Id
     *
     * @return 作业Id
     */
    private Long setId(List<TaskEntity> tasks) {
        Map<Long, Long> idMap = Maps.newHashMap();
        // TODO 业务标识 按需实现 可以设置全局序列号 此处只保证单机环境下唯一
        Long jobId = UniqueIdGenerator.generate12DigitId();
        for (TaskEntity task : tasks) {
            Long id = UniqueIdGenerator.generate12DigitId();
            idMap.put(task.getId(), id);
            task.setId(idMap.get(task.getId()));
            task.setJobId(jobId);
        }

        //依赖设置真实Id
        for (TaskEntity task : tasks) {
            task.setPreTaskIds(task.getPreTaskIds().stream().map(idMap::get).collect(Collectors.toSet()));
            task.setNextTaskIds(task.getNextTaskIds().stream().map(idMap::get).collect(Collectors.toSet()));
        }
        return jobId;
    }

    private Long existJobId(JobBizTypeEnum jobBizType, String jobBizKey, Long hashIndex, List<TaskEntity> tasks) {
        TaskQuery queryDTO = new TaskQuery();
        queryDTO.setJobBizType(jobBizType.name());
        queryDTO.setJobBizKey(jobBizKey);
        queryDTO.setHashIndex(hashIndex);
        queryDTO.setStatus(TaskStatusEnum.READY.name());
        List<Long> existJobIdList = taskRepository.findByJobKey(queryDTO);
        if (CollectionUtils.isEmpty(existJobIdList)) {
            return null;
        }
        List<TaskEntity> existList = taskRepository.findByJobIds(existJobIdList);

        //jobId : tasks
        Map<Long, List<TaskEntity>> jobMap = existList.stream().collect(Collectors.groupingBy(TaskEntity::getJobId));
        for (Map.Entry<Long, List<TaskEntity>> entry : jobMap.entrySet()) {
            List<TaskEntity> existTasks = entry.getValue();
            List<String> taskKeys = buildTaskSortedKeys(tasks);
            List<String> existTaskKeys = buildTaskSortedKeys(existTasks);
            if (taskKeys.equals(existTaskKeys)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 判断是否有循环依赖
     */
    public String hasCyclicDependency(List<Task> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            // 无循环依赖
            return null;
        }

        // 0：未访问，1：正在访问，2：已访问
        Map<Task, Integer> visitedMap = new HashMap<>();
        Stack<Task> path = new Stack<>();

        for (Task task : tasks) {
            int status = Optional.ofNullable(visitedMap.get(task)).orElse(0);
            if (status == 0) {
                String cyclePath = hasCycleDfs(task, visitedMap, path);
                if (null != cyclePath) {
                    return cyclePath;
                }
            }
        }

        // 无循环依赖
        return null;
    }

    /**
     * 判断是否有循环依赖：深度优先搜索（DFS）
     */
    private String hasCycleDfs(Task current, Map<Task, Integer> visitedMap, Stack<Task> path) {
        path.push(current);
        visitedMap.put(current, 1);

        Set<Task> nextTasks = Optional.ofNullable(current.getNextTasks()).orElse(Collections.emptySet());
        for (Task nextTask : nextTasks) {
            int status = Optional.ofNullable(visitedMap.get(nextTask)).orElse(0);
            //循环依赖
            if (status == 1) {
                int startIndex = path.indexOf(nextTask);
                if (startIndex != -1) {
                    // 提取环路径：从startIndex到当前栈顶的所有节点
                    List<Task> cyclePath = path.subList(startIndex, path.size());
                    return cyclePath.stream().map(this::generateTaskKey).collect(Collectors.joining(" -> ")) + " -> " + generateTaskKey(nextTask);
                }
            } else if (status == 0) {
                String cycle = hasCycleDfs(nextTask, visitedMap, path);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        // 回溯：标记为已访问，并弹出当前节点
        visitedMap.put(current, 2);
        path.pop();
        return null;
    }

    /**
     * 打平任务依赖：广度优先搜索（BFS）
     */
    public List<TaskEntity> flatTaskBfs(List<Task> tasks) {
        AtomicLong taskTempIdGenerator = new AtomicLong(1L);
        Map<String, TaskEntity> resultMap = new HashMap<>();
        Map<String, TaskEntity> taskCacheMap = new HashMap<>();

        // 使用队列实现 BFS 遍历，确保每个任务只处理一次
        Queue<Task> queue = new LinkedList<>(tasks);
        while (!queue.isEmpty()) {
            Task current = queue.poll();

            TaskEntity currentEntity = fetchOrCreateTaskEntity(current, taskCacheMap, taskTempIdGenerator);
            resultMap.put(generateTaskKey(current), currentEntity);

            // 处理子任务
            Set<Task> nextTasks = Optional.ofNullable(current.getNextTasks()).orElse(Collections.emptySet());
            for (Task childTask : nextTasks) {
                queue.offer(childTask);
                TaskEntity childEntity = fetchOrCreateTaskEntity(childTask, taskCacheMap, taskTempIdGenerator);

                // 设置父子关系
                currentEntity.getNextTaskIds().add(childEntity.getId());
                childEntity.getPreTaskIds().add(currentEntity.getId());
            }
        }

        return Lists.newArrayList(resultMap.values()).stream().sorted(Comparator.comparing(TaskEntity::getId)).collect(Collectors.toList());
    }

    /**
     * 获取或构造任务
     */
    private TaskEntity fetchOrCreateTaskEntity(Task task, Map<String, TaskEntity> taskCacheMap, AtomicLong taskTempIdGenerator) {
        String taskKey = generateTaskKey(task);
        TaskEntity taskEntity = taskCacheMap.get(taskKey);
        if (null == taskEntity) {
            taskEntity = TaskConvertor.convert2TaskEntity(task);
            taskEntity.setId(taskTempIdGenerator.getAndIncrement());
            taskCacheMap.put(taskKey, taskEntity);
        }
        return taskEntity;
    }

    /**
     * hash(preTaskKey # preTaskKey ~ taskKey1 ~ nextTaskKey # nextTaskKey @ preTaskKey # preTaskKey ~ taskKey2 ~ nextTaskKey # nextTaskKey)
     */
    private Long buildHashIndex(List<TaskEntity> tasks) {
        List<String> taskSortedKeys = buildTaskSortedKeys(tasks);
        return HashUtils.hash(String.join(TaskConstant.TASK_TO_TASK_SEPARATOR, taskSortedKeys));
    }

    /**
     * [
     * preTaskKey # preTaskKey ~ taskKey1 ~ nextTaskKey # nextTaskKey
     * preTaskKey # preTaskKey ~ taskKey2 ~ nextTaskKey # nextTaskKey
     * ]
     */
    private List<String> buildTaskSortedKeys(List<TaskEntity> entities) {
        //任务Id : TaskEntity(unit|taskBizType|taskBizKey)
        Map<Long, String> existIdKeymap = entities.stream().collect(Collectors.toMap(TaskEntity::getId, this::generateTaskKey));
        // preTaskKey # preTaskKey ~ taskKey ~ nextTaskKey # nextTaskKey
        return entities.stream().map(it -> generateTaskKeyWithDependency(it, existIdKeymap)).sorted().toList();
    }

    /**
     * 生成任务唯一Key: TaskDTO(unit|taskBizType|taskBizKey)
     */
    private String generateTaskKey(Task task) {
        return task.getUnit().getValue() + TaskConstant.KEY_SEPARATOR + task.getTaskBizType() + TaskConstant.KEY_SEPARATOR + task.getTaskBizKey();
    }

    /**
     * 生成任务唯一Key: TaskEntity(unit|taskBizType|taskBizKey)
     */
    private String generateTaskKey(TaskEntity task) {
        return task.getUnit().getValue() + TaskConstant.KEY_SEPARATOR + task.getTaskBizType() + TaskConstant.KEY_SEPARATOR + task.getTaskBizKey() + TaskConstant.KEY_SEPARATOR + task.getStatus();
    }

    /**
     * 生成任务唯一Key: preTaskKey # preTaskKey ~ taskKey ~ nextTaskKey # nextTaskKey
     */
    private String generateTaskKeyWithDependency(TaskEntity task, Map<Long, String> idKeyMap) {
        String preTaskKeys = task.getPreTaskIds().stream().map(idKeyMap::get).sorted().collect(Collectors.joining(TaskConstant.DEPENDENCY_TO_DEPENDENCY_SEPARATOR));
        String nextTaskKeys = task.getNextTaskIds().stream().map(idKeyMap::get).sorted().collect(Collectors.joining(TaskConstant.DEPENDENCY_TO_DEPENDENCY_SEPARATOR));
        return preTaskKeys + TaskConstant.TASK_TO_DEPENDENCY_SEPARATOR + generateTaskKey(task) + TaskConstant.TASK_TO_DEPENDENCY_SEPARATOR + nextTaskKeys;
    }

    /**
     * 构造作业名称
     */
    private String buildJobName(TaskEntity task) {
        return task.getUnit().getValue() + ":" + task.getJobBizType().name() + ":" + task.getJobBizKey();
    }

    /**
     * 构造查询名称
     */
    private String buildQueryName(TaskQuery query) {
        return query.getJobBizType() + "-" + query.getUnit() + "-" + query.getStatus();
    }
}
