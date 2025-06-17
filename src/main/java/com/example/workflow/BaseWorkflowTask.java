package com.example.workflow;

import java.util.UUID;

abstract class BaseWorkflowTask implements WorkflowTask {
    private String id;
    private String description;

    public BaseWorkflowTask() {
        this.id = UUID.randomUUID().toString();
    }

    public BaseWorkflowTask(String description) {
        this();
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public abstract void execute();
}
