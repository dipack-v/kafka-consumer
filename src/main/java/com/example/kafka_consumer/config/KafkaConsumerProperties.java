package com.example.kafka_consumer.config;

import java.util.Properties;

import lombok.Data;

@Data
public class KafkaConsumerProperties {
    
    String groupId;
    Properties properties;

}
