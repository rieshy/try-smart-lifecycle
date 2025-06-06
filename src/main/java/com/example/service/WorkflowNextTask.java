package com.example.service;

import com.example.model.WorkflowTask;

public class WorkflowNextTask implements WorkflowTask {
    private final String workflowId;
    private final String description;

    public WorkflowNextTask(String workflowId, String description) {
        this.workflowId = workflowId;
        this.description = description;
    }

    @Override
    public void execute() {
        System.out.println("Moving the worflow to the next stage...");
    }

    @Override
    public WorkflowAction getAction() {
        return WorkflowAction.NEXT;
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
        return String.format("WorkflowNextTask[workflowId=%s, description=%s]", workflowId, description);
    }
}
