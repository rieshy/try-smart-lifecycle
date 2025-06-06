package com.example.model;

import com.example.service.WorkflowAction;

public interface WorkflowTask {
    void execute();
    WorkflowAction getAction();
    String getWorkflowId();
    String getDescription();
}
