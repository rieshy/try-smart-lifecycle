package com.example.service;

import com.example.model.WorkflowTask;

public class WorkflowStartTask implements WorkflowTask {
    private final String id;
    private final String description;

    public WorkflowStartTask(String id, String description) {
        this.id = id;
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
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("WorkflowStartTask[id=%s, description=%s]", id, description);
    }
}
