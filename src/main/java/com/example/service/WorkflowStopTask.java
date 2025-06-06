package com.example.service;

import com.example.model.WorkflowTask;

public class WorkflowStopTask implements WorkflowTask {
    private final String workflowId;
    private final String description;

    public WorkflowStopTask(String workflowId, String description) {
        this.workflowId = workflowId;
        this.description = description;
    }
    
    @Override
    public void execute() {
        System.out.println("Stopping workflow...");
    }
    
    @Override
    public WorkflowAction getAction() {
        return WorkflowAction.STOP;
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
        return String.format("WorkflowStopTask[workflowId=%s, description=%s]", workflowId, description);
    }
}
