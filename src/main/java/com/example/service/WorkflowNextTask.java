package com.example.service;

import com.example.model.WorkflowTask;

public class WorkflowNextTask implements WorkflowTask {
    private final String id;
    private final String description;

    public WorkflowNextTask(String id, String description) {
        this.id = id;
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
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("WorkflowNextTask[id=%s, description=%s]", id, description);
    }
}
