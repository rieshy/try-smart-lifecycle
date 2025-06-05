package com.example.service;

import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.Optional;

@Service
public class TaskQueue {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskQueue(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void addTask(Task task) {
        taskRepository.save(task);
    }

    @Transactional
    public Task takeTask() throws InterruptedException {
        long endTime = System.currentTimeMillis() + 1000; // 1 second timeout
        
        while (true) {
            Optional<Task> task = taskRepository.findFirstByStatusOrderByCreatedAtAsc(TaskStatus.PENDING);
            if (task.isPresent()) {
                Task foundTask = task.get();
                foundTask.setStatus(TaskStatus.PROCESSING);
                foundTask.setProcessedAt(java.time.LocalDateTime.now());
                return taskRepository.save(foundTask);
            }
            
            if (System.currentTimeMillis() > endTime) {
                return null;
            }
            
            Thread.sleep(100);
        }
    }

    @Transactional
    public Task pollTask(long timeout, TimeUnit unit) throws InterruptedException {
        long endTime = System.currentTimeMillis() + unit.toMillis(timeout);
        
        while (true) {
            Optional<Task> task = taskRepository.findFirstByStatusOrderByCreatedAtAsc(TaskStatus.PENDING);
            if (task.isPresent()) {
                Task foundTask = task.get();
                foundTask.setStatus(TaskStatus.PROCESSING);
                foundTask.setProcessedAt(java.time.LocalDateTime.now());
                return taskRepository.save(foundTask);
            }
            
            if (System.currentTimeMillis() > endTime) {
                return null;
            }
            
            Thread.sleep(100);
        }
    }

    @Transactional
    public void markTaskCompleted(Task task) {
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
    }

    @Transactional
    public void markTaskFailed(Task task) {
        task.setStatus(TaskStatus.FAILED);
        taskRepository.save(task);
    }
} 