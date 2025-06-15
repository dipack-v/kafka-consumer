package com.example.kafka_consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("kafka")
public class KafkaClusterProperties {
    String bootstrapServers;
    String username;
    String password;
    KafkaConsumerProperties consumer;

}
