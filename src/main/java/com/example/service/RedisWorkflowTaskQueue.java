package com.example.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.model.WorkflowTask;

@Component("redisWorkflowTaskQueue")
public class RedisWorkflowTaskQueue implements WorkflowTaskQueue {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String queueKey = "workflow_task_queue";
    
    @Autowired
    public RedisWorkflowTaskQueue(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public WorkflowTask poll(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            Object result = redisTemplate.opsForList().rightPop(queueKey, timeout, unit);
            if (result != null) {
                return (WorkflowTask) result;
            }
            return null;
        } catch (RedisSystemException e) {
            // Look for InterruptedException in the cause chain
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof InterruptedException) {
                    throw (InterruptedException) cause;
                }
                cause = cause.getCause();
            }
            throw e;
        }
    }

    @Override
    public void offer(WorkflowTask task) {
        redisTemplate.opsForList().leftPush(queueKey, task);
    }

    @Override
    public boolean isEmpty() {
        Long size = redisTemplate.opsForList().size(queueKey);
        return size == null || size == 0;
    }

    @Override
    public String toString() {
        List<Object> elements = redisTemplate.opsForList().range(queueKey, 0, 9);
        return "RedisWorkflowTaskQueue[queueKey=" + queueKey + ", elements(0-9)=" + elements + "]";
    }
}
