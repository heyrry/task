package com.herry.task.repository.impl;


import com.herry.task.entity.TaskEntity;
import com.herry.task.mapper.TaskMapper;
import com.herry.task.repository.TaskRepository;
import com.herry.task.repository.convert.Converters;
import com.herry.task.repository.convert.TaskConvertor;
import com.herry.task.repository.dataobject.TaskDO;
import com.herry.task.repository.dataobject.TaskWithBLOBs;
import com.herry.task.repository.dto.TaskQuery;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author herry
 * @since 2025/03/30
 */
@Repository
public class TaskRepositoryImpl implements TaskRepository {

    @Resource
    private TaskMapper taskMapper;

    @Override
    public void saveTaskList(List<TaskEntity> tasks) {
        List<TaskDO> doList = Converters.convertList(tasks, TaskConvertor::entity2DO);
        taskMapper.batchInsert(doList);
    }

    @Override
    public TaskEntity findByTaskId(Long taskId) {
        TaskWithBLOBs dataObject = taskMapper.findById(taskId);
        return TaskConvertor.doWithBlob2Entity(dataObject);
    }

    @Override
    public List<TaskEntity> findByJobIds(List<Long> jobIds) {
        List<TaskDO> doList = taskMapper.findByJobIds(jobIds);
        return Converters.convertList(doList, TaskConvertor::do2Entity);
    }

    @Override
    public List<TaskEntity> findByJobId(Long jobId) {
        List<TaskWithBLOBs> doList = taskMapper.findByJobId(jobId);
        return Converters.convertList(doList, TaskConvertor::doWithBlob2Entity);
    }

    @Override
    public boolean updateTask(TaskEntity task) {
        TaskWithBLOBs dataObject = TaskConvertor.entity2DO(task);
        return taskMapper.updateById(dataObject) > 0;
    }

    @Override
    public List<Long> findByJobKey(TaskQuery queryDTO) {
        List<TaskDO> doList = taskMapper.findByJobKey(queryDTO);
        return Optional.ofNullable(doList).orElse(Collections.emptyList()).stream().map(TaskDO::getJobId).distinct().collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> scanJobList(TaskQuery queryDTO) {
        List<TaskDO> doList = taskMapper.scanJobList(queryDTO);
        return Optional.ofNullable(doList).orElse(Collections.emptyList()).stream().map(TaskConvertor::do2Entity).distinct().collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> queryList(TaskQuery queryDTO) {
        List<TaskDO> doList = taskMapper.queryList(queryDTO);
        return Optional.ofNullable(doList).orElse(Collections.emptyList()).stream().map(TaskConvertor::do2Entity).distinct().collect(Collectors.toList());
    }
}
