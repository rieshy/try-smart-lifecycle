package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.WorkflowTask;

public class WorkflowStartTask implements WorkflowTask {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowStartTask.class);

    private final String workflowId;
    private final String description;

    public WorkflowStartTask(String workflowId, String description) {
        this.workflowId = workflowId;
        this.description = description;
    }

    @Override
    public void execute() {
        logger.info("Executing task: {}", description);
        try {
            for (int i = 0; i < 10; i++) {
                logger.info("Executing step {} of task '{}'", i, description);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Task got interrupted");
        }
    }

    @Override
    public WorkflowAction getAction() {
        return WorkflowAction.START;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("WorkflowStartTask[workflowId=%s, description=%s]", workflowId, description);
    }
}
