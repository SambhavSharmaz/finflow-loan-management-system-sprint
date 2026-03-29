package com.capgemini.documentservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue documentUploadQueue() {
        return new Queue("documentUploadQueue", false);
    }
}
