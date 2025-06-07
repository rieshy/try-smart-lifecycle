package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.WorkflowTask;

public class WorkflowStartTask implements WorkflowTask {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowStartTask.class);

    private WorkflowAction action;
    private String workflowId;
    private String description;

    public WorkflowStartTask() {
        this.action = WorkflowAction.START;
    }

    public WorkflowStartTask(String workflowId, String description) {
        this();
        this.workflowId = workflowId;
        this.description = description;
    }

    @Override
    public WorkflowAction getAction() {
        return action;
    }

    public void setAction(WorkflowAction action) {
        this.action = action;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("WorkflowStartTask[workflowId=%s, description=%s]", workflowId, description);
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
}
