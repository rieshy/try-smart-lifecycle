package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@ComponentScan(basePackages = {"com.example.service"})
@PropertySource("classpath:application.properties")
@Configuration
@Import({RedisConfig.class, WorkflowConfig.class})
public class AppConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("lifecycle-");
    }

    @Bean(name = "lifecycleProcessor")
    public DefaultLifecycleProcessor lifecycleProcessor(TaskExecutor taskExecutor) {
        DefaultLifecycleProcessor processor = new DefaultLifecycleProcessor();
        processor.setTaskExecutor(taskExecutor);
        // Set a default shutdown timeout of 30 seconds
        processor.setTimeoutPerShutdownPhase(30_000);
        return processor;
    }
}
