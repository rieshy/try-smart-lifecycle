package com.example.workflow;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component("localWorkflowTaskQueue")
public class LocalWorkflowTaskQueue implements WorkflowTaskQueue {
    private final BlockingQueue<WorkflowTask> queue = new LinkedBlockingQueue<>();

    @Override
    public WorkflowTask poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    @Override
    public void offer(WorkflowTask task) {
        if (!queue.offer(task)) {
            throw new IllegalStateException("Failed to add task to the queue: " + task);
        }
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public String toString() {
        List<WorkflowTask> elements = new ArrayList<>(queue);
        return "LocalWorkflowTaskQueue[queue=" + elements.subList(0, Math.min(elements.size(), 10)) + "]";
    }
}
