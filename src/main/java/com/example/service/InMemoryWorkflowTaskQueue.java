package com.example.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.example.model.WorkflowTask;

@Component
@Primary
public class InMemoryWorkflowTaskQueue implements WorkflowTaskQueue {
    private final BlockingQueue<WorkflowTask> queue = new LinkedBlockingQueue<>();
    
    @Override
    public WorkflowTask poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
    
    @Override
    public void offer(WorkflowTask task) {
        queue.offer(task);
    }
    
    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    @Override
    public void notifyWorkers() {
        // No-op for in-memory queue as BlockingQueue handles notifications
    }
}
