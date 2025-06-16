package com.example.kafka_consumer.controller;

import com.example.kafka_consumer.config.KafkaClusterProperties;
import com.example.kafka_consumer.controller.dto.LabelValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConsumerController {
    private final KafkaClusterProperties kafkaClusterProperties;
    private final ReceiverOptions<String, String> receiverOptions;

    @CrossOrigin(origins = "http://localhost:5173/")
    @GetMapping(value = "/topics")
    public List<LabelValue> getTopics() {
        return kafkaClusterProperties.getConsumer().getTopics().stream().map(topic -> new LabelValue(topic, topic)).toList();
    }

    @CrossOrigin(origins = "http://localhost:5173/")
    @GetMapping(value = "/stream/{topic}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> consumeRecord(@PathVariable(value="topic") String topic) {
        log.info("Consuming topic: {}", topic);
        ReceiverOptions<String, String> options = receiverOptions.subscription(Collections.singleton(topic))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));

        Flux<ReceiverRecord<String, String>> kafkaFlux = KafkaReceiver.create(options).receive();

        return kafkaFlux
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
