package com.example.service;

import java.util.concurrent.TimeUnit;

import com.example.model.WorkflowTask;

public interface WorkflowTaskQueue {
    WorkflowTask poll(long timeout, TimeUnit unit) throws InterruptedException;
    void offer(WorkflowTask task);
    boolean isEmpty();
    void notifyWorkers(); // For distributed notifications
    String toString();
}
