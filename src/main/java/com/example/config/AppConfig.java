package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;   

@ComponentScan(basePackages = {
    "com.example.service",
    "com.example.config"
})
@PropertySource("classpath:application.properties")
@Configuration
public class AppConfig {

}
