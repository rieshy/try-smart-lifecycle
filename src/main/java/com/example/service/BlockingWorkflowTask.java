package com.example.service;

import com.example.model.WorkflowTask;
import com.example.model.WorkflowTaskCompletionSupport;
import java.util.concurrent.CountDownLatch;

/**
 * Wrapper for WorkflowTask that allows waiting for completion.
 */
public class BlockingWorkflowTask implements WorkflowTask, WorkflowTaskCompletionSupport {
    private final WorkflowTask delegate;
    private final CountDownLatch latch = new CountDownLatch(1);

    public BlockingWorkflowTask(WorkflowTask delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute() {
        delegate.execute();
    }

    @Override
    public WorkflowAction getAction() {
        return delegate.getAction();
    }

    @Override
    public String getWorkflowId() {
        return delegate.getWorkflowId();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    public void complete() {
        latch.countDown();
    }

    public void awaitCompletion() throws InterruptedException {
        latch.await();
    }
} 