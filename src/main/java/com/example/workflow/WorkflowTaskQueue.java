package com.example.workflow;

import java.util.concurrent.TimeUnit;

public interface WorkflowTaskQueue {
    WorkflowTask poll(long timeout, TimeUnit unit) throws InterruptedException;
    void offer(WorkflowTask task);
    boolean isEmpty();
    String toString();
}
