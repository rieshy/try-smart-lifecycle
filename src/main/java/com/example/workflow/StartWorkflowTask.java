package com.example.workflow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StartWorkflowTask extends BaseWorkflowTask {
    private static final Logger logger = LogManager.getLogger();

    public StartWorkflowTask() {
        super();
    }

    public StartWorkflowTask(String description) {
        super(description);
    }

    @Override
    public void execute() {
        logger.info("Executing task: {}", this);
        try {
            for (int i = 0; i < 10; i++) {
                logger.info("Executing step {} of task '{}'", i, this);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Task got interrupted");
        }
    }

    @Override
    public String toString() {
        return "StartWorkflowTask{" +
                "id='" + getId() + '\'' +
                ", description='" + getDescription() + '\'' +
                '}';
    }
}
