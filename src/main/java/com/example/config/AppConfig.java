package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@ComponentScan(basePackages = {"com.example.service"})
@PropertySource("classpath:application.properties")
@Configuration
@Import({RedisConfig.class, WorkflowConfig.class})
public class AppConfig {
    
}
