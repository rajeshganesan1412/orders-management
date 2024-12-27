package com.order.management.producer;

import com.order.management.model.Orders;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageProducer.class);
    private final KafkaTemplate<String, Orders> kafkaTemplate;

    @Value("${order.message.topic}")
    private String orderMessageTopic;

    public void sendMessage(Orders orders) {
        ProducerRecord<String, Orders> record = new ProducerRecord<>(orderMessageTopic, orders);
        kafkaTemplate.send(record)
                .toCompletableFuture()
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish message to the kafka topic");
                        throw new KafkaException(ex.getMessage());
                    }
                })
                .join();
    }

}
