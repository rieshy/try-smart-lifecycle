package com.example.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WorkflowWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowWorker.class);

    private final WorkflowTaskQueue taskQueue;
    private final String workerId;
    private volatile boolean shutdown = false;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private final AtomicReference<WorkflowTask> currentTask = new AtomicReference<>();
    private final WorkflowMessageBroker messageBroker;

    // Adaptive polling configuration
    private static final long INITIAL_POLL_TIMEOUT = 1000; // 1 second
    private static final long MAX_POLL_TIMEOUT = 30000;    // 30 seconds
    private long currentPollTimeout = INITIAL_POLL_TIMEOUT;

    public WorkflowWorker(WorkflowTaskQueue taskQueue, String workerId, WorkflowMessageBroker messageBroker) {
        this.taskQueue = taskQueue;
        this.workerId = workerId;
        this.messageBroker = messageBroker;
    }

    @Override
    public void run() {
        logger.info("Worker {} started", workerId);

        while (!shutdown) {
            try {
                // Only poll if not shutting down
                if (!shutdown) {
                    currentTask.set(taskQueue.poll(currentPollTimeout, TimeUnit.MILLISECONDS));
                    if (currentTask.get() != null) {
                        // Check again if shutdown was requested while polling
                        if (shutdown) {
                            logger.info("Worker {} received shutdown signal, returning unprocessed task {} to the queue", workerId, currentTask.get());
                            taskQueue.offer(currentTask.get());
                            break;
                        }

                        // Reset poll timeout when the task is received
                        currentPollTimeout = INITIAL_POLL_TIMEOUT;
                        processTask(currentTask.get());
                        currentTask.set(null);
                    } else {
                        // Gradually increase poll timeout to reduce CPU usage
                        adaptPollTimeout();
                    }
                }

            } catch (InterruptedException e) {
                // Handle interruption gracefully - check if we're processing a task
                if (currentTask.get() != null) {
                    logger.warn("Worker {} got interrupted while processing task, allowing current task to complete", workerId);
                    // Don't break the loop immediately, let the current task finish
                    // Set shutdown flag so we don't pick up new tasks
                    shutdown = true;
                } else {
                    logger.info("Worker {} got interrupted while waiting for tasks", workerId);
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
        try {
            logger.info("Worker {} processing task '{}'", workerId, task);
            task.execute();
            logger.info("Worker {} completed task '{}'", workerId, task);
        } catch (Exception e) {
            logger.error("Worker {} failed to process task '{}'", workerId, task, e);
        } finally {
            messageBroker.publishTaskDone(task);
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
        return currentTask.get() != null;
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
