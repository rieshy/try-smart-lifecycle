package com.example.service;

import com.example.model.WorkflowTask;

public class WorkflowStopTask implements WorkflowTask {
    private final String id;
    private final String description;

    public WorkflowStopTask(String id, String description) {
        this.id = id;
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
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("WorkflowStopTask[id=%s, description=%s]", id, description);
    }
}
