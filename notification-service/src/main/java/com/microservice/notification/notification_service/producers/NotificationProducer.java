package com.microservice.notification.notification_service.producers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for sending messages to a Kafka topic.
 * It uses a KafkaTemplate to send messages and logs the success of the operation.
 */
@Component
public class NotificationProducer {

    private static final Logger logger = LogManager.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Constructor for NotificationProducer.
     *
     * @param kafkaTemplate The KafkaTemplate used to send messages to Kafka topics.
     */
    public NotificationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a message to the specified Kafka topic.
     *
     * @param message   The message to be sent to the Kafka topic.
     * @param TOPIC_NAME The name of the Kafka topic to send the message to.
     */
    public void sendMessage(String message, String TOPIC_NAME) {
        kafkaTemplate.send(TOPIC_NAME, message);  // Send message to the Kafka topic
        logger.info("Message '{}' has been successfully sent to the topic: {}", message, TOPIC_NAME);  // Log the success of the operation
    }
}
