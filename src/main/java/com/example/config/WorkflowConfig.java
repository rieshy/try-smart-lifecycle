package com.example.config;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import com.example.service.WorkflowTaskQueue;

@Configuration
public class WorkflowConfig {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowConfig.class);

    @Autowired(required = false)
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    @Primary
    public WorkflowTaskQueue workflowTaskQueue(@Qualifier("redisWorkflowTaskQueue") WorkflowTaskQueue redisWorkflowTaskQueue, 
                                               @Qualifier("inMemoryWorkflowTaskQueue") WorkflowTaskQueue inMemoryWorkflowTaskQueue) {
        if (isRedisAvailable()) {
            logger.info("Using Redis for workflow task queue");
            return redisWorkflowTaskQueue;
        }
        logger.info("Using in-memory for workflow task queue");
        return inMemoryWorkflowTaskQueue;
    }

    private boolean isRedisAvailable() {
        try {
            String response = lettuceConnectionFactory.getConnection().ping();
            return Objects.equals("PONG", response);
        } catch (Exception e) {
            return false;
        }
    }
}
