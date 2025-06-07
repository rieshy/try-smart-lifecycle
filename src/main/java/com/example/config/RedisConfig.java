package com.example.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;

@Configuration
@PropertySource("classpath:application.properties")
public class RedisConfig implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    
    private ClientResources clientResources;
    private LettuceConnectionFactory connectionFactory;
    private volatile boolean destroyed = false;
    
    @Bean
    public ClientResources clientResources() {
        clientResources = DefaultClientResources.builder()
            .ioThreadPoolSize(4)
            .computationThreadPoolSize(4)
            .build();
        return clientResources;
    }
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            @Value("${redis.host:localhost}") String host,
            @Value("${redis.port:6379}") int port,
            ClientResources clientResources) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        
        ClientOptions clientOptions = ClientOptions.builder()
            .autoReconnect(false)
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
            .build();
            
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .clientOptions(clientOptions)
            .clientResources(clientResources)
            .build();
            
        connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
        return connectionFactory;
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    
    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        
        logger.info("Shutting down Redis resources...");
        
        // First, clean up Netty ThreadLocal resources
        try {
            FastThreadLocal.removeAll();
            InternalThreadLocalMap.destroy();
            logger.info("Cleaned up Netty ThreadLocal resources");
        } catch (Exception e) {
            logger.warn("Error cleaning up Netty ThreadLocal resources", e);
        }
        
        // Then shut down client resources
        try {
            if (clientResources != null) {
                clientResources.shutdown().get();
                logger.info("Lettuce client resources shut down");
            }
        } catch (Exception e) {
            logger.warn("Error shutting down Lettuce client resources", e);
        }
    }
}
