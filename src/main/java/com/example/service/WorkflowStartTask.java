package com.example.service;

import com.example.model.WorkflowTask;

public class WorkflowStartTask implements WorkflowTask {
    private final String workflowId;
    private final String description;

    public WorkflowStartTask(String workflowId, String description) {
        this.workflowId = workflowId;
        this.description = description;
    }

    @Override
    public void execute() {
        System.out.println("Starting new workflow...");
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
