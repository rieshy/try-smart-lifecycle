package com.example.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.WorkflowTask;

public class WorkflowWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowWorker.class);

    private final WorkflowTaskQueue taskQueue;
    private final String workerId;
    private volatile boolean shutdown = false;
    private volatile boolean processingTask = false;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private final AtomicReference<WorkflowTask> currentTask = new AtomicReference<>();

    // Adaptive polling configuration
    private static final long INITIAL_POLL_TIMEOUT = 1000; // 1 second
    private static final long MAX_POLL_TIMEOUT = 30000;    // 30 seconds
    private long currentPollTimeout = INITIAL_POLL_TIMEOUT;

    public WorkflowWorker(WorkflowTaskQueue taskQueue, String workerId) {
        this.taskQueue = taskQueue;
        this.workerId = workerId;
    }
    
    @Override
    public void run() {
        logger.info("Worker {} started", workerId);
        
        while (!shutdown) {
            try {
                // Only poll if not shutting down
                if (!shutdown) {
                    WorkflowTask task = taskQueue.poll(currentPollTimeout, TimeUnit.MILLISECONDS);
                    
                    if (task != null) {
                        // Check again if shutdown was requested while polling
                        if (shutdown) {
                            logger.info("Worker {} received shutdown signal, not processing new task {}", workerId, task.getWorkflowId());
                            break;
                        }
                        
                        // Reset poll timeout when task is found
                        currentPollTimeout = INITIAL_POLL_TIMEOUT;
                        processTask(task);
                    } else {
                        // Gradually increase poll timeout to reduce CPU usage
                        adaptPollTimeout();
                    }
                }
                
            } catch (InterruptedException e) {
                // Handle interruption gracefully - check if we're processing a task
                if (processingTask) {
                    logger.warn("Worker {} interrupted while processing task, allowing current task to complete", workerId);
                    // Don't break the loop immediately, let current task finish
                    // Set shutdown flag so we don't pick up new tasks
                    shutdown = true;
                } else {
                    logger.info("Worker {} interrupted while waiting for tasks", workerId);
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                logger.error("Error in worker {}", workerId, e);
            }
        }
        
        logger.info("Worker {} stopped", workerId);
        shutdownLatch.countDown();
    }

    private void processTask(WorkflowTask task) {
        processingTask = true;
        currentTask.set(task);
        
        try {
            logger.debug("Worker {} processing task {}", workerId, task.getWorkflowId());
            task.execute();
            logger.debug("Worker {} completed task {}", workerId, task.getWorkflowId());
        } catch (Exception e) {
            logger.error("Worker {} failed to process task {}", workerId, task.getWorkflowId(), e);
        } finally {
            processingTask = false;
            currentTask.set(null);
        }
    }

    private void adaptPollTimeout() {
        // Exponential backoff with cap
        currentPollTimeout = Math.min(currentPollTimeout * 2, MAX_POLL_TIMEOUT);
    }

    public void shutdown() {
        logger.info("Worker {} received shutdown signal", workerId);
        shutdown = true;
    }
    
    public boolean isProcessingTask() {
        return processingTask;
    }
    
    public WorkflowTask getCurrentTask() {
        return currentTask.get();
    }
    
    public String getWorkerId() {
        return workerId;
    }
    
    public boolean awaitShutdown(long timeout, TimeUnit unit) throws InterruptedException {
        return shutdownLatch.await(timeout, unit);
    }
}
