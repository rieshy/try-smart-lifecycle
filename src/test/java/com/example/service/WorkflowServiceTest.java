package com.example.service;

import com.example.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringJUnitConfig(classes = {AppConfig.class})
@WebAppConfiguration
public class WorkflowServiceTest {

    @Autowired
    private WorkflowService workflowService;

    @Test
    public void startWorkflow() throws Exception {
        WorkflowStartTask task = new WorkflowStartTask();
        task.setWorkflowId("123");
        task.setAction(WorkflowAction.START);
        task.setDescription("Start workflow 123");
        workflowService.submitTask(task);
        workflowService.waitUntilIdle(30);
    }

    @Test
    public void submitTaskAndWait_blocksUntilTaskProcessed() throws Exception {
        WorkflowStartTask task = new WorkflowStartTask();
        task.setWorkflowId("456");
        task.setAction(WorkflowAction.START);
        task.setDescription("Start workflow 456");
        long start = System.currentTimeMillis();
        workflowService.submitTaskAndWait(task);
        long duration = System.currentTimeMillis() - start;
        // The task sleeps for 10 seconds in execute(), so duration should be >= 10_000 ms
        assert duration >= 9000 : "Task did not block for expected time";
    }
} 