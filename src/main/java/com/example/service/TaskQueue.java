package com.example.service;

import com.example.model.Task;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class TaskQueue {
    private final BlockingQueue<Task> queue;

    public TaskQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public void addTask(Task task) {
        queue.offer(task);
    }

    public Task takeTask() throws InterruptedException {
        return queue.take();
    }

    public Task pollTask(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
} 