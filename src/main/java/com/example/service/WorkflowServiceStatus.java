package com.example.service;

public class WorkflowServiceStatus {
    private final int totalWorkers;
    private final long runningTasks;
    private final boolean queueEmpty;
    private final boolean processorRunning;
    
    public WorkflowServiceStatus(int totalWorkers, long runningTasks, boolean queueEmpty, boolean processorRunning) {
        this.totalWorkers = totalWorkers;
        this.runningTasks = runningTasks;
        this.queueEmpty = queueEmpty;
        this.processorRunning = processorRunning;
    }
    
    public int getTotalWorkers() { return totalWorkers; }
    public long getRunningTasks() { return runningTasks; }
    public boolean isQueueEmpty() { return queueEmpty; }
    public boolean isProcessorRunning() { return processorRunning; }
    
    @Override
    public String toString() {
        return String.format("TaskProcessor[running=%s, workers=%d, activeTasks=%d, queueEmpty=%s]", 
                           processorRunning, totalWorkers, runningTasks, queueEmpty);
    }
}
