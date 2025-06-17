package com.example.controller;

import com.example.workflow.StartWorkflowTask;
import com.example.workflow.WorkflowService;
import com.example.workflow.WorkflowTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflows")
public class TaskController {
    private final WorkflowService workflowService;

    @Autowired
    public TaskController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public String startWorkflows(@RequestParam String workflowIds) {
        List<WorkflowTask> tasks = Arrays.stream(workflowIds.split("\\s+"))
                .filter(workflowId -> !workflowId.isEmpty())
                .map(workflowId -> new StartWorkflowTask("Start workflow " + workflowId))
                .collect(Collectors.toList());

        tasks.forEach(workflowService::submitTask);

        return String.format("Started %d workflows: %s",
                tasks.size(),
                tasks.stream()
                        .map(WorkflowTask::getId)
                        .collect(Collectors.joining(", ")));
    }
} 