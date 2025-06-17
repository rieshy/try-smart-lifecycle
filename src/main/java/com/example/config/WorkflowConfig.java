package com.example.config;

import com.example.workflow.WorkflowMessageBroker;
import com.example.workflow.WorkflowTaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Objects;

@Configuration
public class WorkflowConfig {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowConfig.class);

    private final LettuceConnectionFactory lettuceConnectionFactory;

    public WorkflowConfig(LettuceConnectionFactory lettuceConnectionFactory) {
        this.lettuceConnectionFactory = lettuceConnectionFactory;
    }

    @Bean
    @Primary
    public WorkflowTaskQueue workflowTaskQueue(@Qualifier("redisWorkflowTaskQueue") WorkflowTaskQueue redisWorkflowTaskQueue,
                                               @Qualifier("localWorkflowTaskQueue") WorkflowTaskQueue localWorkflowTaskQueue) {
        if (isRedisAvailable()) {
            logger.info("Using Redis for workflow task queue");
            return redisWorkflowTaskQueue;
        }
        logger.info("Using local memory for workflow task queue");
        return localWorkflowTaskQueue;
    }

    @Bean
    @Primary
    public WorkflowMessageBroker workflowMessageBroker(@Qualifier("redisWorkflowMessageBroker") WorkflowMessageBroker redisWorkflowMessageBroker,
                                                       @Qualifier("localWorkflowMessageBroker") WorkflowMessageBroker localWorkflowMessageBroker) {
        if (isRedisAvailable()) {
            logger.info("Using Redis for workflow message broker");
            return redisWorkflowMessageBroker;
        }
        logger.info("Using local memory for workflow message broker");
        return localWorkflowMessageBroker;
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
