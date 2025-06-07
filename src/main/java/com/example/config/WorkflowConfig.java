package com.example.config;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.service.InMemoryWorkflowTaskQueue;
import com.example.service.RedisWorkflowTaskQueue;
import com.example.service.WorkflowTaskQueue;

@Configuration
public class WorkflowConfig {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowConfig.class);

    @Bean
    public WorkflowTaskQueue workflowTaskQueue(LettuceConnectionFactory connectionFactory, RedisTemplate<String, Object> redisTemplate) {
        if (isRedisAvailable(connectionFactory)) {
            logger.info("Using Redis for workflow task queue");
            return new RedisWorkflowTaskQueue(redisTemplate);
        }
        logger.info("Using in-memory for workflow task queue");
        return new InMemoryWorkflowTaskQueue();
    }

    private boolean isRedisAvailable(LettuceConnectionFactory connectionFactory) {
        try {
            String response = connectionFactory.getConnection().ping();
            return Objects.equals("PONG", response);
        } catch (Exception e) {
            return false;
        }
    }
}
