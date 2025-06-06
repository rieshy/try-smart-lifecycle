package com.example.controller;

import com.example.model.WorkflowTask;
import com.example.service.WorkflowService;
import com.example.service.WorkflowStartTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final WorkflowService workflowService;
    
    @Autowired
    public TaskController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public String createTasks(@RequestParam String userInput) {
        List<WorkflowTask> tasks = Arrays.stream(userInput.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> new WorkflowStartTask(word, String.format("Start workflow with id %s", word)))
                .collect(Collectors.toList());
        
        tasks.forEach(workflowService::submitTask);
        
        return String.format("Created %d tasks: %s", 
            tasks.size(), 
            tasks.stream()
                .map(task -> task.getWorkflowId())
                .collect(Collectors.joining(", ")));
    }
} 