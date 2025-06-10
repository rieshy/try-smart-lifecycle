package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.model.WorkflowTask;

@Service
public class WorkflowService implements SmartLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);

    private final WorkflowTaskQueue taskQueue;
    private final List<WorkflowWorker> workers = new ArrayList<>();
    private final List<Thread> workerThreads = new ArrayList<>();
    private final int numberOfWorkers;
    private final int shutdownTimeoutSeconds;

    private volatile boolean running = false;
    private final Object lifecycleLock = new Object();

    @Autowired
    public WorkflowService(WorkflowTaskQueue taskQueue,
                           @Value("${app.workflow.worker.count:3}") int numberOfWorkers, 
                           @Value("${app.workflow.shutdown.timeout.seconds:30}") int shutdownTimeoutSeconds) {
        this.taskQueue = taskQueue;
        this.numberOfWorkers = numberOfWorkers;
        this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
    }
    
    @Override
    public void start() {
        synchronized (lifecycleLock) {
            if (!running) {
                logger.info("Starting workflow service with {} workers", numberOfWorkers);
                
                for (int i = 1; i <= numberOfWorkers; i++) {
                    WorkflowWorker worker = new WorkflowWorker(taskQueue, String.valueOf(i));
                    Thread workerThread = new Thread(worker, "workflow-worker-" + i);
                    
                    workers.add(worker);
                    workerThreads.add(workerThread);
                    
                    workerThread.start();
                }
                
                running = true;
                logger.info("Workflow service started successfully");
            }
        }
    }

    @Override
    public void stop() {
        synchronized (lifecycleLock) {
            if (running) {
                logger.info("Stopping workflow service gracefully");
                
                // Signal all workers to stop accepting new tasks
                workers.forEach(WorkflowWorker::shutdown);
                
                // Wait for all running tasks to complete
                waitForRunningTasksToComplete();
                
                // Interrupt threads only after all tasks are done
                workerThreads.forEach(Thread::interrupt);
                
                // Wait for final shutdown confirmation
                for (int i = 0; i < workers.size(); i++) {
                    try {
                        if (!workers.get(i).awaitShutdown(2, TimeUnit.SECONDS)) {
                            logger.warn("Worker {} did not confirm shutdown", i);
                        }
                    } catch (InterruptedException e) {
                        logger.warn("Interrupted while waiting for worker {} shutdown confirmation", i);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // Clean up collections
                workers.clear();
                workerThreads.clear();
                running = false;
                
                logger.info("Workflow service stopped successfully");
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public int getPhase() {
        // Return a high phase number to ensure this starts after most other beans
        // and stops before most other beans
        return Integer.MAX_VALUE - 1000;
    }
    
    @Override
    public boolean isAutoStartup() {
        return true; // Automatically start when Spring context is ready
    }
    
    @Override
    public void stop(@NonNull Runnable callback) {
        // Asynchronous shutdown support
        CompletableFuture.runAsync(() -> {
            try {
                stop();
            } finally {
                callback.run();
            }
        });
    }

    private void waitForRunningTasksToComplete() {
        logger.info("Waiting for running tasks to complete...");
        
        long maxWaitTime = shutdownTimeoutSeconds * 1000L;
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < maxWaitTime) {
            boolean anyTasksRunning = workers.stream()
                .anyMatch(WorkflowWorker::isProcessingTask);
            
            if (!anyTasksRunning) {
                logger.info("All running tasks completed");
                break;
            }
            
            // Log current running tasks every 5 seconds
            if ((System.currentTimeMillis() - startTime) % 5000 < 1000) {
                workers.stream()
                    .filter(WorkflowWorker::isProcessingTask)
                    .forEach(worker -> {
                        WorkflowTask currentTask = worker.getCurrentTask();
                        if (currentTask != null) {
                            logger.info("Waiting for task {} to complete on worker {}", 
                                      currentTask.getWorkflowId(), worker.getWorkerId());
                        }
                    });
            }
            
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Interrupted while waiting for tasks to complete");
                break;
            }
        }
        
        // Log if waiting timed out
        long runningTasks = workers.stream()
            .mapToLong(worker -> worker.isProcessingTask() ? 1 : 0)
            .sum();
        
        if (runningTasks > 0) {
            logger.warn("Timed out waiting for {} tasks to complete after {} seconds", 
                       runningTasks, shutdownTimeoutSeconds);
        }
    }
    
    public void submitTask(WorkflowTask task) {
        if (!running) {
            throw new IllegalStateException("Workflow service is not running");
        }
        taskQueue.offer(task);
    }
    
    public WorkflowServiceStatus getStatus() {
        if (!running) {
            return new WorkflowServiceStatus(0, 0, true, false);
        }
        
        long runningTasks = workers.stream()
            .mapToLong(worker -> worker.isProcessingTask() ? 1 : 0)
            .sum();
        
        return new WorkflowServiceStatus(numberOfWorkers, runningTasks, taskQueue.isEmpty(), running);
    }
}
