package com.example.workflow;

import com.example.config.AppConfig;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component("redisWorkflowMessageBroker")
public class RedisWorkflowMessageBroker implements WorkflowMessageBroker {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    public RedisWorkflowMessageBroker(RedisTemplate<String, Object> redisTemplate, RedisMessageListenerContainer redisMessageListenerContainer) {
        this.redisTemplate = redisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    @Override
    public CountDownLatch listenForTaskDone(WorkflowTask task) {
        if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        }
        String channel = AppConfig.APP_NAME + ":workflow:done:" + task.getId();
        CountDownLatch latch = new CountDownLatch(1);

        ChannelTopic topic = new ChannelTopic(channel);
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                if (message.toString().equals("done")) {
                    latch.countDown();
                    redisMessageListenerContainer.removeMessageListener(this, topic);
                }
            }
        };
        redisMessageListenerContainer.addMessageListener(messageListener, topic);

        return latch;
    }

    @Override
    public void publishTaskDone(WorkflowTask task) {
        if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        }
        String channel = AppConfig.APP_NAME + ":workflow:done:" + task.getId();
        redisTemplate.convertAndSend(channel, "done");
    }
}
