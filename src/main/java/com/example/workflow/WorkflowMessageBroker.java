package com.example.workflow;

import java.util.concurrent.CountDownLatch;

public interface WorkflowMessageBroker {
    CountDownLatch listenForTaskDone(WorkflowTask task);

    void publishTaskDone(WorkflowTask task);
}
