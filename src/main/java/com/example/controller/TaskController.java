package com.example.controller;

import com.example.model.Task;
import com.example.service.TaskQueue;
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
    private final TaskQueue taskQueue;

    @Autowired
    public TaskController(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @PostMapping
    public String createTasks(@RequestParam String description) {
        List<Task> tasks = Arrays.stream(description.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(Task::new)
                .collect(Collectors.toList());
        
        tasks.forEach(taskQueue::addTask);
        
        return String.format("Created %d tasks: %s", 
            tasks.size(), 
            tasks.stream()
                .map(task -> task.getId().toString())
                .collect(Collectors.joining(", ")));
    }
} 