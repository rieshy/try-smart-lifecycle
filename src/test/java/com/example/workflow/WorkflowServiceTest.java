package com.example.workflow;

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
    public void startWorkflow() throws InterruptedException {
        StartWorkflowTask task = new StartWorkflowTask("Start workflow 123");
        workflowService.submitTaskAndWait(task).await();
    }
} 