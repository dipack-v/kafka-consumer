package com.example.kafka_consumer.controller;

import com.example.kafka_consumer.config.KafkaClusterProperties;
import com.example.kafka_consumer.controller.dto.LabelValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConsumerController {
    private final KafkaClusterProperties kafkaClusterProperties;
    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;

    @CrossOrigin(origins = "http://localhost:5173/")
    @GetMapping(value = "/topics")
    public List<LabelValue> getTopics() {
        return kafkaClusterProperties.getConsumer().getTopics().stream().map(topic -> new LabelValue(topic, topic)).toList();
    }

    @CrossOrigin(origins = "http://localhost:5173/")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> consumeRecord() {
        return reactiveKafkaConsumerTemplate.receive()
                .doOnSubscribe(s -> log.info("Subscribed to Kafka consumer"))
                .map(record -> {
                    String message = record.value();
                    record.receiverOffset().acknowledge();
                    return message;
                })
                .doOnNext(msg -> log.info("Received: {}", msg))
                .doOnCancel(() -> log.info("Consumer cancelled"))
                .doOnError(error -> log.error("Consumer error: {}", error.getMessage()));
    }
}
