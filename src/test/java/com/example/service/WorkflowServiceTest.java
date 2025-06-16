package com.example.service;

import com.example.config.AppConfig;
import com.example.model.WorkflowTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

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
} 