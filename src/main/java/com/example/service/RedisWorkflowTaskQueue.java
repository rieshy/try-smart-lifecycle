package com.example.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.model.WorkflowTask;

@Component
public class RedisWorkflowTaskQueue implements WorkflowTaskQueue {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String queueKey = "workflow_task_queue";
    private final String notificationChannel = "workflow_notification";
    
    @Autowired
    public RedisWorkflowTaskQueue(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public WorkflowTask poll(long timeout, TimeUnit unit) throws InterruptedException {
        Object result = redisTemplate.opsForList().rightPop(queueKey, timeout, unit);
        if (result != null) {
            return (WorkflowTask) result;
        }
        return null;
    }

    @Override
    public void offer(WorkflowTask task) {
        redisTemplate.opsForList().leftPush(queueKey, task);
        notifyWorkers();
    }

    @Override
    public boolean isEmpty() {
        Long size = redisTemplate.opsForList().size(queueKey);
        return size == null || size == 0;
    }

    @Override
    public void notifyWorkers() {
        redisTemplate.convertAndSend(notificationChannel, "new_task");
    }
}
