package com.example.workflow;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Component("localWorkflowMessageBroker")
public class LocalWorkflowMessageBroker implements WorkflowMessageBroker {
    private final ConcurrentHashMap<String, CountDownLatch> latches = new ConcurrentHashMap<>();

    @Override
    public CountDownLatch listenForTaskDone(WorkflowTask task) {
        String taskId = task.getId();
        if (taskId == null || taskId.isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        }

        CountDownLatch latch = new CountDownLatch(1);
        latches.put(taskId, latch);

        return latch;
    }

    @Override
    public void publishTaskDone(WorkflowTask task) {
        String taskId = task.getId();
        if (taskId == null || taskId.isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        }

        CountDownLatch latch = latches.remove(taskId);
        if (latch != null) {
            latch.countDown();
        } else {
            throw new IllegalStateException("No latch found for task ID: " + taskId);
        }
    }
}
