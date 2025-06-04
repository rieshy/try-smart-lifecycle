package com.example.service;

import com.example.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TaskProcessor implements SmartLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
    
    private final TaskQueue taskQueue;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread processorThread;

    @Autowired
    public TaskProcessor(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            processorThread = new Thread(this::processTasks);
            processorThread.setName("task-processor");
            processorThread.start();
            logger.info("Task processor started");
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping task processor...");
            try {
                if (processorThread != null) {
                    processorThread.join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Task processor was interrupted during shutdown", e);
            }
            logger.info("Task processor stopped");
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE; // Start late, stop early
    }

    private void processTasks() {
        while (running.get()) {
            try {
                Task task = taskQueue.takeTask();
                logger.info("Processing task: {}", task);
                // Simulate task processing
                Thread.sleep(2000);
                logger.info("Task completed: {}", task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!running.get()) {
                    break;
                }
                logger.error("Task processor was interrupted", e);
            }
        }
    }
} 