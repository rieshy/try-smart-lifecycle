package com.example.workflow;

public interface WorkflowTask {
    String getId();

    String getDescription();

    void execute();
}
