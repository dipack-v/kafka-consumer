package com.example.kafka_consumer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConsumerController {
    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> consumeRecord() {
        return reactiveKafkaConsumerTemplate.receive()
                .map(ReceiverRecord::value)
                .doOnNext(msg -> log.info("Received: {}", msg))
                .doOnError(error -> log.error("Consumer error: {}", error.getMessage()));
    }
}
