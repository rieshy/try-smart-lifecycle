package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;   

@ComponentScan(basePackages = {
    "com.example.config",
    "com.example.workflow",
})
@PropertySource("classpath:application.properties")
@Configuration
public class AppConfig {
    public static final String APP_NAME = "try-smart-lifecycle";
}
