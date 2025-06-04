package com.example.controller;

import com.example.model.Task;
import com.example.service.TaskQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskQueue taskQueue;

    @Autowired
    public TaskController(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @PostMapping
    public String createTask(@RequestParam String description) {
        Task task = new Task(UUID.randomUUID().toString(), description);
        taskQueue.addTask(task);
        return "Task created: " + task.getId();
    }
} 