package com.microservice.notification.notification_service.consumers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.microservice.notification.notification_service.constants.NotificationConstants;
import com.microservice.notification.notification_service.constants.NotificationStatusEnum;
import com.microservice.notification.notification_service.dao.NotificationRepository;
import com.microservice.notification.notification_service.entities.NotificationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consumer service for processing SMS notifications from Kafka.
 * <p>
 * This service listens to the configured Kafka topic for SMS notifications,
 * validates the incoming messages, and updates the notification status in the database.
 * </p>
 */
@Service
public class SmsConsumer {

    private static final Logger logger = LogManager.getLogger(SmsConsumer.class);
    private final Gson gson = new Gson();

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Kafka listener for processing SMS messages from the Kafka topic.
     *
     * @param message The raw Kafka message in JSON format.
     */
    @KafkaListener(topics = NotificationConstants.SMS_KAFKA_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        logger.info("Received message from Kafka: {}", message);

        try {
            // Deserialize the JSON message into a NotificationRequest object
            NotificationRequest notificationRequest = gson.fromJson(message, NotificationRequest.class);

            // Validate the message content
            if (validate(notificationRequest)) {
                // Update the notification status to "DELIVERED"
                updateNotificationStatus(notificationRequest.getIdentifier(), NotificationStatusEnum.DELEIVERED.name());
                logger.info("Notification status updated to DELIVERED for identifier: {}", notificationRequest.getIdentifier());
            } else {
                logger.warn("Validation failed for message: {}", message);
            }
        } catch (JsonSyntaxException e) {
            logger.error("Invalid message format received: {}", message, e);
        } catch (Exception e) {
            logger.error("Unexpected error while processing message: {}", message, e);
        }
    }

    /**
     * Validates the NotificationRequest object to ensure all required fields are present.
     *
     * @param notificationRequest The deserialized NotificationRequest object.
     * @return true if the request is valid; false otherwise.
     */
    private boolean validate(NotificationRequest notificationRequest) {
        if (notificationRequest == null) {
            logger.error("Validation failed: NotificationRequest is null");
            return false;
        }

        if (notificationRequest.getIdentifier() == null || notificationRequest.getIdentifier().isEmpty()) {
            logger.error("Validation failed: Identifier is missing");
            return false;
        }

        if (notificationRequest.getNotificationType() == null || notificationRequest.getNotificationType().isEmpty()) {
            logger.error("Validation failed: Notification type is missing");
            return false;
        }

        if ((notificationRequest.getPhone() == null || notificationRequest.getPhone().isEmpty()) &&
                (notificationRequest.getEmail() == null || notificationRequest.getEmail().isEmpty())) {
            logger.error("Validation failed: Recipient information (phone/email) is missing");
            return false;
        }

        return true;
    }

    /**
     * Updates the notification status in the database.
     * <p>
     * If the notification with the given identifier is not found, logs a warning.
     * </p>
     *
     * @param identifier The unique identifier of the notification.
     * @param status     The new status to set for the notification (e.g., DELIVERED).
     */
    private void updateNotificationStatus(String identifier, String status) {
        notificationRepository.findByIdentifier(identifier).ifPresentOrElse(notification -> {
            notification.setStatus(status);
            notificationRepository.save(notification);
            logger.info("Status updated successfully for identifier: {}", identifier);
        }, () -> {
            logger.warn("Notification with identifier {} not found in the database.", identifier);
        });
    }
}
