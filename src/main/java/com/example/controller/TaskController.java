package com.example.controller;

import com.example.model.WorkflowTask;
import com.example.service.WorkflowStartTask;
import com.example.service.WorkflowTaskQueue;
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
    private final WorkflowTaskQueue taskQueue;
    
    @Autowired
    public TaskController(WorkflowTaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @PostMapping
    public String createTasks(@RequestParam String userInput) {
        List<WorkflowTask> tasks = Arrays.stream(userInput.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> new WorkflowStartTask(word, String.format("Start workflow with %s", word)))
                .collect(Collectors.toList());
        
        tasks.forEach(taskQueue::offer);
        
        return String.format("Created %d tasks: %s", 
            tasks.size(), 
            tasks.stream()
                .map(task -> task.getId().toString())
                .collect(Collectors.joining(", ")));
    }
} 