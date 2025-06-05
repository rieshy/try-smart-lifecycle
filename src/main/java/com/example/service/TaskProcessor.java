package com.example.service;

import com.example.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class TaskProcessor implements SmartLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);
    private static final int CONCURRENT_TASKS = 3; // Number of concurrent tasks to process
    private static final long POLL_TIMEOUT_MS = 100; // Timeout for polling tasks
    
    private final TaskQueue taskQueue;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ExecutorService executorService;

    @Autowired
    public TaskProcessor(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            executorService = Executors.newFixedThreadPool(CONCURRENT_TASKS);
            
            // Start worker threads
            for (int i = 0; i < CONCURRENT_TASKS; i++) {
                final int workerId = i + 1;
                executorService.submit(() -> {
                    while (running.get()) {
                        try {
                            Task task = taskQueue.pollTask(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                            if (task == null) {
                                // No task available, continue checking
                                continue;
                            }
                            if (!running.get()) {
                                // Put task back in queue if we're shutting down
                                taskQueue.addTask(task);
                                break;
                            }
                            processTask(task);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            if (!running.get()) {
                                break;
                            }
                            logger.error("Worker {} was interrupted", workerId, e);
                        }
                    }
                });
            }
            
            logger.info("Task processor started with {} workers", CONCURRENT_TASKS);
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping task processor...");
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    logger.warn("Task processor did not terminate gracefully within timeout");
                    executorService.shutdownNow();
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
    public void stop(@NonNull Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE; // Start late, stop early
    }

    private void processTask(Task task) {
        try {
            logger.info("Processing task: {}", task);
            // Simulate task processing
            logger.info("Processing step 1 for task: {}", task);
            Thread.sleep(2000);
            logger.info("Processing step 2 for task: {}", task);
            Thread.sleep(3000);
            logger.info("Processing step 3 for task: {}", task);
            Thread.sleep(2000);
            logger.info("Processing completed for task: {}", task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Task processing was interrupted for task: {}", task, e);
        }
    }
} 