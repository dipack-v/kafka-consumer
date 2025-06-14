package com.example.kafka_consumer.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class EventListener {
   // @KafkaListener(topics = "events", groupId = "kafka_consumer_app")
    public void listenGroupFoo(String message) {
        System.out.println("Received Message in group foo: " + message);
    }
}
